package be.machigan.mutilities.utils;

import be.machigan.mutilities.MUtilities;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Logger;

public class Tools {


    /**
     * @param message The message you want to log
     * @param logType (info, warning or severe)
     */
    public static void log(String message, String logType) {


        if (logType.equalsIgnoreCase(Const.LOG_INFO)) {
            Logger.getLogger("Minecraft").info(Const.PREFIX + ChatColor.WHITE + message);
            return;
        }

        if (logType.equalsIgnoreCase(Const.LOG_WARNING)) {
            Logger.getLogger("Minecraft").warning(Const.PREFIX + ChatColor.GOLD + message);
            return;
        }

        if (logType.equalsIgnoreCase(Const.LOG_SEVERE)) {
            Logger.getLogger("Minecraft").severe(Const.PREFIX + ChatColor.DARK_RED + message);
        }
    }

    public static void log(String message) {
        Logger.getLogger("Minecraft").info(Const.PREFIX + ChatColor.WHITE + message);
    }

    public static String replaceColor(String text) {
        return text.replace("&0", "" + ChatColor.BLACK)
                .replace("&1", "" + ChatColor.DARK_BLUE)
                .replace("&2", "" + ChatColor.DARK_GREEN)
                .replace("&3", "" + ChatColor.DARK_AQUA)
                .replace("&4", "" + ChatColor.DARK_RED)
                .replace("&5", "" + ChatColor.DARK_PURPLE)
                .replace("&6", "" + ChatColor.GOLD)
                .replace("&7", "" + ChatColor.GRAY)
                .replace("&8", "" + ChatColor.DARK_GRAY)
                .replace("&9", "" + ChatColor.BLUE)
                .replace("&a", "" + ChatColor.GREEN)
                .replace("&b", "" + ChatColor.AQUA)
                .replace("&c", "" + ChatColor.RED)
                .replace("&d", "" + ChatColor.LIGHT_PURPLE)
                .replace("&e", "" + ChatColor.YELLOW)
                .replace("&f", "" + ChatColor.WHITE)
                .replace("&k", "" + ChatColor.MAGIC)
                .replace("&l", "" + ChatColor.BOLD)
                .replace("&m", "" + ChatColor.STRIKETHROUGH)
                .replace("&n", "" + ChatColor.UNDERLINE)
                .replace("&o", "" + ChatColor.ITALIC)
                .replace("&r", "" + ChatColor.RESET);
    }

    public static void sudo(String command) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MUtilities.getInstance(), () -> Bukkit.getServer().dispatchCommand(
                Bukkit.getServer().getConsoleSender(), command), 0);
    }

    public static String configColor(String path) {
        try {
            return replaceColor(Const.CONFIG.getString(path));
        } catch (NullPointerException ignored) {
            Tools.log("Warning, the field \"" + path + "\" is empty in the configuration file", Const.LOG_SEVERE);
            return replaceColor(Const.PREFIX + "&4Missing section &c&o" + path + " &4in the configuration file");
        }
    }

    public static String getUUID(String name) throws IOException, ParseException {
        URL url = new URL("https://api.mojang.com/users/profiles/minecraft/" + name);
        URLConnection uc = url.openConnection();
        BufferedReader bf = new BufferedReader(new InputStreamReader(uc.getInputStream()));
        StringBuilder response = new StringBuilder();
        String inputLine;
        while ((inputLine = bf.readLine()) != null) {
            response.append(inputLine);
        }
        bf.close();

        if (response.toString().isEmpty()) {
            throw new IOException("Username not found");
        }
        JSONParser parser = new JSONParser();
        Object object = parser.parse(response.toString());
        JSONObject jo = (JSONObject) object;
        String str = (String) jo.get("id");
        return str.replaceAll("(\\w{8})(\\w{4})(\\w{4})(\\w{4})(\\w{12})", "$1-$2-$3-$4-$5");
    }

    public static boolean withdraw(Player player, double amount) {
        EconomyResponse econ = Const.ECONOMY.withdrawPlayer(player, amount);
        return econ.transactionSuccess();
    }

    public static boolean pay(Player player, double amount) {
        EconomyResponse econ = Const.ECONOMY.depositPlayer(player, amount);
        return econ.transactionSuccess();
    }

    public static boolean pay(OfflinePlayer player, double amount) {
        EconomyResponse econ = Const.ECONOMY.depositPlayer(player, amount);
        return econ.transactionSuccess();
    }

    public static boolean checkBalance(Player player, double amount) {
        return (Const.ECONOMY.getBalance(player) >= amount);
    }



}
