package be.machigan.mutilities.task;

import be.machigan.mutilities.utils.Const;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabTask implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg = new ArrayList<>();
        List<String> tab = new ArrayList<>();
        if (!Const.CONFIG.getBoolean("task.enabled")) {
            return tab;
        }
        if (!commandSender.hasPermission("mutils.task.use")) {
            return tab;
        }

        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.task.me")) {
                arg.add("me");
            }
            if (commandSender.hasPermission("mutils.task.info")) {
                arg.add("info");
            }
            if (commandSender.hasPermission("mutils.task.start")) {
                arg.add("start");
            }
            StringUtil.copyPartialMatches(strings[0], arg, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("info") && commandSender.hasPermission("mutils.task.info")) {
                return null;
            }
        }

        return tab;
    }
}
