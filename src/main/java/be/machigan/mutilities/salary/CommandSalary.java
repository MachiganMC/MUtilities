package be.machigan.mutilities.salary;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public class CommandSalary implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("salary.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.salary.use")) {
            commandSender.sendMessage(Tools.configColor("salary.message.no perm.salary use").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.salary.use"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(Tools.configColor("salary.message.miss argument").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }

        if (!Salary.SALARIES_LIST.contains(strings[0])) {
            commandSender.sendMessage(Tools.configColor("salary.message.no exist").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]));
            return true;
        }
        if (strings.length < 2) {
            commandSender.sendMessage(Tools.configColor("salary.message.no action").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", command.getName()).replace("{arg}", strings[0]));
            return true;
        }
        if (strings[1].equalsIgnoreCase("add")) {
            if (!commandSender.hasPermission("mutils.salary.add")) {
                commandSender.sendMessage(Tools.configColor("salary.message.no perm.salary use").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.salary.add")
                        .replace("{arg}", strings[0]).replace("{arg2}", strings[1]));
                return true;
            }
            if (strings.length < 3) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.add.no player").replace("{prefix", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1]));
                return true;
            }
            String uuid;
            try {
                uuid = Tools.getUUID(strings[2]);
            } catch (IOException | ParseException ignored) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.add.player not found").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", strings[2]));
                return true;
            }

            FileConfiguration config = YamlConfiguration.loadConfiguration(Salary.FILE);
            if (config.getStringList(strings[0]).contains(uuid)) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.add.already in").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
                return true;
            }
            List<String> allPlayer = config.getStringList(strings[0]);
            allPlayer.add(uuid);
            config.set(strings[0], allPlayer);
            try {
                config.save(Salary.FILE);
            } catch (IOException e) {
                commandSender.sendMessage(Tools.replaceColor("salary.message.arg.add.intern error").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
                return true;
            }
            commandSender.sendMessage(Tools.configColor("salary.message.arg.add.success").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                    .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
            return true;

        }

        if (strings[1].equalsIgnoreCase("remove")) {
            if (!commandSender.hasPermission("mutils.salary.remove")) {
                commandSender.sendMessage(Tools.configColor("salary.message.no perm.salary remove").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.salary.remove")
                        .replace("{arg}", strings[0]).replace("{arg2}", strings[1]));
                return true;
            }
            if (strings.length < 3) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.remove.no player").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[2]));
                return true;
            }

            String uuid;
            try {
                uuid = Tools.getUUID(strings[2]);
            } catch (IOException | ParseException e) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.remove.player not found").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", strings[2]));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Salary.FILE);
            List<String> allPlayer = config.getStringList(strings[0]);
            if (!allPlayer.contains(uuid)) {
                commandSender.sendMessage(Tools.configColor("salary.message.arg.remove.already out").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
                return true;
            }
            allPlayer.remove(uuid);
            config.set(strings[0], allPlayer);
            try {
                config.save(Salary.FILE);
            } catch (IOException ignored) {
                commandSender.sendMessage(Tools.replaceColor("salary.message.arg.remove.intern error").replace("{prefix}", Salary.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                        .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
                return true;
            }
            commandSender.sendMessage(Tools.configColor("salary.message.arg.remove.success").replace("{prefix}", Salary.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1])
                    .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
            return true;
        }

        commandSender.sendMessage(Tools.configColor("salary.message.bad argument").replace("{prefix}", Salary.PREFIX)
                .replace("{sender}", commandSender.getName()).replace("{arg}", strings[0]).replace("{arg2}", strings[1]));
        return true;
    }
}
