package me.matzhilven.shatteredseeds.data;

import org.bukkit.Location;

import java.util.HashMap;

public class BlockData {

    private final HashMap<Location, HashMap<String, String>> blockData;

    public BlockData() {
        blockData = new HashMap<>();
    }

    public void addData(Location location, String key, String value) {
        HashMap<String, String> data;
        if (!blockData.containsKey(location)) {
            data = new HashMap<>();
        } else {
            data = blockData.get(location);
        }
        data.put(key, value);
        blockData.put(location, data);
    }

    public boolean hasData(Location location) {
        return blockData.containsKey(location);
    }

    public HashMap<String, String> getData(Location location) {
        return blockData.get(location);
    }

    public void removeData(Location loc, String key) {
        blockData.get(loc).remove(key);
    }

    public void clearLocation(Location location) {
        blockData.remove(location);
    }
}
