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
        text = text.replace("&0", "" + ChatColor.BLACK);
        text = text.replace("&1", "" + ChatColor.DARK_BLUE);
        text = text.replace("&2", "" + ChatColor.DARK_GREEN);
        text = text.replace("&3", "" + ChatColor.DARK_AQUA);
        text = text.replace("&4", "" + ChatColor.DARK_RED);
        text = text.replace("&5", "" + ChatColor.DARK_PURPLE);
        text = text.replace("&6", "" + ChatColor.GOLD);
        text = text.replace("&7", "" + ChatColor.GRAY);
        text = text.replace("&8", "" + ChatColor.DARK_GRAY);
        text = text.replace("&9", "" + ChatColor.BLUE);
        text = text.replace("&a", "" + ChatColor.GREEN);
        text = text.replace("&b", "" + ChatColor.AQUA);
        text = text.replace("&c", "" + ChatColor.RED);
        text = text.replace("&d", "" + ChatColor.LIGHT_PURPLE);
        text = text.replace("&e", "" + ChatColor.YELLOW);
        text = text.replace("&f", "" + ChatColor.WHITE);
        text = text.replace("&k", "" + ChatColor.MAGIC);
        text = text.replace("&l", "" + ChatColor.BOLD);
        text = text.replace("&m", "" + ChatColor.STRIKETHROUGH);
        text = text.replace("&n", "" + ChatColor.UNDERLINE);
        text = text.replace("&o", "" + ChatColor.ITALIC);
        text = text.replace("&r", "" + ChatColor.RESET);
        return text;
    }

    public static void sudo(String command) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(MUtilities.getInstance(), () -> Bukkit.getServer().dispatchCommand(
                Bukkit.getServer().getConsoleSender(), command), 0);
    }

    public static String configColor(String path) {
        return replaceColor(Const.CONFIG.getString(path));
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
