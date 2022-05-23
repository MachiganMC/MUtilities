package be.machigan.mutilities.papi;

import be.machigan.mutilities.auction.Auction;
import be.machigan.mutilities.colorname.Colorname;
import be.machigan.mutilities.react.Reaction;
import be.machigan.mutilities.survey.Survey;
import be.machigan.mutilities.task.Task;
import be.machigan.mutilities.utils.Const;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class PapiMUtils extends PlaceholderExpansion {

    @Override
    public String getIdentifier() {
        return "mutils";
    }

    @Override
    public String getAuthor() {
        return "Machigan";
    }

    @Override
    public String getVersion() {
        return "v1.0";
    }

    @Override
    public boolean canRegister() {
        return true;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public String onPlaceholderRequest(Player p, String params) {
        return getResult(p.getUniqueId().toString(), params);
    }

    @Override
    public String onRequest(OfflinePlayer p, String params) {
        return getResult(p.getUniqueId().toString(), params);
    }

    private static String getResult(String uuid, String params) {

        if (params.equalsIgnoreCase("colorname")) {
            if (!Const.CONFIG.getBoolean("colorname.enabled")) {
                return null;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Colorname.FILE);
            List<String> allPlayer = config.getStringList("list");
            if (!allPlayer.contains(uuid)) {
                return null;
            }
            return config.getString("all player." + uuid);
        }

        if (params.toLowerCase().contains("auction")) {
            if (!Const.CONFIG.getBoolean("auction.enabled")) {
                return null;
            }
            if (!Auction.inAuction) {
                return null;
            }
            switch (params.toLowerCase()) {
                case "auction_bid": return Double.toString(Auction.bid);
                case "auction_bidder": return Bukkit.getOfflinePlayer(UUID.fromString(Auction.bidder)).getName();
                case "auction_creator": return Bukkit.getOfflinePlayer(UUID.fromString(Auction.creator)).getName();
                case "auction_item": return Auction.item.toString();
                default: return null;
            }
        }

        if (params.toLowerCase().contains("survey")) {
            if (Const.CONFIG.getBoolean("survey.enabled")) {
                return null;
            }
            if (!Survey.isSurvey) {
                return null;
            }
            switch (params.toLowerCase()) {
                case "survey": return Survey.SURVEY;
                case "survey_creator": return Survey.CREATOR;
                case "survey_yes": return Integer.toString(Survey.YES);
                case "survey_no": return Integer.toString(Survey.NO);
                case "survey_useless": return Integer.toString(Survey.USELESS);
                case "survey_total_vote": return Integer.toString(Survey.YES + Survey.NO + Survey.USELESS);
                default: return null;
            }
        }

        if (params.toLowerCase().contains("react")) {
            if (!Const.CONFIG.getBoolean("reaction.enabled")) {
                return null;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Reaction.FILE);
            String path = "player." +  uuid + ".";
            switch (params.toLowerCase()) {
                case "react_time": return Double.toString(config.getDouble(path + "average time"));
                case "react_first": return Integer.toString(config.getInt(path + "win.pos 1"));
                case "react_second": return Integer.toString(config.getInt(path + "win.pos 2"));
                case "react_third": return Integer.toString(config.getInt(path + "win.pos 3"));
                case "react_total": return Integer.toString(config.getInt(path + "win.pos 1") + config.getInt(path + "win.pos 2") +
                        config.getInt(path + "win.pos 3"));
                default: return null;
            }
        }

        if (params.toLowerCase().contains("task")) {
            if (Const.CONFIG.getBoolean("task.enabled")) {
                return null;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Task.FILE);
            if (config.get("all player." + uuid) == null) {
                return null;
            }
            switch (params.toLowerCase()) {
                case "task_time": return Double.toString(config.getDouble("all player." + uuid + ".average"));
                case "task_total": return Integer.toString(config.getInt("all player." + uuid + ".total"));
                default: return null;
            }
        }

        return null;
    }
}
