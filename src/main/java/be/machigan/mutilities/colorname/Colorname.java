package be.machigan.mutilities.colorname;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Colorname {
    final public static File FILE = new File(MUtilities.getInstance().getDataFolder(), "/colorname.yml");
    public static String PREFIX;
    private static final Character[] COLOR_ARRAYS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    private static final Character[] FORMAT_ARRAYS = {'k', 'l', 'm', 'n', 'o'};
    final public static List<Character> COLOR_LIST;
    final public static List<Character> FORMAT_LIST;

    static {
        init();
        COLOR_LIST = new ArrayList<>(Arrays.asList(COLOR_ARRAYS));
        FORMAT_LIST = new ArrayList<>(Arrays.asList(FORMAT_ARRAYS));
    }


    public static void init() {
        PREFIX = Tools.configColor("colorname.prefix");
    }

    public static void reset(Player player) {
        player.setDisplayName(player.getName() + ChatColor.RESET);
        player.setPlayerListName(player.getName() + ChatColor.RESET);
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        List<String> allPlayer = config.getStringList("list");
        allPlayer.remove(player.getUniqueId().toString());
        config.set("list", allPlayer);
        config.set("all player." + player.getUniqueId(), null);
        try {
            config.save(FILE);
        } catch (IOException ignored) {
            Tools.log("The colorname of " + player.getUniqueId() + " cannot be reset", Const.LOG_SEVERE);
        }
    }

    public static void set(Player player, String colorCode) {
        player.setDisplayName(colorCode + player.getName() + ChatColor.RESET);
        player.setPlayerListName(colorCode + player.getName() + ChatColor.RESET);
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        List<String> allPlayer = config.getStringList("list");
        if (!allPlayer.contains(player.getUniqueId().toString())) {
            allPlayer.add(player.getUniqueId().toString());
            config.set("list", allPlayer);
        }
        config.set("all player." + player.getUniqueId(), colorCode);
        try {
            config.save(FILE);
        } catch (IOException ignored) {
            Tools.log("The display name cannot be saved", Const.LOG_SEVERE);
        }
    }

    public static String charListToColor(List<Character> charList) {
        StringBuilder color = new StringBuilder();
        for (Character c : charList) {
            color.append(Tools.replaceColor("&" + c));
        }
        return color.toString();
    }






}
