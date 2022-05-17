package be.machigan.mutilities.task;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.UUID;

public class CommandTask implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("task.enabled")) {
            return true;
        }

        if (!commandSender.hasPermission("mutils.task.use")) {
            commandSender.sendMessage(Tools.configColor("task.message.no perm.task use").replace("{prefix}", Task.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.task.use"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(Tools.configColor("task.message.miss argument").replace("{prefix}", Task.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }

        if (strings[0].equalsIgnoreCase("me")) {
            if (!commandSender.hasPermission("mutils.task.me")) {
                commandSender.sendMessage(Tools.configColor("task.message.no perm.task me").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.task.me"));
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.me.only player").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Task.FILE);
            if (config.get("all player." + ((Player) commandSender).getUniqueId()) == null) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.me.not participate").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            commandSender.sendMessage(Tools.configColor("task.message.arg.me.success").replace("{prefix}", Task.PREFIX)
                    .replace("{sender}", commandSender.getName())
                    .replace("{total win}", Integer.toString(config.getInt("all player." + ((Player) commandSender).getUniqueId() + ".total")))
                    .replace("{avg time}", Double.toString(config.getDouble("all player." + ((Player) commandSender).getUniqueId() + ".average"))));
            return true;
        }

        if (strings[0].equalsIgnoreCase("info")) {
            if (!commandSender.hasPermission("mutils.task.info")) {
                commandSender.sendMessage(Tools.configColor("task.message.no perm.task info").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.task.info"));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.info.miss argument").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            String uuid;
            try {
                uuid = Tools.getUUID(strings[1]);
            } catch (IOException | ParseException ignored) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.info.not found").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[1]));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Task.FILE);
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if (config.get("all player." + uuid) == null) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.info.not participate").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", player.getName()));
                return true;
            }
            commandSender.sendMessage(Tools.configColor("task.message.arg.info.success").replace("{prefix}", Task.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{player}", player.getName())
                    .replace("{total win}", Integer.toString(config.getInt("all player." + uuid + ".total")))
                    .replace("{avg time}", Double.toString(config.getDouble("all player." + uuid + ".average"))));
            return true;
        }

        if (strings[0].equalsIgnoreCase("start")) {
            if (!commandSender.hasPermission("mutils.task.start")) {
                commandSender.sendMessage(Tools.configColor("task.message.no perm.task start").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.task.start"));
                return true;
            }
            if (Task.inGame) {
                commandSender.sendMessage(Tools.configColor("task.message.arg.start.already task").replace("{prefix}", Task.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            Task.newTask();
            commandSender.sendMessage(Tools.configColor("task.message.arg.start.success").replace("{prefix}", Task.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }



        commandSender.sendMessage(Tools.configColor("task.message.not found").replace("{prefix}", Task.PREFIX)
                .replace("{sender}", commandSender.getName()));
        return true;
    }
}
