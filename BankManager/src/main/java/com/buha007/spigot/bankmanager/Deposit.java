package com.buha007.spigot.bankmanager;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Deposit implements CommandExecutor {

    private Main main;

    public Deposit(Main instance) {
        main = instance;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!cmd.getName().equalsIgnoreCase("deposit"))
            return true;

        FileConfiguration cfg = main.getConfig();

        if (!sender.hasPermission("bankmanager.deposit")) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("noPermission"));
            return true;
        }

        if (args.length != 1) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notEnoughArgsDeposit"));
            return true;
        }

        Player player = (Player) sender;
        ItemStack itemInHand = player.getInventory().getItemInMainHand();

        double value = getBanknoteValue(itemInHand);
        if (value == -1) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notBanknote"));
            return true;
        }

        int amountToDeposit = 0;
        try {
            amountToDeposit = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            Main.msg(sender, cfg.getString("prefix") + cfg.getString("notANumber"));
            return true;
        }
        if (amountToDeposit < 0)
            amountToDeposit *= -1;

        int amount = itemInHand.getAmount();
        if (amount < amountToDeposit) {
            Main.msg(player, cfg.getString("prefix") + cfg.getString("notEnoughBanknotesToDeposit"));
            return true;
        }

        if (value * amountToDeposit + Main.economy.getBalance(player) > cfg.getDouble("maxMoney")) {
            Main.msg(player, cfg.getString("prefix") + cfg.getString("accountFull"));
            return true;
        }

        player.getInventory().getItemInMainHand().setAmount(amount - amountToDeposit);
        Main.economy.depositPlayer(player, Math.abs(value * amountToDeposit));
        Main.msg(sender, cfg.getString("prefix") + cfg.getString("success"));
        return true;
    }

    private double getBanknoteValue(ItemStack item) {
        String value = "none";
        try {
            for (String string : item.getItemMeta().getLore()) {
                if (string.contains("Value")) {
                    value = string;
                    break;
                }
            }
        } catch (NullPointerException e) {
            return -1;
        }

        if (value.equals("none"))
            return -1;

        char[] number = value.toCharArray();
        int size = value.length();
        String result = "";
        int i = 0;
        while (number[i] != ' ' && i < size)
            ++i;

        while (i < size) {
            if (".0123456789".contains("" + number[i]))
                result += number[i];
            ++i;
        }

        try {
            return Double.parseDouble(result);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

}