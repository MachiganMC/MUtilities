package be.machigan.mutilities.survey;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TabSurvey implements TabCompleter {

    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {
        List<String> arg = new ArrayList<>();
        List<String> arg2 = new ArrayList<>();
        List<String> tab = new ArrayList<>();

        if (!commandSender.hasPermission("mutils.survey.use")) {
            return tab;
        }

        if (strings.length == 1) {
            if (commandSender.hasPermission("mutils.survey.create")) {
                arg.add("create");
            }
            if (commandSender.hasPermission("mutils.survey.vote")) {
                arg.add("vote");
            }
            if (commandSender.hasPermission("mutils.survey.stop")) {
                arg.add("stop");
            }
            if (commandSender.hasPermission("mutils.survey.history")) {
                arg.add("history");
            }

            StringUtil.copyPartialMatches(strings[0], arg, tab);
            Collections.sort(tab);
            return tab;
        }
        if (strings[0].equalsIgnoreCase("vote") && commandSender.hasPermission("mutils.survey.vote")) {
            arg2.add("yes");
            arg2.add("no");
            arg2.add("useless");

            StringUtil.copyPartialMatches(strings[1], arg2, tab);
            Collections.sort(tab);
            return tab;
        }

        if (strings[0].equalsIgnoreCase("history") && commandSender.hasPermission("mutils.survey.history")) {
            FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
            if (config.getInt("next id") == 0) {
                return tab;
            }
            for (int i = 1; i <= config.getInt("next id"); i++) {
                arg2.add(Integer.toString(i));
            }
            StringUtil.copyPartialMatches(strings[1], arg2, tab);
            return tab;
        }




        return tab;
    }
}
