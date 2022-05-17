package be.machigan.mutilities.survey;

import be.machigan.mutilities.auction.Auction;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class GUIAuction implements InventoryHolder, Listener {

    @Override
    public Inventory getInventory() {
        Inventory menu = Bukkit.createInventory(this, 9, Tools.configColor("auction.menu name"));
        ItemStack pane = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta paneMeta = pane.getItemMeta();
        paneMeta.setDisplayName(" ");
        paneMeta.setLore(new ArrayList<>());
        paneMeta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        pane.setItemMeta(paneMeta);
        for (int i = 0; i <= 8; i++) {
            menu.setItem(i, pane);
        }
        menu.setItem(4, Auction.item);
        return menu;
    }

    @EventHandler
    public static void onClick(InventoryClickEvent e) {
        if (e.getInventory().getHolder() instanceof GUIAuction) {
            e.setCancelled(true);
        }
    }
}
