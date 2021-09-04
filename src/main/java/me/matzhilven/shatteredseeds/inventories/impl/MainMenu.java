package me.matzhilven.shatteredseeds.inventories.impl;

import me.matzhilven.shatteredseeds.farm.Farm;
import me.matzhilven.shatteredseeds.inventories.Menu;
import me.matzhilven.shatteredseeds.utils.ItemBuilder;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class MainMenu extends Menu {

    private final HashMap<Integer, String> slots;
    private final Farm farm;

    public MainMenu(Player p, Farm farm) {
        super(p);
        this.farm = farm;
        slots = new HashMap<>();
    }

    @Override
    public String getMenuName() {
        return main.getGuiConfig().getString("main.title").replace("%farm%", farm.getName());
    }

    @Override
    public int getSlots() {
        return main.getGuiConfig().getInt("main.size");
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (!slots.containsKey(e.getSlot())) return;

        switch (slots.get(e.getSlot())) {
            case "storage":
                new StorageMenu(p, farm).open();
                break;
            case "upgrades":
                new UpgradesMenu(p, farm).open();
                break;
        }
    }

    @Override
    public void setMenuItems() {
        main.getGuiConfig().getConfigurationSection("main.items").getKeys(false).forEach(item -> {
            inventory.setItem(
                    main.getGuiConfig().getInt("main.items." + item + ".slot"),
                    new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("main.items." + item + ".material")))
                            .setName(main.getGuiConfig().getString("main.items." + item + ".name"))
                            .setLore(main.getGuiConfig().getStringList("main.items." + item + ".lore"))
                            .replace("%name%", StringUtils.colorize(farm.getName()))
                            .replace("%speed%", String.valueOf(farm.getUpgrades().getSpeed()))
                            .replace("%items%", String.valueOf(farm.getUpgrades().getItemsPerHarvest()))
                            .replace("%capacity%", String.valueOf(farm.getUpgrades().getStorageSpace()))
                            .toItemStack()
            );

            slots.put(main.getGuiConfig().getInt("main.items." + item + ".slot"), item);
        });


        if (main.getGuiConfig().getBoolean("main.filler.enabled")) {
            setFillerGlass(new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("main.filler.material")))
                    .setName(main.getGuiConfig().getString("main.filler.name"))
                    .setLore(main.getGuiConfig().getStringList("main.filler.lore"))
                    .toItemStack());
        }
    }
}
