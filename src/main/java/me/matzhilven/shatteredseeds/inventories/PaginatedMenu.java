package me.matzhilven.shatteredseeds.inventories;

import me.matzhilven.shatteredseeds.utils.ItemBuilder;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class PaginatedMenu extends Menu {

    protected int page = 0;

    protected int maxItemsPerPage = 45;

    protected int index = 0;

    public PaginatedMenu(Player p) {
        super(p);
    }

    public int getMaxItemsPerPage() {
        return maxItemsPerPage;
    }
}

