package be.machigan.mutilities.colorname;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.util.List;

public class ReloadColorname implements Listener {

    @EventHandler
    public void onJoin(PlayerJoinEvent e) {
        FileConfiguration config = YamlConfiguration.loadConfiguration(Colorname.FILE);
        List<String> allPlayer = config.getStringList("list");
        if (allPlayer.contains(e.getPlayer().getUniqueId().toString())) {
            e.getPlayer().setDisplayName(config.getString("all player." + e.getPlayer().getUniqueId()) + e.getPlayer().getName() + ChatColor.RESET);
            e.getPlayer().setPlayerListName(config.getString("all player." + e.getPlayer().getUniqueId()) + e.getPlayer().getName() + ChatColor.RESET);
        }
    }

}
