package be.machigan.mutilities.survey;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.text.ParseException;
import java.util.UUID;

public class CommandSurvey implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("survey.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.survey.use")) {
            commandSender.sendMessage(Tools.configColor("survey.message.no perm.survey use").replace("{prefix}", Survey.PREFIX)
                    .replace("{sender}", command.getName()).replace("{perm}", "mutils.survey.use"));
            return true;
        }
        Survey.update();
        if (strings.length == 0) {
            if (Survey.isSurvey) {
                commandSender.sendMessage(Tools.configColor("survey.message.survey").replace("{prefix}", Survey.PREFIX)
                        .replace("{survey}", Survey.SURVEY).replace("{yes}", Integer.toString(Survey.YES))
                        .replace("{no}", Integer.toString(Survey.NO)).replace("{useless}", Integer.toString(Survey.USELESS))
                        .replace("{total vote}", Integer.toString(Survey.YES + Survey.NO + Survey.USELESS))
                        .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(Survey.CREATOR)).getName())
                        .replace("{receiver}", command.getName()));
            } else {
                commandSender.sendMessage(Tools.configColor("survey.message.no survey").replace("{prefix}", Survey.PREFIX)
                        .replace("{receiver}", command.getName()));
            }
            return true;
        }

        if (strings[0].equalsIgnoreCase("create")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.create.only player").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", command.getName()));
                return true;
            }
            if (!commandSender.hasPermission("mutils.survey.create")) {
                commandSender.sendMessage(Tools.configColor("survey.message.no perm.survey create").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.survey.create"));
                return true;
            }
            if (Survey.isSurvey) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.create.already survey").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", command.getName()).replace("{survey}", Survey.SURVEY));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.create.miss argument").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }

            StringBuilder survey = new StringBuilder();
            for (int i = 1; i < strings.length; i++) {
                survey.append(strings[i]).append(" ");
            }
            try {
                Survey.create(survey.toString(), ((Player) commandSender).getUniqueId().toString());
            } catch (IOException | ParseException ignored) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.create.intern error").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                Tools.log("The new survey cannot be created caused of a parsing error. Disabling of the survey module");
                Const.CONFIG.set("survey.enabled", false);
                MUtilities.getInstance().saveConfig();
                return true;
            }
            commandSender.sendMessage(Tools.configColor("survey.message.arg.create.success").replace("{prefix}", Survey.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{survey}", Survey.SURVEY));
            Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.arg.create.announcement").replace("{prefix}", Survey.PREFIX)
                    .replace("{survey}", Survey.SURVEY).replace("{creator}", commandSender.getName()));
            return true;
        }

        if (strings[0].equalsIgnoreCase("vote")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.only player").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!commandSender.hasPermission("mutils.survey.vote")) {
                commandSender.sendMessage(Tools.configColor("survey.message.no perm.survey vote").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.survey.vote"));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
            if (config.getStringList("actual.all player").contains(((Player) commandSender).getUniqueId().toString())) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.already vote").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.miss argument").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings[1].equalsIgnoreCase("yes")) {
                try {
                    Survey.vote("yes", ((Player) commandSender).getUniqueId().toString());
                } catch (IOException ignored) {
                    commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.intern error").replace("{prefix}", Survey.PREFIX)
                            .replace("{sender}", commandSender.getName()));
                    return true;
                }
                Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.arg.vote.vote yes").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings[1].equalsIgnoreCase("no")) {
                try {
                    Survey.vote("no", ((Player) commandSender).getUniqueId().toString());
                } catch (IOException ignored) {
                    commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.intern error").replace("{prefix}", Survey.PREFIX)
                            .replace("{sender}", commandSender.getName()));
                    return true;
                }
                Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.arg.vote.vote no").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings[1].equalsIgnoreCase("useless")) {
                try {
                    Survey.vote("useless", ((Player) commandSender).getUniqueId().toString());
                } catch (IOException ignored) {
                    commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.intern error").replace("{prefix}", Survey.PREFIX)
                            .replace("{sender}", commandSender.getName()));
                    return true;
                }
                Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.arg.vote.useless").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                Survey.update();
                if (Survey.USELESS >= Const.CONFIG.getInt("survey.useless to stop")) {
                    Survey.archive("stopped by vote");
                    config = YamlConfiguration.loadConfiguration(Survey.FILE);
                    config.set("actual", null);
                    try {
                        config.save(Survey.FILE);
                    } catch (IOException e) {
                        Tools.log("Error with the canceling of the survey. Disabling the survey module");
                        Const.CONFIG.set("survey.enabled", false);
                        MUtilities.getInstance().saveConfig();
                    }
                    Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.stop").replace("{prefix}", Survey.PREFIX));
                }
                return true;
            }

            commandSender.sendMessage(Tools.configColor("survey.message.arg.vote.bad argument").replace("{prefix}", Survey.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{arg}", strings[1]));
            return true;
        }

        if (strings[0].equalsIgnoreCase("stop")) {
            if (!commandSender.hasPermission("mutils.survey.stop")) {
                commandSender.sendMessage(Tools.replaceColor("survey.message.no perm.survey stop").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.survey.stop"));
                return true;
            }
            if (!Survey.isSurvey) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.stop.no survey").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings.length > 1) {
                StringBuilder flag = new StringBuilder();
                for (int i = 1; i < strings.length; i++) {
                    flag.append(strings[i]).append(" ");
                }
                Survey.archive(flag.toString());
            } else {
                Survey.archive();
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
            config.set("actual", null);

            try {
                config.save(Survey.FILE);
            } catch (IOException ignored) {
                commandSender.sendMessage(Tools.replaceColor("survey.message.arg.stop.intern error").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            commandSender.sendMessage(Tools.configColor("survey.message.arg.stop.success").replace("{prefix}", Survey.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{survey}", Survey.SURVEY));
            Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.stop").replace("{prefix}", Survey.PREFIX));


            return true;

        }

        if (strings[0].equalsIgnoreCase("history")) {
            if (!commandSender.hasPermission("mutils.survey.history")) {
                commandSender.sendMessage(Tools.configColor("survey.message.no perm.survey history").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.survey.history"));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
            if (config.getInt("next id") == 0) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.history.no survey").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.history.miss argument").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{max}", Integer.toString(config.getInt("next id"))));
                return true;
            }
            int id;
            try {
                id = Integer.parseInt(strings[1]);
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.history.nan").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{max}", Integer.toString(config.getInt("next id")))
                        .replace("{arg}", strings[1]));
                return true;
            }
            if (config.get("history." + id) == null) {
                commandSender.sendMessage(Tools.configColor("survey.message.arg.history.bad id").replace("{prefix}", Survey.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{max}", Integer.toString(config.getInt("next id")))
                        .replace("{id}", Integer.toString(id)));
                return true;
            }
            String survey = Tools.replaceColor(config.getString("history." + id + ".survey"));
            int yes = config.getInt("history." + id + ".yes");
            int no = config.getInt("history." + id + ".no");
            int useless = config.getInt("history." + id + ".useless");
            String flag = (config.getString("history." + id + ".flag") == null ?("") : Tools.replaceColor(config.getString("history." + id + ".flag")));
            String creator = Bukkit.getOfflinePlayer(UUID.fromString(config.getString("history." + id + ".creator"))).getName();
            commandSender.sendMessage(Tools.configColor("survey.message.arg.history.success").replace("{prefix}", Survey.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{id}", Integer.toString(id)).replace("{survey}", survey)
                    .replace("{yes}", Integer.toString(yes)).replace("{no}", Integer.toString(no))
                    .replace("{useless}", Integer.toString(useless)).replace("{total vote}", Integer.toString(yes + no + useless))
                    .replace("{creator}", creator).replace("{flag}", flag));
            return true;
        }






        commandSender.sendMessage(Tools.configColor("survey.message.bad argument").replace("{prefix}", Survey.PREFIX)
                .replace("{sender}", commandSender.getName()));
        return true;
    }
}
