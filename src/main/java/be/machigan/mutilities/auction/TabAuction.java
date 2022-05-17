package be.machigan.mutilities.auction;

import be.machigan.mutilities.utils.Const;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabAuction implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg = new ArrayList<>();
        List<String> arg2 = new ArrayList<>();
        List<String> tab = new ArrayList<>();

        if (!Const.CONFIG.getBoolean("auction.enabled")) {
            return tab;
        }
        if (!commandSender.hasPermission("mutils.auction.use")) {
            return tab;
        }
        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.auction.create")) {
                arg.add("create");
            }
            if (commandSender.hasPermission("mutils.auction.item")) {
                arg.add("item");
            }
            if (commandSender.hasPermission("mutils.auction.overbid")) {
                arg.add("overbid");
            }
            arg.add("claim");

            StringUtil.copyPartialMatches(strings[0], arg, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings.length == 2) {
            if (strings[0].equalsIgnoreCase("create") && commandSender.hasPermission("mutils.auction.create")) {
                arg2.add("10");
                arg2.add("100");
                arg2.add("1000");
                arg2.add("10000");
                StringUtil.copyPartialMatches(strings[1], arg2, tab);
                return tab;
            }

            if (strings[0].equalsIgnoreCase("overbid") && commandSender.hasPermission("mutils.auction.overbid")) {
                arg2.add(Double.toString(Auction.bid + 10));
                arg2.add(Double.toString(Auction.bid + 100));
                arg2.add(Double.toString(Auction.bid + 1000));
                arg2.add(Double.toString(Auction.bid + 10000));
                StringUtil.copyPartialMatches(strings[1], arg2, tab);
                return tab;
            }
        }

        return tab;
    }
}
