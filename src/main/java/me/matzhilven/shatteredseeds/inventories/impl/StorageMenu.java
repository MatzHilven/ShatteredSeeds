package me.matzhilven.shatteredseeds.inventories.impl;

import me.matzhilven.shatteredseeds.farm.Farm;
import me.matzhilven.shatteredseeds.inventories.PaginatedMenu;
import me.matzhilven.shatteredseeds.utils.ItemBuilder;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.List;

public class StorageMenu extends PaginatedMenu {

    private final HashMap<Integer, String> slots;
    private final Farm farm;

    public StorageMenu(Player p, Farm farm) {
        super(p);
        this.farm = farm;
        slots = new HashMap<>();
    }

    @Override
    public String getMenuName() {
        return main.getGuiConfig().getString("storage.title").replace("%farm%", farm.getName());
    }

    @Override
    public int getSlots() {
        return main.getGuiConfig().getInt("storage.size");
    }

    @Override
    public void handleClick(InventoryClickEvent e) {
        if (slots.containsKey(e.getSlot())) {
            switch (slots.get(e.getSlot())) {
                case "previous":
                    if (page == 0) {
                        StringUtils.sendMessage(p, "&cYou are already on the first page!");
                        return;
                    }
                    page--;
                    setMenuItems();
                    break;
                case "back":
                    new MainMenu(p, farm).open();
                    break;
                case "next":
                    if ((index + 1) >= (farm.getStorage().size() * 9)) {
                        StringUtils.sendMessage(p, "&cYou are already on the last page!");
                        return;
                    }
                    page++;
                    setMenuItems();

                    break;
            }
            return;
        }


    }

    @Override
    public void setMenuItems() {
        addMenuBorder(farm.getUpgrades().getStorageSpace());

        List<ItemStack> storage = farm.getStorage();

        if(storage != null && !storage.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= storage.size()) break;
                if (storage.get(index) != null){
                    inventory.addItem(storage.get(index));
                }
            }
        }


    }

    private boolean isMultiPaged() {
        return farm.getStorage().size() > farm.getUpgrades().getStorageSpace() * 9;
    }

    private void addMenuBorder(int rows) {
        ItemStack FILLER_GLASS = new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("storage.filler.material")))
                .setName(main.getGuiConfig().getString("storage.filler.name"))
                .setLore(main.getGuiConfig().getStringList("storage.filler.lore"))
                .toItemStack();

        inventory.setItem(main.getGuiConfig().getInt("storage.items.next.slot"),
                new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("storage.items.next.material")))
                        .setName(main.getGuiConfig().getString("storage.items.next.name"))
                        .setLore(main.getGuiConfig().getStringList("storage.items.next.lore"))
                        .toItemStack());

        inventory.setItem(main.getGuiConfig().getInt("storage.items.back.slot"),
                new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("storage.items.back.material")))
                        .setName(main.getGuiConfig().getString("storage.items.back.name"))
                        .setLore(main.getGuiConfig().getStringList("storage.items.back.lore"))
                        .toItemStack());

        inventory.setItem(main.getGuiConfig().getInt("storage.items.previous.slot"),
                new ItemBuilder(Material.matchMaterial(main.getGuiConfig().getString("storage.items.previous.material")))
                        .setName(main.getGuiConfig().getString("storage.items.previous.name"))
                        .setLore(main.getGuiConfig().getStringList("storage.items.previous.lore"))
                        .toItemStack());


        for (int i = (9 * rows); i < 45; i++) {
            inventory.setItem(i, FILLER_GLASS);
        }

        slots.put(main.getGuiConfig().getInt("storage.items.next.slot"), "next");
        slots.put(main.getGuiConfig().getInt("storage.items.back.slot"), "back");
        slots.put(main.getGuiConfig().getInt("storage.items.previous.slot"), "previous");

    }
}
