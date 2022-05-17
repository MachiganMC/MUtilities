package be.machigan.mutilities.colorname;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CommandColorname implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("colorname.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.colorname.use")) {
            commandSender.sendMessage(Tools.configColor("colorname.message.no perm.colorname use").replace("{prefix}", Colorname.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.colorname.use"));
            return true;
        }
        if (strings.length < 1) {
            commandSender.sendMessage(Tools.configColor("colorname.message.miss argument").replace("{prefix}", Colorname.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }

        if (strings[0].equalsIgnoreCase("me")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.only player").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!commandSender.hasPermission("mutils.colorname.me")) {
                commandSender.sendMessage(Tools.configColor("colorname.message.no perm.colorname me").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.colorname.me"));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.miss argument").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings[1].equalsIgnoreCase("reset")) {
                Colorname.reset((Player) commandSender);
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.reset.success").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{display name}", ((Player) commandSender).getDisplayName()));
                return true;
            }
            if (strings[1].equalsIgnoreCase("set")) {
                if (strings.length < 3) {
                    commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.set.miss argument").replace("{prefix}", Colorname.PREFIX)
                            .replace("{sender}", command.getName()));
                    return true;
                }

                boolean alreadyColor = false;
                List<Character> code = new ArrayList<>();
                for (Character c : strings[2].toCharArray()) {
                    if ((!Colorname.COLOR_LIST.contains(c)) && (!Colorname.FORMAT_LIST.contains(c))) {
                        commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.set.bad code").replace("{prefix}", Colorname.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{code}", c.toString()));
                        return true;
                    }
                    if (!code.contains(c)) {
                        if (Colorname.COLOR_LIST.contains(c)) {
                            if (alreadyColor) {
                                continue;
                            }
                            alreadyColor = true;
                        }
                        code.add(c);
                    }
                }
                Colorname.set((Player) commandSender, Colorname.charListToColor(code));
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.me.set.success").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{code}", strings[2])
                        .replace("{display name}", ((Player) commandSender).getDisplayName()));
                return true;
            }

        }

        if (commandSender.hasPermission("mutils.colorname.other")) {
            String uuid;
            try {
                uuid = Tools.getUUID(strings[0]);
            } catch (IOException | ParseException e) {
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.player not found").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", strings[0]));
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.miss argument").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", player.getName()));
                return true;
            }

            if (strings[1].equalsIgnoreCase("reset")) {
                Colorname.reset((Player) player);
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.reset.success").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", player.getName())
                        .replace("{display name}", ((Player) player).getDisplayName()));
                return true;
            }

            if (strings[1].equalsIgnoreCase("set")) {
                if (strings.length < 3) {
                    commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.set.miss argument").replace("{prefix}", Colorname.PREFIX)
                            .replace("{sender}", commandSender.getName()).replace("{player}", player.getName()));
                    return true;
                }
                boolean alreadyColor = false;
                List<Character> code = new ArrayList<>();
                for (Character c : strings[2].toCharArray()) {
                    if ((!Colorname.COLOR_LIST.contains(c)) && (!Colorname.FORMAT_LIST.contains(c))) {
                        commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.set.bad code").replace("{prefix}", Colorname.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{code}", c.toString()));
                        return true;
                    }
                    if (!code.contains(c)) {
                        if (Colorname.COLOR_LIST.contains(c)) {
                            if (alreadyColor) {
                                continue;
                            }
                            alreadyColor = true;
                        }
                        code.add(c);
                    }
                }
                if (player.isOnline()) {
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        if (p.getUniqueId().equals(player.getUniqueId())) {
                            Colorname.set(p, Colorname.charListToColor(code));
                            break;
                        }
                    }
                }
                commandSender.sendMessage(Tools.configColor("colorname.message.arg.other.set.success").replace("{prefix}", Colorname.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", player.getName())
                        .replace("{code}", strings[2])
                        .replace("{display name}", (Colorname.charListToColor(code) + player.getName() + ChatColor.RESET)));
                return true;

            }

        }



        commandSender.sendMessage(Tools.configColor("colorname.message.bad argument").replace("{prefix}", Colorname.PREFIX)
                .replace("{sender}", commandSender.getName()));
        return true;
    }
}
