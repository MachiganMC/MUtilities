package be.machigan.mutilities.react;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Reaction {
    final public static File FILE = new File(MUtilities.getInstance().getDataFolder(), "/react.yml");
    public static String PREFIX;
    public static List<String> REACTIONS_GAME;
    public static int DELAY;
    public static int TIME;
    public static String COMMAND_FIRST;
    public static String COMMAND_SECOND;
    public static String COMMAND_THIRD;
    public static String END_MESSAGE;

    public static boolean IN_GAME = false;
    public static long START_TIME;
    public static String GAME_LINK;

    public static String MESSAGE;
    public static String WORD;
    public static Map<String, Double> WINNER;
    public static String NO_WIN;
    public static String WIN;
    public static int TASK_ID = -1;

    static {
        init();
    }

    public static void init() {
        PREFIX = Tools.replaceColor(Const.CONFIG.getString("reaction.prefix"));
        REACTIONS_GAME = Const.CONFIG.getStringList("reaction.list");
        DELAY = Const.CONFIG.getInt("reaction.delay");
        TIME = Const.CONFIG.getInt("reaction.time");
        COMMAND_FIRST = Const.CONFIG.getString("reaction.command first");
        COMMAND_SECOND = Const.CONFIG.getString("reaction.command second");
        COMMAND_THIRD = Const.CONFIG.getString("reaction.command third");
        END_MESSAGE = Tools.replaceColor(Const.CONFIG.getString("reaction.end message").replace("{prefix}", PREFIX));

        if (REACTIONS_GAME.size() == 0) {
            Tools.log("No reaction game has been wrote", Const.LOG_WARNING);
            Tools.log("Reaction module disabled");
            Const.CONFIG.set("notification.enabled", false);
            MUtilities.getInstance().saveConfig();
        }
        if (TASK_ID != -1) {
            Bukkit.getScheduler().cancelTask(TASK_ID);
            launch();
        }
    }

    public static void launch() {
        /*
        launch the reaction repeating task,
        each x minute a new reaction game will launch
         */
        if (!Const.CONFIG.getBoolean("reaction.enabled")) {
            return;
        }
        TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MUtilities.getInstance(), () -> {
            if (Const.CONFIG.getBoolean("reaction.enabled")) {
                Reaction.newGame();
            } else {
                Bukkit.getScheduler().cancelTask(TASK_ID);
                Tools.log("The repeating task of react module has been canceled, because the module is disable");
            }
        }, DELAY * 20L * 60, DELAY * 20L * 60);
    }

    public static void newGame() {
        //launch a new reaction game
        GAME_LINK = REACTIONS_GAME.get(new Random().nextInt(REACTIONS_GAME.size()));
        MESSAGE = Tools.replaceColor(Const.CONFIG.getString("reaction.build." + GAME_LINK + ".message").replace("{prefix}", PREFIX));
        WORD = Const.CONFIG.getString("reaction.build." + GAME_LINK + ".word");
        WIN = Tools.replaceColor(Const.CONFIG.getString("reaction.build." + GAME_LINK + ".win").replace("{prefix}", PREFIX));
        NO_WIN = Tools.replaceColor(Const.CONFIG.getString("reaction.build." + GAME_LINK + ".nowin").replace("{prefix}", PREFIX));

        WINNER = new LinkedHashMap<>();
        START_TIME = System.currentTimeMillis();
        IN_GAME = true;
        Bukkit.getServer().broadcastMessage(MESSAGE);
        Bukkit.getScheduler().scheduleSyncDelayedTask(MUtilities.getInstance(), () -> {
            if (IN_GAME) {
                finish();
            }
        }, TIME * 20L);
    }

    public static void finish() {
        IN_GAME = false;
        if (WINNER.isEmpty())  {
            Bukkit.getServer().broadcastMessage(NO_WIN);
        } else {
            Bukkit.getServer().broadcastMessage(END_MESSAGE);
        }

        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        config.set("total react", config.getInt("total react") + 1);
        List<String> winnersList = new ArrayList<>(WINNER.keySet());
        List<String> allPlayer = config.getStringList("all player");

        WINNER.forEach((String player, Double time) -> {
            if (!allPlayer.contains(player)) {
                allPlayer.add(player);
            }
            String key = "player." + player + ".";
            int pos = winnersList.size() - winnersList.indexOf(player);
            int totalWin = config.getInt(key + "win.pos 1") + config.getInt("win.pos 2") + config.getInt("win.pos 3");
            double avgTime = (double) Math.round((
                    ((config.getDouble(key + "average time") * totalWin) + time) / (totalWin + 1)
            ) * 100) / 100;
            if (config.getDouble(key + "best time") != 0 && config.getDouble(key + "best time") > time) {
                config.set(key + "best time", time);
            }
            config.set(key + "average time" , avgTime);
            config.set(key + "win.pos " + pos, config.getInt(key + "win.pos " + pos) + 1);
        });
        config.set("all player", allPlayer);
        try {
            config.save(FILE);
        } catch (IOException ignored) {
            Tools.log("The stats of the react game couldn't be save", Const.LOG_SEVERE);
        }

    }



}
