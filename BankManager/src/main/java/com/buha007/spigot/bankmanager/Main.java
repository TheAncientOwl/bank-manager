package com.buha007.spigot.bankmanager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import net.milkbowl.vault.economy.Economy;

/**
 * @author Bufnita
 */

public class Main extends JavaPlugin {

    private static ConsoleCommandSender console;
    private ConfigAccessor configuration;
    public static Economy economy = null;
    public static ItemStack banknoteTemplate = null;
    public static Material banknoteMaterial = null;

    @Override
    public void onEnable() {
        configuration = new ConfigAccessor(this, "config.yml");
        console = getServer().getConsoleSender();
        printConsole("&9Bank&3Manager &bfor 1.14.4 developed by Bufnita");

        if (!setupEconomy()) {
            printConsole("&8[&4ERROR&8] &9Bank&3Manager &8-> &cVault dependency not found! Stopping BankManager!");
            return;
        } else
            printConsole("&7[&9Bank&3Manager&7] &aVault dependency found!");

        createBanknoteTemplate();

        this.getCommand("withdraw").setExecutor(new Withdraw(this));
        this.getCommand("deposit").setExecutor(new Deposit(this));
        this.getCommand("bankmanagerreload").setExecutor(new Reload(this));

        if (getConfig().getBoolean("isBanknoteMaterialBlock"))
            getServer().getPluginManager().registerEvents(new PlaceListener(this), this);
    }

    public void createBanknoteTemplate() {
        FileConfiguration cfg = configuration.getConfig();
        Material material = Material.getMaterial(cfg.getString("material"));
        try {
            banknoteTemplate = new ItemStack(material);
        } catch (IllegalArgumentException e) {
            banknoteTemplate = new ItemStack(Material.PAPER);
            printConsole(
                    "&8[&4ERROR&8] &9Bank&3Manager &8-> Banknote's material is not a valid material type; Default PAPER will be used! Change it in config.yml! (material:)");
        }
        ItemMeta meta = banknoteTemplate.getItemMeta();
        meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', cfg.getString("displayName")));
        List<String> lore = cfg.getStringList("lore");
        if (!containsPlaceholder(lore)) {
            printConsole(
                    "&8[&4ERROR&8] &9Bank&3Manager &8-> &c{MONEY} placeholder not found in banknote's lore! Adding it now! Check config.yml to change it and reload BankNotes!");
            lore.add("&8Value: &a{MONEY}&2$");
        }
        List<String> coloredLore = new ArrayList<String>();
        for (String s : lore)
            coloredLore.add(ChatColor.translateAlternateColorCodes('&', s));
        meta.setLore(coloredLore);
        banknoteTemplate.setItemMeta(meta);
        banknoteTemplate.addUnsafeEnchantment(Enchantment.ARROW_INFINITE, 1);
        banknoteMaterial = banknoteTemplate.getType();
    }

    private boolean containsPlaceholder(List<String> list) {
        for (String string : list)
            if (string.contains("{MONEY}"))
                return true;
        return false;
    }

    private boolean setupEconomy() {
        if (Bukkit.getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    public static void printConsole(String message) {
        console.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void msg(CommandSender sender, String message) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    public static void msg(Player player, String message) {
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', message));
    }

    @Override
    public FileConfiguration getConfig() {
        return configuration.getConfig();
    }

    @Override
    public void reloadConfig() {
        configuration.reloadConfig();
    }

    @Override
    public void onDisable() {

    }

}