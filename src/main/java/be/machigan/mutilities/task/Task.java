package be.machigan.mutilities.task;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class Task {
    public static String PREFIX;
    public static List<String> taskList;
    public static int DELAY;
    public static int TASK_ID = -1;
    final public static Map<String, Integer> COUNT = new HashMap<>();
    final public static Map<String, Double> WINNERS = new HashMap<>();
    final public static Set<Location> BLOCK_LOC = new HashSet<>();
    final public static File FILE = new File(MUtilities.getInstance().getDataFolder(), "/task.yml");

    public static long start;
    public static boolean inGame;
    public static String task;
    public static int time;
    public static String action;
    public static String object;
    public static String command;
    public static String message;
    public static int quant;


    static {
        init();
    }

    public static void init() {
        PREFIX = Tools.configColor("task.prefix");
        taskList = Const.CONFIG.getStringList("task.list");
        DELAY = Const.CONFIG.getInt("task.delay");

        if (TASK_ID != -1) {
            Bukkit.getScheduler().cancelTask(TASK_ID);
        }

        TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MUtilities.getInstance(), () -> {
            if (!Const.CONFIG.getBoolean("task.enabled")) {
                Bukkit.getScheduler().cancelTask(TASK_ID);
            } else if (!inGame) {
                newTask();
            }
        }, (DELAY + 5) * 60 * 20L, DELAY * 60 * 20L);

    }

    public static void newTask() {
        if (inGame) {
            return;
        }
        task = taskList.get(new Random().nextInt(taskList.size()));
        time = Const.CONFIG.getInt("task.build." + task + ".time");
        action = Const.CONFIG.getString("task.build." + task + ".action").toUpperCase();
        object = Const.CONFIG.getString("task.build." + task + ".object");
        command = Const.CONFIG.getString("task.build." + task + ".command");
        message = Tools.configColor("task.build." + task + ".message");
        quant = Const.CONFIG.getInt("task.build." + task + ".quant");
        start = System.currentTimeMillis();

        switch (action) {
            case "BREAK":
            case "CRAFT":
            case "EAT": {
                if (Material.matchMaterial(object) == null) {
                    Tools.log("The task " + task + " cannot be created because the object isn't a material", Const.LOG_WARNING);
                }
                break;
            }
            case "KILL": {
                if (EntityType.valueOf(object) == null) {
                    Tools.log("The task " + task + " cannot be created because the object isn't an entity" , Const.LOG_WARNING);
                    return;
                }
                break;
            }
            default: {
                Tools.log("The task " + task + " cannot be created because the type is invalid", Const.LOG_WARNING);
                return;
            }
        }
        if (time <= 0) {
            Tools.log("The task " + task + " cannot be created because the time is equal or inferior at 0", Const.LOG_WARNING);
            return;
        }
        if (quant <= 0) {
            Tools.log("The task " + task + " cannot be created because the quant is equal or inferior at 0", Const.LOG_WARNING);
            return;
        }

        BLOCK_LOC.clear();
        COUNT.clear();
        Bukkit.getScheduler().scheduleSyncDelayedTask(MUtilities.getInstance(), () -> {
            inGame = false;
            FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
            WINNERS.forEach((String uuid, Double time) -> {
                int total = config.getInt("all player." + uuid + ".total");
                config.set("all player." + uuid + ".total", total + 1);
                config.set("all player." + uuid + ".average", (double) (Math.round(
                               ( ((config.getDouble("all player." + uuid + ".average") * total) + time) / (total + 1)) * 100
                )) / 100);
            });
            config.set("total task", config.getInt("total task") + 1);
            try {
                config.save(FILE);
            } catch (IOException ignored) {
                Tools.log("The stats of the task cannot be saved", Const.LOG_WARNING);
            }
            Bukkit.getServer().broadcastMessage(Tools.configColor("task.end message").replace("{prefix}", PREFIX));
        }, time * 60 * 20L);
        Bukkit.getServer().broadcastMessage(Tools.configColor("task.build." + task + ".message").replace("{prefix}", PREFIX));
        inGame = true;
    }

    public static void add(String uuid) {

        if (Task.COUNT.containsKey(uuid)) {
            Task.COUNT.put(uuid, Task.COUNT.get(uuid) + 1);
        } else {
            Task.COUNT.put(uuid, 1);
        }
        if (Task.COUNT.get(uuid) == Task.quant) {
            double time = (double) (Math.round((double) (System.currentTimeMillis() - start) / 1000 * 100)) / 100;
            Bukkit.getServer().broadcastMessage(Tools.configColor("task.winners").replace("{prefix}", PREFIX)
                    .replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()).replace("{time}", Double.toString(time)));
            Tools.sudo(command.replace("{prefix}", PREFIX).replace("{player}", Bukkit.getOfflinePlayer(UUID.fromString(uuid)).getName()));
            WINNERS.put(uuid, time);
        }
    }






}
