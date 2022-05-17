package be.machigan.mutilities.notif;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.hover.content.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Notification {
    public static String PREFIX;
    public static List<String> LIST_NOTIF;
    public static int DELAY;

    public static int TASK_ID = -1;

    static {
        init();
    }

    public static void init() {
        PREFIX = Tools.configColor("notification.prefix");
        LIST_NOTIF = new ArrayList<>(Const.CONFIG.getStringList("notification.all notif"));
        DELAY = Const.CONFIG.getInt("notification.delay");

        if (LIST_NOTIF.size() == 0) {
            Tools.log("No notif has been wrote", Const.LOG_WARNING);
            Tools.log("Notification module disabled");
        }
        if (TASK_ID != -1) {
            Bukkit.getScheduler().cancelTask(TASK_ID);
            launch();
        }
    }

    public static void launch() {
        if (!Const.CONFIG.getBoolean("notification.enabled")) {
            return;
        }

        TASK_ID = Bukkit.getScheduler().scheduleSyncRepeatingTask(MUtilities.getInstance(), () -> {
            String notif = LIST_NOTIF.get(new Random().nextInt(LIST_NOTIF.size()));
            if (Const.CONFIG.getBoolean("notification.enabled")) {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    TextComponent message = new TextComponent(Tools.configColor("notification.build." + notif + ".message").replace("{prefix}", PREFIX)
                            .replace("{player}", player.getName()));
                    if (Const.CONFIG.getString("notification.build." + notif + ".hover") != null) {
                        message.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new Text(
                                Tools.configColor("notification.build." + notif + ".hover").replace("{prefix}", PREFIX).replace("{player}", player.getName())
                        )));
                    }
                    if (Const.CONFIG.getString("notification.build." + notif + ".click") != null) {
                        message.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, Const.CONFIG.getString("notification.build." + notif + ".click")
                                .replace("{prefix}", PREFIX).replace("{player}", player.getName())));
                    }
                    player.spigot().sendMessage(message);
                }
            } else {
                Bukkit.getScheduler().cancelTask(TASK_ID);
                Tools.log("The repeating task of notif module has been canceled, because the module is disable");
            }
        }, (DELAY + 1) * 60 *20L, DELAY * 60 * 20L);
    }

}
