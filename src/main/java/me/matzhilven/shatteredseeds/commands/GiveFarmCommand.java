package me.matzhilven.shatteredseeds.commands;

import me.matzhilven.shatteredseeds.ShatteredSeeds;
import me.matzhilven.shatteredseeds.farm.Farm;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GiveFarmCommand implements CommandExecutor, TabExecutor {

    private final ShatteredSeeds main;

    public GiveFarmCommand(ShatteredSeeds main) {
        this.main = main;
        main.getCommand("givefarm").setExecutor(this);
        main.getCommand("givefarm").setTabCompleter(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!sender.hasPermission("seeds.givefarm")) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-permissions"));
            return true;
        }

        if (args.length != 2) {
            StringUtils.sendMessage(sender, main.getConfig().getStringList("messages.usage"));
            return true;
        }

        Player target = Bukkit.getPlayer(args[0]);
        if (target == null) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-player"));
            return true;
        }

        Optional<Farm> farm = main.getFarmManager().byName(args[1]);
        if (!farm.isPresent()) {
            StringUtils.sendMessage(sender, main.getConfig().getString("messages.invalid-farm"));
            return true;
        }

        target.getInventory().addItem(farm.get().getItem());
        StringUtils.sendMessage(sender, main.getConfig().getString("messages.received-farm")
        .replace("%farm%", farm.get().getName()));


        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String s, String[] args) {

        if (args.length == 2) {
            return StringUtil.copyPartialMatches(args[1],
                    main.getFarmManager().getFarms().stream().map(Farm::getNameUncolored).collect(Collectors.toList()),
                    new ArrayList<>());
        }

        return null;
    }
}
