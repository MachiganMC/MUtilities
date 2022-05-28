package be.machigan.mutilities.salary;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.*;

public class Salary {
    public static String PREFIX;
    public static List<String> SALARIES_LIST;
    final public static File FILE = new File(MUtilities.getInstance().getDataFolder(), "/salary.yml");
    final public static Map<String, Integer> TASK_ID = new HashMap<>();

    static {
        init();
    }

    public static void init() {
        if (!Const.CONFIG.getBoolean("salary.enabled")) {
            return;
        }
        PREFIX = Tools.configColor("salary.prefix");
        SALARIES_LIST = Const.CONFIG.getStringList("salary.all salaries");
        for (String salary : SALARIES_LIST) {
            if (TASK_ID.containsKey(salary)) {
                Bukkit.getScheduler().cancelTask(TASK_ID.get(salary));
                TASK_ID.remove(salary);
            }
            int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(MUtilities.getInstance(), () -> {
                if (!Const.CONFIG.getBoolean("salary.enabled")) {
                    TASK_ID.forEach((String s, Integer i) -> Bukkit.getScheduler().cancelTask(i));
                    TASK_ID.clear();
                    return;
                }
                FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
                for (String uuid : config.getStringList(salary)) {
                    if (Bukkit.getOnlinePlayers().contains(Bukkit.getPlayer(UUID.fromString(uuid)))) {
                        Tools.sudo(Const.CONFIG.getString("salary.build." + salary + ".command").replace("{prefix}", PREFIX)
                                .replace("{player}", Bukkit.getPlayer(UUID.fromString(uuid)).getName()));
                        Bukkit.getPlayer(UUID.fromString(uuid)).sendMessage(Tools.configColor("salary.build." + salary + ".message")
                                .replace("{prefix}", PREFIX).replace("{player}", Bukkit.getPlayer(UUID.fromString(uuid)).getName()));
                    }
                }
            }, Const.CONFIG.getInt("salary.build." + salary + ".delay") * 60 * 20L, Const.CONFIG.getInt("salary.build." + salary + ".delay") * 60 * 20L);
            TASK_ID.put(salary, taskId);
        }

    }
}
