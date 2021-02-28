package com.buha007.spigot.bankmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Reload implements CommandExecutor {

    private Main main;

    public Reload(Main instance) {
        main = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("bankmanagerreload"))
            return true;

        if (!sender.hasPermission("bankmanager.reload")) {
            Main.msg(sender, main.getConfig().getString("prefix") + main.getConfig().getString("noPermission"));
            return true;
        }

        main.reloadConfig();
        main.createBanknoteTemplate();
        Main.msg(sender, "&9&lBank&3&lManager &8&l> &7Config reloaded&8!");
        return true;
    }

}