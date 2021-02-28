package com.buha007.spigot.bankmanager;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

public class PlaceListener implements Listener {

    private Main main;

    public PlaceListener(Main instance) {
        main = instance;
    }

    @EventHandler
    public void onBanknotePlace(BlockPlaceEvent e) {
        if (e.getBlock().getType().equals(Main.banknoteMaterial)) {
            if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta()
                    .getEnchantLevel(Enchantment.ARROW_INFINITE) != 0) {
                e.setCancelled(true);
                FileConfiguration cfg = main.getConfig();
                Main.msg(e.getPlayer(), cfg.getString("prefix") + cfg.getString("placeBanknoteDeny"));
            }
        }
    }

}