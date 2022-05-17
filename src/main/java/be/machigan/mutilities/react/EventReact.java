package be.machigan.mutilities.react;

import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class EventReact implements Listener {

    @EventHandler
    public static void onChat(AsyncPlayerChatEvent e) {


        if (!Reaction.IN_GAME) {
            return;
        }
        if (!e.getMessage().equalsIgnoreCase(Reaction.WORD)) {
            return;
        }
        if (Reaction.WINNER.containsKey(e.getPlayer().getUniqueId().toString())) {
            return;
        }
        e.setCancelled(true);
        double time = (double) Math.round((double) (System.currentTimeMillis() - Reaction.START_TIME) / 1000 * 100) / 100;
        String winMessage = Reaction.WIN.replace("{player}", e.getPlayer().getName()).replace("{time}", "" + time);
        Bukkit.getServer().broadcastMessage(winMessage);
        Reaction.WINNER.put(e.getPlayer().getUniqueId().toString(), time);

        switch (Reaction.WINNER.size()) {
            case 1: {
                Tools.sudo(Reaction.COMMAND_FIRST.replace("{player}" , e.getPlayer().getName()));
                break;
            }
            case 2: {
                Tools.sudo(Reaction.COMMAND_SECOND.replace("{player}", e.getPlayer().getName()));
                break;
            }
            case 3: {
                Tools.sudo(Reaction.COMMAND_THIRD.replace("{player}", e.getPlayer().getName()));
                Reaction.finish();
            }
        }


    }
}
