package be.machigan.mutilities.colorname;

import be.machigan.mutilities.utils.Const;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabColorname implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg = new ArrayList<>();
        List<String> arg2 =new ArrayList<>();
        List<String> arg3 = new ArrayList<>();
        List<String> tab = new ArrayList<>();
        if (!Const.CONFIG.getBoolean("colorname.enabled")) {
            return tab;
        }
        if (!commandSender.hasPermission("mutils.colorname.use")) {
            return tab;
        }

        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.colorname.me")) {
                arg.add("me");
            }
            if (commandSender.hasPermission("mutils.colorname.other")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    arg.add(player.getName());
                }
            }

            StringUtil.copyPartialMatches(strings[0], arg, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings.length == 2) {
            if ((strings[0].equalsIgnoreCase("me") && commandSender.hasPermission("mutils.colorname.me"))
            || (commandSender.hasPermission("mutils.colorname.other"))) {
                arg2.add("reset");
                arg2.add("set");
                StringUtil.copyPartialMatches(strings[1], arg2, tab);
                Collections.sort(tab);
                return tab;
            }
        }


        if (strings.length == 3) {
            if ((strings[1].equalsIgnoreCase("set") && strings[0].equalsIgnoreCase("me") && commandSender.hasPermission("mutils.colorname.me"))
            || (strings[1].equalsIgnoreCase("set") && commandSender.hasPermission("mutils.colorname.other"))) {
                if (strings[2].length() == 0) {
                    for (Character c : Colorname.COLOR_LIST) {
                        tab.add(c.toString());
                    }
                    return tab;
                }

                boolean colorUsed = false;
                List<Character> formatRest = new ArrayList<>(Colorname.FORMAT_LIST);
                for (Character c: strings[2].toCharArray()) {
                    if ((!Colorname.COLOR_LIST.contains(c)) && (!Colorname.FORMAT_LIST.contains(c))) {
                        return tab;
                    }
                    if (Colorname.COLOR_LIST.contains(c)) {
                        colorUsed = true;
                    } else {
                        formatRest.remove(c);
                    }
                }
                if (!colorUsed) {
                    for (Character c : Colorname.COLOR_LIST) {
                        arg3.add(strings[2] + c);
                    }
                }
                for (Character c : formatRest) {
                    arg3.add(strings[2] + c);
                }
                StringUtil.copyPartialMatches(strings[2], arg3, tab);
                Collections.sort(tab);
                return tab;

            }
        }



        return tab;
    }
}
