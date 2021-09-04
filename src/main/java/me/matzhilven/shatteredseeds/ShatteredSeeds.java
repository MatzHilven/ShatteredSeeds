package me.matzhilven.shatteredseeds;

import me.matzhilven.shatteredseeds.commands.GiveFarmCommand;
import me.matzhilven.shatteredseeds.data.BlockData;
import me.matzhilven.shatteredseeds.farm.FarmManager;
import me.matzhilven.shatteredseeds.listeners.InventoryListeners;
import me.matzhilven.shatteredseeds.listeners.PlayerListeners;
import me.matzhilven.shatteredseeds.utils.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class ShatteredSeeds extends JavaPlugin {

    private Economy econ = null;

    private FileConfiguration guiConfig;
    private FileConfiguration farmsConfig;

    private FarmManager farmManager;

    private BlockData blockData;

    @Override
    public void onEnable() {

        createFiles();

        blockData = new BlockData();

        farmManager = new FarmManager(this);

        if (!setupEconomy() ) {
            Logger.severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        new InventoryListeners(this);
        new PlayerListeners(this);

        new GiveFarmCommand(this);
    }

    @Override
    public void onDisable() {

    }

    private void createFiles() {

        saveDefaultConfig();
        createSchematicDirectory();

        File guiF = new File(getDataFolder(), "gui.yml");
        File farmsF = new File(getDataFolder(), "farms.yml");

        if (!guiF.exists()) {
            guiF.getParentFile().mkdir();
            saveResource("gui.yml", false);
        }

        if (!farmsF.exists()) {
            farmsF.getParentFile().mkdir();
            saveResource("farms.yml", false);
        }

        guiConfig = new YamlConfiguration();
        farmsConfig = new YamlConfiguration();

        try {
            guiConfig.load(guiF);
            farmsConfig.load(farmsF);
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }

    }

    private void createSchematicDirectory() {
        File dir = new File(getDataFolder(), "/schematics");

        if (!dir.exists()) {
            dir.getParentFile().mkdir();
        }
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return true;
    }

    public void saveFarms() {
        File farmsF = new File(getDataFolder(), "farms.yml");
        try {
            farmsConfig.save(farmsF);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public FileConfiguration getGuiConfig() {
        return guiConfig;
    }

    public FileConfiguration getFarmsConfig() {
        return farmsConfig;
    }

    public FarmManager getFarmManager() {
        return farmManager;
    }

    public BlockData getBlockData() {
        return blockData;
    }

    public Economy getEcon() {
        return econ;
    }
}
