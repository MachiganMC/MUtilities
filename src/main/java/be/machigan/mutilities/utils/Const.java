package be.machigan.mutilities.utils;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.auction.Auction;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.file.FileConfiguration;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Const {

    final public static  String PREFIX = Tools.replaceColor("&3[&6&lM&eUtils.&3] &r");
    final public static FileConfiguration CONFIG = MUtilities.getInstance().getConfig();
    public final static DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    final public static String LOG_INFO = "info";
    final public static String LOG_WARNING = "warning";
    final public static String LOG_SEVERE = "severe";
    final public static Economy ECONOMY = Auction.rsp.getProvider();


}
