package me.matzhilven.shatteredseeds.listeners;

import me.matzhilven.shatteredseeds.ShatteredSeeds;
import me.matzhilven.shatteredseeds.inventories.Menu;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.InventoryHolder;

public class InventoryListeners implements Listener {

    private final ShatteredSeeds main;

    public InventoryListeners(ShatteredSeeds main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }


    @EventHandler
    private void onInventoryClick(InventoryClickEvent e) {
        InventoryHolder holder = e.getInventory().getHolder();

        if (holder instanceof Menu) {
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR) return;
            ((Menu) holder).handleClick(e);
        }
    }
}
