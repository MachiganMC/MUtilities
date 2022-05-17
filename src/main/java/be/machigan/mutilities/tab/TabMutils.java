package be.machigan.mutilities.tab;

import be.machigan.mutilities.MUtilities;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabMutils implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg2 = new ArrayList<>();
        List<String> tab = new ArrayList<>();
        if (!commandSender.hasPermission("mutils.command.use")) {
            return tab;
        }

        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.command.prefix")) {
                arg2.add("prefix");
            }
            if (commandSender.hasPermission("mutils.command.reloadconfig")) {
                arg2.add("reloadconfig");
            }
            if (commandSender.hasPermission("mutils.command.enable")) {
                arg2.add("enable");
            }
            if (commandSender.hasPermission("mutils.command.disable")) {
                arg2.add("disable");
            }

            StringUtil.copyPartialMatches(strings[0], arg2, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings[0].equalsIgnoreCase("prefix") && commandSender.hasPermission("mutils.command.prefix")) {
            if (strings.length == 2) {
                StringUtil.copyPartialMatches(strings[1], MUtilities.LIST_MODULE, tab);
                Collections.sort(tab);
            }
            return tab;
        }
        if (strings[0].equalsIgnoreCase("enable") && commandSender.hasPermission("mutils.command.enable")) {
            if (strings.length == 2) {
                List<String> arg3 = new ArrayList<>(MUtilities.LIST_MODULE);
                arg3.add("*");
                StringUtil.copyPartialMatches(strings[1], arg3, tab);
                Collections.sort(tab);
            }
            return tab;
        }
        if (strings[0].equalsIgnoreCase("disable") && commandSender.hasPermission("mutils.command.disable")) {
            if (strings.length == 2) {
                List<String> arg3 = new ArrayList<>(MUtilities.LIST_MODULE);
                arg3.add("*");
                StringUtil.copyPartialMatches(strings[1], arg3, tab);
                Collections.sort(tab);
            }
            return tab;
        }

        return tab;
    }
}
