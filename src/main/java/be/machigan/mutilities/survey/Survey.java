package be.machigan.mutilities.survey;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class Survey {
    public static final File FILE = new File(MUtilities.getInstance().getDataFolder(), "/survey.yml");
    public static String PREFIX;
    public static int DELAY;
    public static int TASK_ID = -1;


    public static String SURVEY;
    public static boolean isSurvey = false;
    public static int YES;
    public static int NO;
    public static int USELESS;
    public static String CREATOR;

    static {
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        init();
        try {
            if (config.get("actual") != null) {
                TimerSurvey.reload();
                isSurvey = true;
            } else {
                isSurvey = false;
            }
        } catch (ParseException e) {
            Tools.log("The survey cannot be use cause the date cannot be parsed. Disabling of the survey module", "severe");
            Const.CONFIG.set("survey.enabled", false);
            MUtilities.getInstance().saveConfig();

        }
    }

    public static void init() {
        PREFIX = Tools.configColor("survey.prefix");
        DELAY = Const.CONFIG.getInt("survey.delay");
        if (TASK_ID != -1) {
            Bukkit.getScheduler().cancelTask(TASK_ID);
        }


        TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MUtilities.getInstance(), () -> {
            update();
            if (!Const.CONFIG.getBoolean("survey.enabled")) {
                Bukkit.getScheduler().cancelTask(TASK_ID);
            } else if (isSurvey) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(Tools.configColor("survey.message.survey").replace("{prefix}", PREFIX)
                            .replace("{survey}",  SURVEY)
                            .replace("{yes}", Integer.toString(YES))
                            .replace("{no}", Integer.toString(NO))
                            .replace("{useless}", Integer.toString(USELESS))
                            .replace("{total vote}", Integer.toString(YES + NO + USELESS))
                            .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(CREATOR)).getName())
                            .replace("{receiver}", player.getName()));
                }
            } else {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(Tools.configColor("survey.message.no survey").replace("{prefix}", PREFIX)
                            .replace("{receiver}", player.getName()));
                }
            }
        }, (DELAY + 2) * 60 * 20L, DELAY * 60 * 20L);
    }

    public static void create(String survey, String creator) throws IOException, ParseException {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        config.set("actual.survey", survey);
        config.set("actual.creator", creator);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime end = today.plusHours(Const.CONFIG.getInt("survey.time"));
        String endS = end.getYear() + "-" + end.getMonthValue() + "-" + end.getDayOfMonth() +
                " " + end.getHour() + ":" + end.getMinute() + ":" + end.getSecond();
        config.set("actual.end", endS);
        config.save(FILE);

        SURVEY = Tools.replaceColor(survey);
        CREATOR = creator;
        YES = 0;
        NO = 0;
        USELESS = 0;
        isSurvey = true;


        Date endD = Const.DATE_FORMAT.parse(endS);
        TimerSurvey.TIMER = new Timer();
        TimerSurvey.TIMER.schedule(new TimerSurvey(), endD);
    }

    public static void vote(String vote, String player) throws IOException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        List<String> allPlayer = config.getStringList("actual.all player");
        switch (vote) {
            case "yes": {
                config.set("actual.yes", config.getInt("actual.yes") + 1);
                break;
            }
            case "no": {
                config.set("actual.no", config.getInt("actual.no") + 1);
                break;
            }
            case "useless": {
                config.set("actual.useless", config.getInt("actual.useless") + 1);
                break;
            }
        }
        allPlayer.add(player);
        config.set("actual.all player", allPlayer);
        config.save(FILE);
    }

    public static void update() {
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        if (config.get("actual") == null) {
            isSurvey = false;
            return;
        }
        isSurvey = true;
        SURVEY = Tools.replaceColor(config.getString("actual.survey"));
        YES = config.getInt("actual.yes");
        NO = config.getInt("actual.no");
        USELESS = config.getInt("actual.useless");
        CREATOR = config.getString("actual.creator");
    }

    public static void archive(String flag)  {
        update();
        if (!isSurvey) {
            return;
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        int id = config.getInt("next id") +1;
        config.set("next id", id);
        config.set("history." + id + ".survey", SURVEY);
        config.set("history." + id + ".creator", CREATOR);
        config.set("history." + id + ".yes", YES);
        config.set("history." + id + ".no", NO);
        config.set("history." + id + ".useless", USELESS);
        Tools.log(config.getString("history." + id + ".creator"));
        if (flag != null) {
            config.set("history." + id + ".flag", flag);
        }
        try {
            config.save(FILE);
        } catch (IOException e) {
            Tools.log("The survey cannot be archived", Const.LOG_WARNING);
        }
    }

    public static void archive() {
        archive(null);
    }
}
