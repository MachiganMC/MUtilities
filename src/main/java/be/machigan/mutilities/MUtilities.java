package be.machigan.mutilities;

import be.machigan.mutilities.auction.Auction;
import be.machigan.mutilities.auction.CommandAuction;
import be.machigan.mutilities.auction.OnJoinAuction;
import be.machigan.mutilities.auction.TabAuction;
import be.machigan.mutilities.colorname.Colorname;
import be.machigan.mutilities.colorname.CommandColorname;
import be.machigan.mutilities.colorname.ReloadColorname;
import be.machigan.mutilities.colorname.TabColorname;
import be.machigan.mutilities.command.CommandMutils;
import be.machigan.mutilities.notif.Notification;
import be.machigan.mutilities.prout.CommandProut;
import be.machigan.mutilities.prout.Prout;
import be.machigan.mutilities.prout.TabProut;
import be.machigan.mutilities.react.CommandReact;
import be.machigan.mutilities.react.EventReact;
import be.machigan.mutilities.react.Reaction;
import be.machigan.mutilities.react.TabReact;
import be.machigan.mutilities.salary.CommandSalary;
import be.machigan.mutilities.salary.Salary;
import be.machigan.mutilities.salary.TabSalary;
import be.machigan.mutilities.survey.CommandSurvey;
import be.machigan.mutilities.survey.GUIAuction;
import be.machigan.mutilities.survey.Survey;
import be.machigan.mutilities.survey.TabSurvey;
import be.machigan.mutilities.tab.TabMutils;
import be.machigan.mutilities.task.CommandTask;
import be.machigan.mutilities.task.EventTask;
import be.machigan.mutilities.task.TabTask;
import be.machigan.mutilities.task.Task;
import be.machigan.mutilities.utils.Const;
import be.machigan.mutilities.utils.Tools;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class MUtilities extends JavaPlugin {
    private static MUtilities plugin;
    final private static String[] MODULES = new String[] {"notification", "reaction", "salary", "survey", "colorname", "task", "auction", "prout"};
    final public static List<String> LIST_MODULE = new ArrayList<>(Arrays.asList(MODULES));

    @Override
    public void onEnable() {
        plugin = this;
        Tools.log("Enabled");

        getCommand("mutils").setExecutor(new CommandMutils());
        getCommand("mutils").setTabCompleter(new TabMutils());

        getCommand("reaction").setExecutor(new CommandReact());
        getCommand("reaction").setTabCompleter(new TabReact());
        getServer().getPluginManager().registerEvents(new EventReact(), this);

        getCommand("salary").setExecutor(new CommandSalary());
        getCommand("salary").setTabCompleter(new TabSalary());

        getCommand("survey").setExecutor(new CommandSurvey());
        getCommand("survey").setTabCompleter(new TabSurvey());

        getCommand("colorname").setExecutor(new CommandColorname());
        getCommand("colorname").setTabCompleter(new TabColorname());
        getServer().getPluginManager().registerEvents(new ReloadColorname(), this);

        getCommand("task").setExecutor(new CommandTask());
        getCommand("task").setTabCompleter(new TabTask());
        getServer().getPluginManager().registerEvents(new EventTask(), this);

        getCommand("prout").setExecutor(new CommandProut());
        getCommand("prout").setTabCompleter(new TabProut());

        getCommand("auction").setExecutor(new CommandAuction());
        getCommand("auction").setTabCompleter(new TabAuction());
        getServer().getPluginManager().registerEvents(new GUIAuction(), this);
        getServer().getPluginManager().registerEvents(new OnJoinAuction(), this);
        if (Const.CONFIG.getBoolean("reaction.enabled")) {
            if (Reaction.REACTIONS_GAME.isEmpty()) {
                Tools.log("No reaction game has been record", Const.LOG_WARNING);
                Tools.log("Reaction module disabled");
                Const.CONFIG.set("reaction.enable", false);
                MUtilities.getInstance().saveConfig();
                updateConfig();
            } else {
                Tools.log("Reaction module enabled");
                Reaction.launch();
            }
        } else {
            Tools.log("Reaction module disabled");
        }
        if (Const.CONFIG.getBoolean("notification.enabled")) {
            if (Notification.LIST_NOTIF.isEmpty()) {
                Tools.log("No notif has been record", Const.LOG_WARNING);
                Tools.log("Notification module disabled");
                Const.CONFIG.set("notification.enabled", false);
                MUtilities.getInstance().saveConfig();
                MUtilities.updateConfig();
            } else {
                Tools.log("Notification module enabled");
                Notification.launch();
            }
        } else {
            Tools.log("Notification module disabled");
        }

        if (Const.CONFIG.getBoolean("salary.enabled")) {
            if (Salary.SALARIES_LIST.isEmpty()) {
                Tools.log("No salary has been record");
                Tools.log("Salary module disabled");
                Const.CONFIG.set("salary.enabled", false);
                MUtilities.getInstance().saveConfig();
                MUtilities.updateConfig();
            } else {
                Tools.log("Salary module enabled");
                Salary.init();
            }
        } else {
            Tools.log("Salary module disabled");
        }

        if (Const.CONFIG.getBoolean("survey.enabled")) {
            Tools.log("Survey module enabled");
            Survey.init();
        } else {
            Tools.log("Survey module disabled");
        }

        if (Const.CONFIG.getBoolean("colorname.enabled")) {
            Tools.log("Colorname module enabled");
            Colorname.init();
        } else {
            Tools.log("Colorname module disabled");
        }

        if (Const.CONFIG.getBoolean("task.enabled")) {
            if (Const.CONFIG.getStringList("task.list").isEmpty()) {
                Tools.log("No task has been recorded", Const.LOG_WARNING);
                Tools.log("Disabling the task module");
                Const.CONFIG.set("task.enabled", false);
                MUtilities.getInstance().saveConfig();
            } else {
                Tools.log("Task module enabled");
                Task.init();
            }
        } else {
            Tools.log("Task module disabled");
        }

        if (Const.CONFIG.getBoolean("prout.enabled")) {
            if (Const.CONFIG.getStringList("prout.list").size() == 0) {
                Tools.log("No prout has been recorded", Const.LOG_WARNING);
                Tools.log("Disabling the prout module");
                Const.CONFIG.set("prout.enabled", false);
                MUtilities.getInstance().saveConfig();
            } else {
                Tools.log("Prout module enabled");
                Prout.init();
            }
        } else {
            Tools.log("prout module disabled");
        }

        if (Const.CONFIG.getBoolean("auction.enabled")) {
            Tools.log("Auction module enabled");
            Auction.init();
        } else {
            Tools.log("Auction module disabled");
        }

    }

    @Override
    public void onDisable() {
        Tools.log("Disabled");
    }

    public static void updateConfig() {
        if (Const.CONFIG.getBoolean("reaction.enabled")) {
            Reaction.init();
        }
        if (Const.CONFIG.getBoolean("notification.enabled")) {
            Notification.init();
        }
        if (Const.CONFIG.getBoolean("salary.enabled")) {
            Salary.init();
        }
        if (Const.CONFIG.getBoolean("survey.enabled")) {
            Survey.init();
        }
        if (Const.CONFIG.getBoolean("colorname.enabled")) {
            Colorname.init();
        }
        if (Const.CONFIG.getBoolean("auction.enabled")) {
            Auction.init();
        }
        if (Const.CONFIG.getBoolean("prout.enabled")) {
            Prout.init();
        }
        if (Const.CONFIG.getBoolean("task.enabled")) {
            Task.init();
        }
        Tools.log("The config has been updated");
    }


    public static MUtilities getInstance() {
        return plugin;
    }



}
