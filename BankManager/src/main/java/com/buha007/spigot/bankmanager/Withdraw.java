package com.buha007.spigot.bankmanager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Withdraw implements CommandExecutor {

    private Main main;

    public Withdraw(Main instance) {
        main = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("withdraw"))
            return true;

        FileConfiguration cfg = main.getConfig();

        if (!sender.hasPermission("bankmanager.withdraw") || !(sender instanceof Player)) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("noPermission"));
            return true;
        }

        if (args.length != 2) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notEnoughArgsWithdraw"));
            return true;
        }

        double value = 0;
        try {
            value = Double.parseDouble(args[0]);
        } catch (NumberFormatException e) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notANumber"));
            return true;
        }

        int amount = 0;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (NumberFormatException e) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notANumber"));
            return true;
        }

        if (value < cfg.getDouble("minWithdrawAmount")) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notMinWithdrawAmount"));
            return true;
        }

        if (value * amount > Main.economy.getBalance((Player) sender)) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notEnoughMoney"));
            return true;
        }

        ItemStack banknote = new ItemStack(Main.banknoteTemplate);
        ItemMeta meta = banknote.getItemMeta();
        List<String> newLore = new ArrayList<String>();
        for (String s : meta.getLore())
            if (!s.contains("{MONEY}"))
                newLore.add(s);
            else
                newLore.add(s.replace("{MONEY}", "" + value));
        meta.setLore(newLore);
        banknote.setItemMeta(meta);
        banknote.setAmount(amount);

        ((Player) sender).getInventory().addItem(banknote);
        Main.economy.withdrawPlayer((Player) sender, Math.abs(value * amount));
        Main.msg(sender, cfg.getString("prefix") + cfg.getString("success"));

        return true;
    }
}
