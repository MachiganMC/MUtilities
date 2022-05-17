package be.machigan.mutilities.auction;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.UUID;

public class Auction {

    final public static File FILE = new File(MUtilities.getInstance().getDataFolder(), "/auction.yml");
    public static String PREFIX;
    public static List<String> BLACK_LIST;
    public static RegisteredServiceProvider<Economy> rsp;

    public static boolean inAuction;
    public static ItemStack item;
    public static String creator;
    public static double bid;
    public static String bidder;

    static {
        init();
    }

    public static void init() {
        if (MUtilities.getInstance().getServer().getPluginManager().getPlugin("Vault") == null) {
            Tools.log("Vault plugin hasn't been found for the auction module", Const.LOG_SEVERE);
            Tools.log("Disabling auction module");
            Const.CONFIG.set("auction.enabled", false);
            MUtilities.getInstance().saveConfig();
            return;
        }
        rsp = MUtilities.getInstance().getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            Tools.log("No economy has been found for the auction module", Const.LOG_SEVERE);
            Tools.log("Disabling auction module");
            Const.CONFIG.set("auction.enabled", false);
            MUtilities.getInstance().saveConfig();
            return;
        }

        inAuction = false;
        PREFIX = Tools.configColor("auction.prefix");
        BLACK_LIST = Const.CONFIG.getStringList("auction.blacklist");
        try {
            TimerAuction.reload();
        } catch (ParseException e) {
            Tools.log("Error while parsing the end date of the auction, disabling the auction module", Const.LOG_WARNING);
            Const.CONFIG.set("auction.enabled", false);
            MUtilities.getInstance().saveConfig();
        }
    }

    public static void create(String uuid, ItemStack auction, double price) throws IOException, ParseException {
        creator = uuid;
        bid = price;
        item = auction;
        bidder = null;

        price = (double) (Math.round(price * 100) / 100);
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        config.set("actual.item", item);
        config.set("actual.creator", uuid);
        config.set("actual.bid", price);
        LocalDateTime today = LocalDateTime.now();
        LocalDateTime end = today.plusSeconds(Const.CONFIG.getInt("auction.time"));
        String endS = end.getYear() + "-" + end.getMonthValue() + "-" + end.getDayOfMonth() +
                " " + end.getHour() + ":" + end.getMinute() + ":" + end.getSecond();
        config.set("actual.end", endS);
        config.save(FILE);
        Date endD = Const.DATE_FORMAT.parse(endS);
        Timer t = new Timer();
        t.schedule(new TimerAuction(), endD);
        inAuction = true;

    }

    public static void create(String uuid, ItemStack auction) throws IOException, ParseException {
        create(uuid, auction, 0);
    }

    public static String auctionMessage() {
        if (!inAuction) {
            return Tools.configColor("auction.message.no auction").replace("{prefix}", PREFIX);
        }
        String bidderM;
        if (bidder == null) {
            bidderM = "";
        } else {
            bidderM = Bukkit.getOfflinePlayer(UUID.fromString(bidder)).getName();
        }

        return Tools.configColor("auction.message.auction").replace("{prefix}", PREFIX)
                .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(creator)).getName())
                .replace("{bidder}", bidderM).replace("{bid}", Double.toString(bid)).replace("{item}", item.toString());
    }

    public static void overbid(String uuid, double amount) throws IOException {
        bid = amount;
        bidder = uuid;
        FileConfiguration config = YamlConfiguration.loadConfiguration(FILE);
        config.set("actual.bid", bid);
        config.set("actual.bidder", bidder);
        config.save(FILE);
    }




}
