package be.machigan.mutilities.auction;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class OnJoinAuction implements Listener {

    @EventHandler
    public static void onJoin(PlayerJoinEvent e) {
        if (!Const.CONFIG.getBoolean("auction.enabled")) {
            return;
        }
        FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
        if (config.getStringList("all player").contains(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().sendMessage(Tools.configColor("auction.message.arg.claim.on connect").replace("{prefix}", Auction.PREFIX)
                    .replace("{sender}", e.getPlayer().getName())
                    .replace("{item}", config.getItemStack("claim." + e.getPlayer().getUniqueId()).toString()));
        }
    }
}
