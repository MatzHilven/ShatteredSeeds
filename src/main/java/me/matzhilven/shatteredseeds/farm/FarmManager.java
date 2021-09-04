package me.matzhilven.shatteredseeds.farm;

import me.matzhilven.shatteredseeds.ShatteredSeeds;
import me.matzhilven.shatteredseeds.farm.upgrades.Upgrades;
import me.matzhilven.shatteredseeds.schematic.Schematic;
import me.matzhilven.shatteredseeds.utils.Logger;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.util.BoundingBox;

import java.util.*;

public class FarmManager {

    private final ShatteredSeeds main;
    private final FileConfiguration config;
    private final FileConfiguration farmsConfig;

    private final Set<Farm> farms;
    private final List<Farm> cashedFarms;

    public FarmManager(ShatteredSeeds main) {
        this.main = main;
        this.config = main.getConfig();
        this.farmsConfig = main.getFarmsConfig();

        this.farms = new HashSet<>();
        this.cashedFarms = new ArrayList<>();

        loadFarms();
    }

    private void loadFarms() {
        Logger.log("loading config farms...");
        config.getConfigurationSection("farms").getKeys(false).forEach(cFarm -> {
            String s = "farms." + cFarm + ".";
            Farm farm = new Farm(
                    cFarm,
                    config.getString(s + "name"),
                    Material.getMaterial(config.getString(s + "material")),
                    config.getStringList(s + "lore"),
                    config.getString(s + "schematic"));

            if (farm.getMaterial() == Material.PLAYER_HEAD) {
                farm.setUrl(config.getString(s + "url"));
            }

            farm.setSchematic(new Schematic(farm.getNameUncolored(), main.getDataFolder().getAbsolutePath() + "\\schematics\\" + farm.getSchematicName()));

            farms.add(farm);
            Logger.log(" loaded farm `" + cFarm + "`");
        });

        if (farmsConfig.get("farms") == null) return;

        Logger.log("loading saved farms...");
        farmsConfig.getConfigurationSection("farms").getKeys(false).forEach(cFarm -> {
            String s = "farms." + cFarm + ".";
            String configID = farmsConfig.getString(s + "configID");

            World world = Bukkit.getWorld(farmsConfig.getString(s + ".world"));

            Farm farm = new Farm(
                    configID,
                    UUID.fromString(cFarm),
                    config.getString("farms." + configID + ".name"),
                    Material.getMaterial(config.getString("farms." + configID + ".material")),
                    config.getStringList("farms." + configID + ".lore"),
                    config.getString("farms." + configID + ".schematic"),
                    farmsConfig.getSerializable(s + "boundingBox", BoundingBox.class),
                    new Upgrades(
                            configID,
                            farmsConfig.getInt(s + "upgrades.speed"),
                            farmsConfig.getInt(s + "upgrades.itemsPerHarvest"),
                            farmsConfig.getInt(s + "upgrades.storageSpace")
                    ),
                    world
                    );


            BoundingBox boundingBox = farm.getBoundingBox();

            for (double i = boundingBox.getMinX(); i < boundingBox.getMaxX(); ++i) {
                for (double j = boundingBox.getMinY(); j < boundingBox.getMaxY(); ++j) {
                    for (double k = boundingBox.getMinZ(); k < boundingBox.getMaxZ(); ++k) {
                        Location loc = new Location(farm.getWorld(), i, j, k);
                        if (world.getBlockAt(loc).getType() == Material.AIR) continue;
                        if (!main.getBlockData().hasData(loc)) {
                            main.getBlockData().addData(loc, "uuid", cFarm);
                        }
                    }
                }
            }

            cashedFarms.add(farm);
            Logger.log(" loaded farm `" + cFarm + "`");
        });
    }

    public Set<Farm> getFarms() {
        return farms;
    }

    public List<Farm> getCashedFarms() {
        return cashedFarms;
    }

    public Optional<Farm> byName(String name) {
        return farms.stream().filter(farm -> farm.getNameUncolored().equalsIgnoreCase(name)).findFirst();
    }

    public Optional<Farm> byUUID(String uuid) {
        return cashedFarms.stream().filter(farm -> farm.getUUID().toString().equalsIgnoreCase(uuid)).findFirst();
    }

    public void addFarm(Farm farm) {
        cashedFarms.add(farm);
    }

    public void removeFarm(Farm farm) {
        cashedFarms.remove(farm);
    }
}
