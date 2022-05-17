package be.machigan.mutilities.survey;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

public class TimerSurvey extends TimerTask {

    @Override
    public void run() {
        Survey.update();
        if (Survey.isSurvey) {
            Bukkit.getServer().broadcastMessage(Tools.configColor("survey.message.end").replace("{prefix}", Survey.PREFIX)
                    .replace("{yes}", Integer.toString(Survey.YES)).replace("{no}", Integer.toString(Survey.NO))
                    .replace("{useless}", Integer.toString(Survey.USELESS)).replace("{survey}", Survey.SURVEY)
                    .replace("{total vote}", Integer.toString(Survey.YES + Survey.NO + Survey.USELESS))
                    .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(Survey.CREATOR)).getName()));
            Survey.archive();
            FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
            config.set("actual", null);
            try {
                config.save(Survey.FILE);
            } catch (IOException ignored) {
                Tools.log("The survey cannot be reset", Const.LOG_WARNING);
            }
        }
    }


    public static void reload() throws ParseException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(Survey.FILE);
        Date end = Const.DATE_FORMAT.parse(config.getString("actual.end"));
        Timer t = new Timer();
        t.schedule(new TimerSurvey(), end);
    }
}
