package be.machigan.mutilities.prout;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Prout {

    public static List<String> PROUT_LIST;
    public static String PREFIX;
    final public static List<String> COOLDOWN = new ArrayList<>();

    static {
        init();
    }

    public static void init() {
        PREFIX = Tools.configColor("prout.prefix");
        PROUT_LIST = Const.CONFIG.getStringList("prout.list");
    }

    public static void prout() {
        String prout = Tools.replaceColor(PROUT_LIST.get(new Random().nextInt(PROUT_LIST.size())));
        Player player = (Player) Bukkit.getOnlinePlayers().toArray()[new Random().nextInt(Bukkit.getOnlinePlayers().size())];
        Player sender = (Player) Bukkit.getOnlinePlayers().toArray()[new Random().nextInt(Bukkit.getOnlinePlayers().size())];
        sender.chat(prout.replace("{random player}", player.getDisplayName()));
    }




}
