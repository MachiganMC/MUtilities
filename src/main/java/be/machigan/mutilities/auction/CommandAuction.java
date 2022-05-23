package be.machigan.mutilities.auction;

import be.machigan.mutilities.survey.GUIAuction;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;
import java.util.UUID;

public class CommandAuction implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("auction.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.auction.use")) {
            commandSender.sendMessage(Tools.configColor("auction.message.no perm.auction use").replace("{prefix}", Auction.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.auction.use"));
            return true;
        }
        if (strings.length < 1) {
            commandSender.sendMessage(Auction.auctionMessage());
            return true;
        }

        if (strings[0].equalsIgnoreCase("create")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.only player").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!commandSender.hasPermission("mutils.auction.create")) {
                commandSender.sendMessage(Tools.configColor("auction.message.no perm.auction create").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.auction.create"));
                return true;
            }
            if (Auction.inAuction) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.already auction").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            ItemStack item = ((Player) commandSender).getInventory().getItemInMainHand();
            if (item.getType().equals(Material.AIR)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.no item").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
            if (config.getStringList("all player").contains(((Player) commandSender).getUniqueId().toString())) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.claim").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName())
                        .replace("{item}", config.getItemStack("claim." + ((Player) commandSender).getUniqueId()).toString()));
            }
            if (Auction.BLACK_LIST.contains(item.getType().toString())) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.blacklisted").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", command.getName()).replace("{item}", item.getType().toString()));
                return true;
            }

            if (strings.length >= 2) {
                try {
                    double price;
                    price = Double.parseDouble(strings[1]);
                    price = (double) (Math.round(price * 100) / 100);
                    if (price < 0) {
                        commandSender.sendMessage(Tools.configColor("auction.message.invalid price").replace("{prefix}", Auction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price))
                                .replace("{item}", item.toString()));
                        return true;
                    }
                    try {
                        Auction.create(((Player) commandSender).getUniqueId().toString(), item, price);
                        ((Player) commandSender).getInventory().remove(item);
                        commandSender.sendMessage(Tools.configColor("auction.message.arg.create.success").replace("{prefix}", Auction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price))
                                .replace("{item}", item.toString()));
                        Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.create.announce").replace("{prefix}", Auction.PREFIX)
                                .replace("{item}", item.toString()).replace("{price}", Double.toString(price)).replace("{creator}", command.getName()));
                        return true;
                    } catch (IOException | ParseException e) {
                        commandSender.sendMessage(Tools.configColor("auction.message.arg.create.intern error").replace("{prefix}", Auction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price))
                                .replace("{item}", item.toString()));
                        return true;
                    }
                } catch (NumberFormatException ignored) {
                    commandSender.sendMessage(Tools.configColor("auction.message.arg.create.invalid price").replace("{prefix}", Auction.PREFIX)
                            .replace("{sender}", commandSender.getName()).replace("{price}", strings[1])
                            .replace("{item}", item.toString()));
                    return true;
                }
            }

            try {
                Auction.create(((Player) commandSender).getUniqueId().toString(), item);
                ((Player) commandSender).getInventory().remove(item);
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.success").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{item}", item.toString())
                        .replace("{price}", Double.toString(0)));
                Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.create.announce").replace("{prefix}", Auction.PREFIX)
                        .replace("{item}", item.toString()).replace("{price}", Double.toString(0)).replace("{creator}", commandSender.getName()));
                return true;
            } catch (IOException | ParseException e) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.create.intern error").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{item}", item.toString())
                        .replace("{price}", Double.toString(0)));
            }


        }

        if (strings[0].equalsIgnoreCase("item")) {
            if (!commandSender.hasPermission("mutils.auction.item")) {
                commandSender.sendMessage(Tools.configColor("auction.message.no perm.auction item").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.auction.item"));
                return true;
            }
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.info.only player").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!Auction.inAuction) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.info.no auction").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            ((Player) commandSender).openInventory(new GUIAuction().getInventory());
            return true;
        }

        if (strings[0].equalsIgnoreCase("overbid")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.only player").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (!commandSender.hasPermission("mutils.auction.overbid")) {
                commandSender.sendMessage(Tools.configColor("auction.message.no perm.auction overbid").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.auction.overbid"));
                return true;
            }
            if (!Auction.inAuction) {
                commandSender.sendMessage(Tools.configColor("auction.message.no auction").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (((Player) commandSender).getUniqueId().toString().equals(Auction.creator)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.creator").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
            if (config.getStringList("all player").contains(((Player) commandSender).getUniqueId().toString())) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.claim").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName())
                        .replace("{item}", config.getItemStack("claim." + ((Player) commandSender).getUniqueId()).toString()));
            }
            if (((Player) commandSender).getUniqueId().toString().equals(Auction.bidder)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.already bidder").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.no bid").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            double price;
            try {
                price = Double.parseDouble(strings[1]);
            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.invalid price").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{price}", strings[1]));
                return true;
            }
            price = (double) (Math.round(price * 100) / 100);
            if (!Tools.checkBalance((Player) commandSender, price)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.not enough money").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price)));
                return true;
            }
            if (price <= Auction.bid) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.bigger price").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price))
                        .replace("{bid}", Double.toString(Auction.bid)));
                return true;
            }
            if (!Tools.withdraw((Player) commandSender, price)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.intern error").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{bid}", Double.toString(Auction.bid))
                        .replace("{price}", Double.toString(price)));
                return true;
            }
            try {
                Auction.overbid(((Player) commandSender).getUniqueId().toString(), price);
            } catch (IOException ignored) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.overbid.intern error").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{bid}", Double.toString(Auction.bid))
                        .replace("{price}", Double.toString(price)));
                return true;
            }
            Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.overbid.success").replace("{prefix}", Auction.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{price}", Double.toString(price)));
            return true;

        }

        if (strings[0].equalsIgnoreCase("claim")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.claim.only player").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            Player player = (Player) commandSender;
            FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
            if (!config.getStringList("all player").contains(player.getUniqueId().toString())) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.claim.no claim").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            ItemStack item = config.getItemStack("claim." + player.getUniqueId());
            List<String> allPlayer = config.getStringList("all player");
            allPlayer.remove(player.getUniqueId().toString());
            config.set("all player", allPlayer);
            config.set("claim." + player.getUniqueId(), null);
            try {
                config.save(Auction.FILE);
            } catch (IOException ignored) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.claim.intern error").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{item}", item.toString()));
                return true;
            }
            player.getInventory().addItem(item);
            commandSender.sendMessage(Tools.configColor("auction.message.arg.claim.success").replace("{prefix}", Auction.PREFIX)
                    .replace("{sender}", commandSender.getName()).replace("{item}", item.toString()));
            return true;

        }

        if (strings[0].equalsIgnoreCase("stop")) {
            if (!commandSender.hasPermission("mutils.auction.stop")) {
                commandSender.sendMessage(Tools.configColor("auction.message.no perm.auction stop").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.auction.stop"));
                return true;
            }
            if (!Auction.inAuction) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.no auction").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Auction.FILE);
            List<String> allPlayer = config.getStringList("all player");
            if (!allPlayer.contains(Auction.creator)) {
                allPlayer.add(Auction.creator);
                config.set("all player", allPlayer);
            }
            if (Auction.bidder != null) {
                if (!Tools.pay(Bukkit.getOfflinePlayer(UUID.fromString(Auction.bidder)), Auction.bid)) {
                    commandSender.sendMessage(Tools.configColor("auction.message.arg.stop.payment error").replace("{prefix}", Auction.PREFIX)
                            .replace("{sender}", commandSender.getName()).replace("{bid}", Double.toString(Auction.bid))
                            .replace("{bidder}", Bukkit.getOfflinePlayer(UUID.fromString(Auction.bidder)).getName()));
                }
            }
            config.set("claim." + Auction.creator, Auction.item);
            config.set("actual", null);
            try {
                config.save(Auction.FILE);
            } catch (IOException ignored) {
                commandSender.sendMessage(Tools.configColor("auction.message.arg.stop.intern error").replace("{prefix}", Auction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            TimerAuction.TIMER.cancel();
            Auction.inAuction = false;
            Bukkit.getServer().broadcastMessage(Tools.configColor("auction.message.arg.stop.success").replace("{prefix}", Auction.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }



        commandSender.sendMessage(Tools.configColor("auction.message.bad argument").replace("{prefix}", Auction.PREFIX)
                .replace("{sender}", commandSender.getName()));
        return true;
    }
}
