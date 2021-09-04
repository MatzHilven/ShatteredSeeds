package me.matzhilven.shatteredseeds.inventories.impl;

import me.matzhilven.shatteredseeds.farm.Farm;
import me.matzhilven.shatteredseeds.inventories.Menu;
import me.matzhilven.shatteredseeds.utils.ItemBuilder;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.util.HashMap;

public class UpgradesMenu extends Menu {

    private final HashMap<Integer, String> slots;
    private final Farm farm;

    public UpgradesMenu(Player p, Farm farm) {
        super(p);
        this.farm = farm;
        slots = new HashMap<>();
    }

    @Override
    public String getMenuName() {
        return main.getGuiConfig().getString("upgrades.title").replace("%farm%", farm.getName());
    }

    @Override
    public int getSlots() {
        return main.getGuiConfig().getInt("upgrades.size");
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (e.getSlot() == main.getGuiConfig().getInt("upgrades.items.back.slot")) {
            new MainMenu(p, farm).open();
            return;
        }

        if (!slots.containsKey(e.getSlot())) return;

        double balance = main.getEcon().getBalance(p);
        double price = farm.getUpgrades().getPrice(slots.get(e.getSlot()));

        if (balance < price) {
            StringUtils.sendMessage(p, main.getConfig().getString("messages.invalid-funds"));
            return;
        }



    }

    @Override
    public void setMenuItems() {
        main.getGuiConfig().getConfigurationSection("upgrades.items").getKeys(false).forEach(item -> {
            inventory.setItem(
                    main.getGuiConfig().getInt("upgrades.items." + item + ".slot"),
                    new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("upgrades.items." + item + ".material")))
                            .setName(main.getGuiConfig().getString("upgrades.items." + item + ".name"))
                            .setLore(main.getGuiConfig().getStringList("upgrades.items." + item + ".lore"))
                            .replaceAll("%level%", farm.getUpgrades().getLevel(item))
                            .replaceAll("%next_level%", farm.getUpgrades().getNextLevel(item))
                            .replaceAll("%max_level%", farm.getUpgrades().getMaxLevel(item))
                            .replaceAll("%name%", StringUtils.colorize(farm.getName()))
                            .replaceAll("%price%", StringUtils.format(farm.getUpgrades().getPrice(item)))
                            .toItemStack()
            );

            slots.put(main.getGuiConfig().getInt("upgrades.items." + item + ".slot"), item);
        });

        if (main.getGuiConfig().getBoolean("upgrades.filler.enabled")) {
            setFillerGlass(new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("upgrades.filler.material")))
                    .setName(main.getGuiConfig().getString("upgrades.filler.name"))
                    .setLore(main.getGuiConfig().getStringList("upgrades.filler.lore"))
                    .toItemStack());
        }
    }
}
