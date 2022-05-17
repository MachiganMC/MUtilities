package be.machigan.mutilities.prout;

import be.machigan.mutilities.MUtilities;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandProut implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("prout.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.prout.use")) {
            commandSender.sendMessage(Tools.configColor("prout.message.no perm.prout use").replace("{prefix}", Prout.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.prout.use"));
            return true;
        }
        if ((commandSender instanceof  Player) && Prout.COOLDOWN.contains(((Player) commandSender).getUniqueId().toString())) {
            commandSender.sendMessage(Tools.configColor("prout.message.wait").replace("{prefix}", Prout.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }
        if (Const.CONFIG.getInt("prout.min player") > Bukkit.getOnlinePlayers().size()) {
            commandSender.sendMessage(Tools.configColor("prout.message.not enough player").replace("{prefix}", Prout.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{min}", Integer.toString(Const.CONFIG.getInt("prout.min player"))));
            return true;
        }
        if (commandSender instanceof Player) {
            Prout.COOLDOWN.add(((Player) commandSender).getUniqueId().toString());
            Bukkit.getScheduler().scheduleSyncDelayedTask(MUtilities.getInstance(), () ->
                    Prout.COOLDOWN.remove(((Player) commandSender).getUniqueId().toString()), Const.CONFIG.getInt("prout.cooldown") * 20 * 60L);
        }
        Prout.prout();
        return true;
    }
}
