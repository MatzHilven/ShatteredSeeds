package me.matzhilven.shatteredseeds.farm.upgrades;

import me.matzhilven.shatteredseeds.ShatteredSeeds;

import java.util.HashMap;

public class Upgrades {

    private final ShatteredSeeds main = ShatteredSeeds.getPlugin(ShatteredSeeds.class);

    private final String configFarm;

    private int speed;
    private int itemsPerHarvest;
    private int storageSpace;

    public Upgrades(String configFarm) {
        this(configFarm, 1, 1, 1);
    }

    public Upgrades(String configFarm, int speed, int itemsPerHarvest, int storageSpace) {
        this.configFarm = configFarm;
        this.speed = speed;
        this.itemsPerHarvest = itemsPerHarvest;
        this.storageSpace = storageSpace;
    }

    public int getSpeed() {
        return speed;
    }

    public int getItemsPerHarvest() {
        return itemsPerHarvest;
    }

    public int getStorageSpace() {
        return storageSpace;
    }

    public HashMap<String, Integer> toConfigFormat() {
        return new HashMap<String, Integer>() {{
            put("speed", speed);
            put("itemsPerHarvest", itemsPerHarvest);
            put("storageSpace", storageSpace);

        }};
    }

    public double getPrice(String item) {
        switch (item) {
            case "speed":
                return main.getConfig().getDouble("farms." + configFarm + ".upgrades." + item + "." + (speed + 1));
            case "itemsPerHarvest":
                return main.getConfig().getDouble("farms." + configFarm + ".upgrades." + item + "." + (itemsPerHarvest + 1));
            case "storageSpace":
                return main.getConfig().getDouble("farms." + configFarm + ".upgrades." + item + "." + (storageSpace + 1));
            default:
                return 0.0;
        }
    }

    public String getLevel(String item) {
        switch (item) {
            case "speed":
                return String.valueOf(speed);
            case "itemsPerHarvest":
                return String.valueOf(itemsPerHarvest);
            case "storageSpace":
                return String.valueOf(storageSpace);
            default:
                return "0";
        }
    }

    public String getNextLevel(String item) {
        switch (item) {
            case "speed":
                return String.valueOf(speed + 1);
            case "itemsPerHarvest":
                return String.valueOf(itemsPerHarvest + 1);
            case "storageSpace":
                return String.valueOf(storageSpace + 1);
            default:
                return "0";
        }
    }

    public String getMaxLevel(String item) {
        if (item.equals("back")) return "";
        String last = "";
       for (String level : main.getConfig().getConfigurationSection("farms." + configFarm + ".upgrades." + item).getKeys(false)) {
           last = level;
       }
        return last;
    }

}
