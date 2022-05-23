package be.machigan.mutilities.command;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandMutils implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {


        if (!commandSender.hasPermission("mutils.command.use")) {
            commandSender.sendMessage(Tools.configColor("mutils.message.no perm.command use").replace("{prefix}", Const.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.command.use"));
            return true;
        }
        if (strings.length == 0) {
            commandSender.sendMessage(Tools.configColor("mutils.message.miss argument").replace("{prefix}", Const.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }

        if (strings[0].equalsIgnoreCase("prefix")) {
            if (!commandSender.hasPermission("mutils.command.prefix")) {
                commandSender.sendMessage(Tools.configColor("mutils.message.no perm.command prefix").replace("{prefix}", Const.PREFIX)
                        .replace("{perm}", "mutils.command.prefix"));
                return true;
            }
            if (strings.length <= 1) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.prefix.miss argument").replace("{prefix}", Const.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!MUtilities.LIST_MODULE.contains(strings[1].toLowerCase())) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.prefix.no exist").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1]));
                return true;
            }
            if (strings.length <= 2) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.prefix.no prefix").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1]));
                return true;
            }
            Const.CONFIG.set(strings[1].toLowerCase() + ".prefix", strings[2]);

            MUtilities.getInstance().saveConfig();
            MUtilities.updateConfig();
            commandSender.sendMessage(Tools.configColor("mutils.message.arg.prefix.success").replace("{prefix}", Const.PREFIX)
                    .replace("{arg}", strings[1]).replace("{new prefix}", Tools.replaceColor(strings[2])));
            return true;

        }

        if (strings[0].equalsIgnoreCase("reloadconfig")) {
            if (!commandSender.hasPermission("mutils.command.reloadconfig")) {
                commandSender.sendMessage(Tools.configColor("mutils.message.no perm.command reloadconfig").replace("{prefix}", Const.PREFIX)
                        .replace("{perm}", "mutils.message.no perm.command reloadconfig"));
            }
            MUtilities.updateConfig();
            commandSender.sendMessage(Tools.configColor("mutils.message.arg.reloadconfig.success").replace("{prefix}", Const.PREFIX));
            return true;
        }

        if (strings[0].equalsIgnoreCase("enable")) {
            if (!commandSender.hasPermission("mutils.command.enable")) {
                commandSender.sendMessage(Tools.configColor("mutils.message.no perm.command enable").replace("{prefi}", Const.PREFIX)
                        .replace("{perm}", "mutils.command.enable"));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.enable.miss argument").replace("{prefix}", Const.PREFIX));
                return true;
            }
            if ((!MUtilities.LIST_MODULE.contains(strings[1].toLowerCase())) && (!strings[1].equalsIgnoreCase("*"))) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.enable.no exist").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1]));
                return true;
            }
            if (strings[1].equalsIgnoreCase("*")) {
                for (String module : MUtilities.LIST_MODULE) {
                    if (!Const.CONFIG.getBoolean(module + ".enabled")) {
                        Const.CONFIG.set(module + ".enabled", true);
                        commandSender.sendMessage(Tools.configColor("mutils.message.arg.enable.success").replace("{prefix}", Const.PREFIX)
                                .replace("{arg}", module));
                    }
                }
            } else {
                Const.CONFIG.set(strings[1].toLowerCase() + ".enabled", true);
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.enable.success").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1].toLowerCase()));
            }
            MUtilities.getInstance().saveConfig();
            MUtilities.updateConfig();

            return true;
        }

        if (strings[0].equalsIgnoreCase("disable")) {
            if (!commandSender.hasPermission("mutils.command.disable")) {
                commandSender.sendMessage(Tools.configColor("mutils.message.no perm.command disable").replace("{prefix}", Const.PREFIX)
                        .replace("{perm}", "mutils.command.disable"));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.disable.miss argument").replace("{prefix}", Const.PREFIX));
                return true;
            }
            if ((!MUtilities.LIST_MODULE.contains(strings[1].toLowerCase())) && (!strings[1].equalsIgnoreCase("*"))) {
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.disable.no exist").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1]));
                return true;
            }

            if (strings[1].equalsIgnoreCase("*")) {
                for (String module : MUtilities.LIST_MODULE) {
                    if (Const.CONFIG.getBoolean(module + ".enabled")) {
                        Const.CONFIG.set(module + ".enabled", false);
                        commandSender.sendMessage(Tools.configColor("mutils.message.arg.disable.success").replace("{prefix}", Const.PREFIX)
                                .replace("{arg}", module));
                    }
                }
            } else {
                Const.CONFIG.set(strings[1].toLowerCase() + ".enabled", false);
                commandSender.sendMessage(Tools.configColor("mutils.message.arg.disable.success").replace("{prefix}", Const.PREFIX)
                        .replace("{arg}", strings[1].toLowerCase()));
            }
            MUtilities.getInstance().saveConfig();
            MUtilities.updateConfig();
            return true;
        }

        commandSender.sendMessage(Tools.configColor("mutils.message.no found").replace("{prefix}", Const.PREFIX));
        return true;
    }
}
