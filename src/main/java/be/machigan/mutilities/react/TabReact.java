package be.machigan.mutilities.react;

import be.machigan.mutilities.utils.Const;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabReact implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> tab = new ArrayList<>();
        List<String> arg2 = new ArrayList<>();
        if (!Const.CONFIG.getBoolean("reaction.enabled")) {
            return tab;
        }
        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.reaction.see.top")) {
                arg2.add("top");
            }
            if (commandSender.hasPermission("mutils.reaction.start")) {
                arg2.add("start");
            }
            if (commandSender.hasPermission("mutils.reaction.see.me")) {
                arg2.add("me");
            }
            if (commandSender.hasPermission("mutils.reaction.see.info")) {
                arg2.add("info");
            }
            StringUtil.copyPartialMatches(strings[0], arg2, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings[0].equalsIgnoreCase("info") && commandSender.hasPermission("mutils.reaction.see.info")) {
            return null;
        }


        return tab;


    }
}
