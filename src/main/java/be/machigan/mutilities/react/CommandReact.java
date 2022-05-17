package be.machigan.mutilities.react;

import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;
public class CommandReact implements CommandExecutor {
    private static final int ELEMENT_PAGE = 10;

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!Const.CONFIG.getBoolean("reaction.enabled")) {
            return true;
        }
        if (!commandSender.hasPermission("mutils.reaction.use")) {
            commandSender.sendMessage(Tools.configColor("reaction.message.no perm.reaction use").replace("{prefix}", Reaction.PREFIX)
                    .replace("{perm}", "mutils.reaction.use"));
            return true;
        }

        if (strings.length == 0) {
            commandSender.sendMessage(Tools.replaceColor(Const.CONFIG.getString("reaction.message.miss argument").replace("{prefix}", Reaction.PREFIX)
                    .replace("sender}", commandSender.getName())));
            return true;
        }

        if (strings[0].equalsIgnoreCase("top")) {
            if (!commandSender.hasPermission("mutils.reaction.see.top")) {
                commandSender.sendMessage(
                        Tools.configColor("reaction.message.no perm.reaction see top").replace("{perm}", "mutils.reaction.see.top")
                                .replace("{sender}", commandSender.getName()));
                return true;
            }
            if (strings.length < 2) {
                Bukkit.dispatchCommand(commandSender, "react top 1");
                return true;
            }

            int maxPage;
            FileConfiguration config = YamlConfiguration.loadConfiguration(Reaction.FILE);
            List<String> allPlayer = config.getStringList("all player");
            if (allPlayer.isEmpty()) {
                commandSender.sendMessage(
                        Tools.configColor("reaction.message.arg.top.empty list").replace("{prefix}", Reaction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{putpage}", strings[1]));
                return true;
            }
            if (allPlayer.size() % ELEMENT_PAGE == 0) {
                maxPage = allPlayer.size() / ELEMENT_PAGE;
            } else {
                maxPage = (allPlayer.size() / ELEMENT_PAGE) + 1;
            }

            try {
                int page = Integer.parseInt(strings[1]);
                if (page <= 0) {
                    commandSender.sendMessage(Tools.configColor("reaction.message.arg.top.not valid page").replace("{prefix}", Reaction.PREFIX)
                            .replace("{sender}", commandSender.getName()).replace("{maxpage}", Integer.toString(maxPage))
                            .replace("{putptage}", Integer.toString(page)));
                    return true;
                }
                if (page > maxPage) {
                    commandSender.sendMessage(Tools.configColor("reaction.message.arg.top.max page").replace("{prefix}", Reaction.PREFIX)
                            .replace("{maxpage}", Integer.toString(maxPage)).replace("{putpage}", Integer.toString(page)));
                    return true;
                }

                allPlayer.sort((String o1, String o2) -> {
                      if (config.getInt("player." + o1 + ".win.pos 1") < config.getInt("player." + o2 + ".win.pos 1")) {return 1;}
                      if (config.getInt("player." + o1 + ".win.pos 1") > config.getInt("player." + o2 + ".win.pos 1")) {return -1;}
                      return 0;
                });
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.top.success").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{maxpage}", Integer.toString(maxPage))
                        .replace("{putpage}", Integer.toString(page)));
                if (allPlayer.size() < (ELEMENT_PAGE * page)) {
                    for (int i = (ELEMENT_PAGE * (page-1)); i < allPlayer.size(); i++) {
                        int pos1 = config.getInt("player." + allPlayer.get(i) + ".win.pos 1");
                        int pos2 = config.getInt("player." + allPlayer.get(i) + ".win.pos 2");
                        int pos3 = config.getInt("player." + allPlayer.get(i) + "win.pos 3");
                        String name = Bukkit.getOfflinePlayer(UUID.fromString(allPlayer.get(i))).getName();
                        commandSender.sendMessage(Tools.configColor("reaction.message.arg.top.line").replace("{prefix}", Reaction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{maxpage}", Integer.toString(maxPage))
                                .replace("{putpage}", Integer.toString(page)).replace("{player}", name)
                                .replace("{avg time}", Double.toString(config.getDouble("player." + allPlayer.get(i) + ".average time")))
                                .replace("{pos1}", Integer.toString(pos1)).replace("{pos2}", Integer.toString(pos2))
                                .replace("{pos3}", Integer.toString(pos3)).replace("{total part}", Integer.toString(pos1 + pos2 + pos3))
                                .replace("{pos}", Integer.toString(i+1)));
                    }
                } else {
                    for (int i = (ELEMENT_PAGE * (page-1)); i < (ELEMENT_PAGE* page); i++) {
                        int pos1 = config.getInt("player." + allPlayer.get(i) + ".win.pos 1");
                        int pos2 = config.getInt("player." + allPlayer.get(i) + ".win.pos 2");
                        int pos3 = config.getInt("player." + allPlayer.get(i) + "win.pos 3");
                        String name = Bukkit.getOfflinePlayer(UUID.fromString(allPlayer.get(i))).getName();
                        commandSender.sendMessage(Tools.configColor("reaction.message.arg.top.line").replace("{prefix}", Reaction.PREFIX)
                                .replace("{sender}", commandSender.getName()).replace("{maxpage}", Integer.toString(maxPage))
                                .replace("{putpage}", Integer.toString(page)).replace("{player}", name)
                                .replace("{avg time}", Double.toString(config.getDouble("player." + allPlayer.get(i) + ".average time")))
                                .replace("{pos1}", Integer.toString(pos1)).replace("{pos2}", Integer.toString(pos2))
                                .replace("{pos3}", Integer.toString(pos3)).replace("{total part}", Integer.toString(pos1 + pos2 + pos3))
                                .replace("{pos}", Integer.toString(i+1)));
                    }
                }
                return true;


            } catch (NumberFormatException ignored) {
                commandSender.sendMessage(
                        Tools.configColor("reaction.message.arg.top.not valid page").replace("{prefix}", Reaction.PREFIX)
                                .replace("{maxpage}", Integer.toString(maxPage)).replace("{sender}", command.getName())
                                .replace("{putpage}", strings[1]));
                return true;
            }


        }

        if (strings[0].equalsIgnoreCase("start")) {
            if (!commandSender.hasPermission("mutils.reaction.start")) {
                commandSender.sendMessage(Tools.configColor("reaction.message.no perm.reaction start").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.reaction.start"));
                return true;
            }
            if (Reaction.IN_GAME) {
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.start.already react").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }

            Reaction.newGame();
            commandSender.sendMessage(Tools.configColor("reaction.message.arg.start.success").replace("{prefix}", Reaction.PREFIX)
                    .replace("{sender}", commandSender.getName()));
            return true;
        }

        if (strings[0].equalsIgnoreCase("me")) {
            if (!(commandSender instanceof Player)) {
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.me.only player").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }

            if (!commandSender.hasPermission("mutils.reaction.see.me")) {
                commandSender.sendMessage(Tools.configColor("reaction.message.no perm.reaction see me").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.reaction.see.me"));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Reaction.FILE);
            if (!config.getStringList("all player").contains(((Player) commandSender).getUniqueId().toString())) {
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.me.not found").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            String player = ((Player) commandSender).getUniqueId().toString();
            double avgTime = config.getDouble("player." + player + ".average time");
            int pos1 = config.getInt("player." + player + ".win.pos 1");
            int pos2 = config.getInt("player." + player + ".win.pos 2");
            int pos3 = config.getInt("player." + player + ".win.pos 3");
            for (String line : Const.CONFIG.getStringList("reaction.message.arg.me.success")) {
                line = Tools.replaceColor(line.replace("{prefix}", Reaction.PREFIX).replace("{sender}", commandSender.getName())
                        .replace("{avg time}", Double.toString(avgTime)).replace("{pos1}", Integer.toString(pos1))
                        .replace("{pos2}", Integer.toString(pos2)).replace("{pos3}", Integer.toString(pos3))
                        .replace("{total part}", Integer.toString(pos1 + pos2 + pos3)));
                commandSender.sendMessage(line);
            }
            return true;


        }

        if (strings[0].equalsIgnoreCase("info")) {
            if (!commandSender.hasPermission("mutils.reaction.see.info")) {
                commandSender.sendMessage(Tools.configColor("reaction.message.no perm.reaction see info").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{perm}", "mutils.reaction.see.info"));
                return true;
            }
            if (strings.length < 2) {
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.info.miss arg").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()));
                return true;
            }
            FileConfiguration config = YamlConfiguration.loadConfiguration(Reaction.FILE);
            String uuid = Bukkit.getOfflinePlayer(strings[1]).getUniqueId().toString();
            if (!config.getStringList("all player").contains(uuid)) {
                commandSender.sendMessage(Tools.configColor("reaction.message.arg.info.not found").replace("{prefix}", Reaction.PREFIX)
                        .replace("{sender}", commandSender.getName()).replace("{player}", strings[1]));
                return true;
            }
            OfflinePlayer player = Bukkit.getOfflinePlayer(UUID.fromString(uuid));
            double avgTime = config.getDouble("player." + uuid + ".average time");
            int pos1 = config.getInt("player." + uuid + ".win.pos 1");
            int pos2 = config.getInt("player." + uuid + ".win.pos 2");
            int pos3 = config.getInt("player." + uuid + ".win.pos 3");
            for (String line : Const.CONFIG.getStringList("reaction.message.arg.info.success")) {
                line = Tools.replaceColor(line.replace("{prefix}", Reaction.PREFIX).replace("{player}", player.getName())
                        .replace("{avg time}", Double.toString(avgTime)).replace("{pos1}", Integer.toString(pos1))
                        .replace("{pos2}", Integer.toString(pos2)).replace("{pos3}", Integer.toString(pos3))
                        .replace("{total part}", Integer.toString(pos1 + pos2 + pos3)));
                commandSender.sendMessage(line);
            }
            return true;


        }


        commandSender.sendMessage(Tools.configColor("reaction.message.no command found").replace("{prefix}", Reaction.PREFIX
                .replace("{sender}", commandSender.getName())));
        return true;
    }
}
