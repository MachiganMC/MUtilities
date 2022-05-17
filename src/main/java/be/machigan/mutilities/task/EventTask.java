package be.machigan.mutilities.task;

import org.bukkit.Material;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

public class EventTask implements Listener {

    @EventHandler
    public static void onPose(BlockPlaceEvent e) {
        if (!Task.inGame) {
            return;
        }
        if (!Task.action.equalsIgnoreCase("break")) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        if (Task.BLOCK_LOC.contains(e.getBlockPlaced().getLocation())) {
            return;
        }
        Task.BLOCK_LOC.add(e.getBlockPlaced().getLocation());
    }

    @EventHandler
    public static void onBreak(BlockBreakEvent e) {
        if (!Task.inGame) {
            return;
        }
        if (!Task.action.equalsIgnoreCase("break")) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        if (!e.getBlock().getType().equals(Material.matchMaterial(Task.object))) {
            return;
        }
        if (Task.BLOCK_LOC.contains(e.getBlock().getLocation())) {
            Task.BLOCK_LOC.remove(e.getBlock().getLocation());
            return;
        }
        if (Task.COUNT.containsKey(e.getPlayer().getUniqueId().toString())) {
            if (Task.COUNT.get(e.getPlayer().getUniqueId().toString()) >= Task.quant) {
                return;
        }

        }

        Task.add(e.getPlayer().getUniqueId().toString());
    }

    @EventHandler
    public static void onKill(EntityDeathEvent e) {
        if (!Task.inGame) {
            return;
        }
        if (!Task.action.equalsIgnoreCase("kill")) {
            return;
        }
        if (!e.getEntity().getType().equals(EntityType.valueOf(Task.object))) {
            return;
        }
        if (!(e.getEntity().getKiller() instanceof Player)) {
            return;
        }
        if (Task.COUNT.containsKey(e.getEntity().getKiller().getUniqueId().toString())) {
            if (Task.COUNT.get(e.getEntity().getKiller().getUniqueId().toString()) >= Task.quant) {
                return;
            }
        }

        Task.add(e.getEntity().getKiller().getUniqueId().toString());
    }

    @EventHandler
    public static void onCraft(CraftItemEvent e) {
        if (!Task.inGame) {
            return;
        }
        if (!Task.action.equalsIgnoreCase("craft")) {
            return;
        }
        if (!e.getRecipe().getResult().getType().equals(Material.matchMaterial(Task.object))) {
            return;
        }
        if (!(e.getWhoClicked() instanceof  Player)) {
            return;
        }
        Player player = (Player) e.getWhoClicked();

        if (Task.COUNT.containsKey(player.getUniqueId().toString())) {
            if (Task.COUNT.get(player.getUniqueId().toString()) >= Task.quant) {
                return;
            }
        }


        Task.add(player.getUniqueId().toString());
    }

    @EventHandler
    public static void onEat(PlayerItemConsumeEvent e) {
        if (!Task.inGame) {
            return;
        }
        if (!Task.action.equalsIgnoreCase("eat")) {
            return;
        }
        if (e.isCancelled()) {
            return;
        }
        if (!e.getItem().getType().equals(Material.matchMaterial(Task.object))) {
            return;
        }
        if (Task.COUNT.containsKey(e.getPlayer().getUniqueId().toString())) {
            if (Task.COUNT.get(e.getPlayer().getUniqueId().toString()) >= Task.quant) {
                return;
            }
        }


        Task.add(e.getPlayer().getUniqueId().toString());
    }
}
