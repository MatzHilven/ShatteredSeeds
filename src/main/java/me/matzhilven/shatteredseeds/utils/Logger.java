package me.matzhilven.shatteredseeds.utils;

import me.matzhilven.shatteredseeds.ShatteredSeeds;
import org.bukkit.Bukkit;

import java.util.logging.Level;

public class Logger {

    public static void log(String s) {
        Bukkit.getLogger().log(Level.INFO, String.format("[%s] " + s, ShatteredSeeds.getPlugin(ShatteredSeeds.class).getDescription().getName()));
    }

    public static void severe(String s) {
        Bukkit.getLogger().log(Level.SEVERE, String.format("[%s] " + s, ShatteredSeeds.getPlugin(ShatteredSeeds.class).getDescription().getName()));
    }
}
