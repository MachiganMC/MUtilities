package be.machigan.mutilities.salary;

import be.machigan.mutilities.utils.Const;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabSalary implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg = new ArrayList<>(Const.CONFIG.getStringList("salary.all salaries"));
        List<String> arg2 = new ArrayList<>();
        List<String> tab = new ArrayList<>();
        if (!Const.CONFIG.getBoolean("salary.enabled")) {
            return tab;
        }

        if (!commandSender.hasPermission("mutils.salary.use")) {
            return tab;
        }
        if (strings.length == 1) {
            StringUtil.copyPartialMatches(strings[0], arg, tab);
            Collections.sort(tab);
            return tab;
        }
        if (strings.length == 2) {
            if (commandSender.hasPermission("mutils.salary.add")) {
                arg2.add("add");
            }
            if (commandSender.hasPermission("mutils.salary.remove")) {
                arg2.add("remove");
            }

            StringUtil.copyPartialMatches(strings[1], arg2, tab);
            Collections.sort(tab);
            return tab;
        }
        if (strings.length == 3) {
            if (strings[1].equalsIgnoreCase("add")) {
                if (commandSender.hasPermission("mutils.salary.add")) {
                    return null;
                }
            }
            if (strings[1].equalsIgnoreCase("remove")) {
                if (commandSender.hasPermission("mutils.salary.remove")) {
                    return null;
                }
            }
        }


        return tab;
    }
}
