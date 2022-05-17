package be.machigan.mutilities.auction;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.IOException;
import java.text.ParseException;
import java.util.*;

public class TimerAuction extends TimerTask {

    @Override
    public void run() {
        if (!Auction.inAuction) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
        if (Auction.bidder == null ){
            Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.end.no bidder").replace("{prefix}", Auction.PREFIX)
                    .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(Auction.creator)).getName())
                    .replace("{item}", Auction.item.toString()));
            List<String> allPlayer = config.getStringList("all player");
            if (!allPlayer.contains(Auction.creator)) {
                allPlayer.add(Auction.creator);
            }
            config.set("all player", allPlayer);
            config.set("claim." + Auction.creator, Auction.item);
            try {
                config.save(Auction.FILE);
            } catch (IOException ignored) {
                Tools.log("Error while set claim item to the creator of the auction and reset the auction", Const.LOG_SEVERE);
                return;
            }


        } else {
            Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.end.with bidder").replace("{prefix}", Auction.PREFIX)
                    .replace("{creator}", Bukkit.getOfflinePlayer(UUID.fromString(Auction.creator)).getName())
                    .replace("{item}", Auction.item.toString())
                    .replace("{bidder}", Bukkit.getOfflinePlayer(UUID.fromString(Auction.bidder)).getName())
                    .replace("{bid}", Double.toString(Auction.bid)));

            List<String> allPlayer = config.getStringList("all player");
            if (!allPlayer.contains(Auction.bidder)) {
                allPlayer.add(Auction.bidder);
            }
            config.set("actual", null);
            config.set("claim." + Auction.bidder, Auction.item);
            config.set("all player", allPlayer);
            try {
                config.save(Auction.FILE);
            } catch (IOException ignored) {
                Tools.log("Error while saving the bidder and clear the auction", Const.LOG_SEVERE);
                return;
            }
            if (!Tools.pay(Bukkit.getOfflinePlayer(UUID.fromString(Auction.creator)), Auction.bid)) {
                Tools.log("Error while paying the player " + Auction.creator + " of " + Auction.bid, Const.LOG_SEVERE);
            }
        }
        Auction.inAuction = false;
    }


    public static void reload() throws ParseException {
        FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
        if (config.get("actual") == null) {
            Auction.inAuction = false;
            return;
        }


        Auction.item = config.getItemStack("actual.item");
        Auction.creator = config.getString("actual.creator");
        Auction.bid = config.getDouble("actual.bid");
        Auction.bidder = config.getString("actual.bidder");
        Date end = Const.DATE_FORMAT.parse(config.getString("actual.end"));
        Timer t = new Timer();
        t.schedule(new TimerAuction(), end);
        Auction.inAuction = true;

    }
}
