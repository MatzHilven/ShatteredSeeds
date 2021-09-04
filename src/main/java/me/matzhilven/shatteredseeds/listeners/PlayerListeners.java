package me.matzhilven.shatteredseeds.listeners;

import de.tr7zw.nbtapi.NBTItem;
import me.matzhilven.shatteredseeds.ShatteredSeeds;
import me.matzhilven.shatteredseeds.farm.Farm;
import me.matzhilven.shatteredseeds.inventories.impl.MainMenu;
import me.matzhilven.shatteredseeds.utils.Logger;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class PlayerListeners implements Listener {

    private final ShatteredSeeds main;

    public PlayerListeners(ShatteredSeeds main) {
        this.main = main;
        main.getServer().getPluginManager().registerEvents(this, main);
    }

    @EventHandler
    private void onFarmPlace(BlockPlaceEvent e) {
        Player player = e.getPlayer();
        ItemStack hand = e.getItemInHand();

        if (hand.getType() == Material.AIR) return;

        NBTItem nbtItem = new NBTItem(hand);

        if (!nbtItem.hasKey("farm")) return;
        e.setCancelled(true);
        Farm farm = main.getFarmManager().byName(nbtItem.getString("farm")).get();
        Bukkit.getScheduler().runTaskLater(main, () -> {
            if (!farm.paste(e.getBlock().getLocation(), true)) {
                StringUtils.sendMessage(player, this.main.getConfig().getString("messages.invalid-place"));
                return;
            }
            hand.setAmount(hand.getAmount() - 1);
            player.getInventory().setItemInMainHand(hand);
        }, 1L);

    }

    @EventHandler
    private void onFarmClick(PlayerInteractEvent e) {
        if (!e.hasBlock()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getClickedBlock() == null) return;
        Player p = e.getPlayer();

        if (main.getBlockData().hasData(e.getClickedBlock().getLocation())) {
            Optional<Farm> farm = main.getFarmManager()
                    .byUUID(main.getBlockData().getData(e.getClickedBlock().getLocation()).get("uuid"));

            if (!farm.isPresent()) return;
            e.setCancelled(true);
            new MainMenu(p, farm.get()).open();
        }
    }

    @EventHandler
    private void onFarmBreak(BlockBreakEvent e) {
        if (main.getBlockData().hasData(e.getBlock().getLocation())) {
            Optional<Farm> farm = main.getFarmManager()
                    .byUUID(main.getBlockData().getData(e.getBlock().getLocation()).get("uuid"));

            if (!farm.isPresent()) return;
            farm.get().remove(e.getBlock().getLocation());
            e.getPlayer().getInventory().addItem(farm.get().getItem());
        }
    }
}
