package me.matzhilven.shatteredseeds.farm;

import me.matzhilven.shatteredseeds.ShatteredSeeds;
import me.matzhilven.shatteredseeds.farm.upgrades.Upgrades;
import me.matzhilven.shatteredseeds.schematic.Schematic;
import me.matzhilven.shatteredseeds.utils.ItemBuilder;
import me.matzhilven.shatteredseeds.utils.StringUtils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BoundingBox;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Farm {

    private final ShatteredSeeds main = ShatteredSeeds.getPlugin(ShatteredSeeds.class);

    private final String configID;
    private final UUID uuid;
    private final String name;
    private final Material material;
    private final List<String> lore;
    private final String schematicName;
    private String url;

    private World world;
    private Schematic schematic;
    private BoundingBox boundingBox;
    private Upgrades upgrades;
    private List<ItemStack> storage;

    public Farm(String configID, String name, Material material, List<String> lore, String schematicName) {
        this(configID, UUID.randomUUID(), name, material, lore, schematicName, null, new Upgrades(configID), null);
    }

    public Farm(String configID, UUID uuid, String name, Material material, List<String> lore,
                String schematicName, BoundingBox boundingBox, Upgrades upgrades, World world) {
        this.configID = configID;
        this.uuid = uuid;
        this.name = name;
        this.material = material;
        this.lore = lore;
        this.schematicName = schematicName;
        this.boundingBox = boundingBox;
        this.upgrades = upgrades;
        this.storage = new ArrayList<>();
        this.world = world;
    }

    public UUID getUUID() {
        return uuid;
    }

    public String getName() {
        return name;
    }

    public String getNameUncolored() {
        return StringUtils.removeColor(name.toLowerCase().replace(" ", "_"));
    }

    public Material getMaterial() {
        return material;
    }

    public String getSchematicName() {
        return schematicName;
    }

    public Upgrades getUpgrades() {
        return upgrades;
    }

    public List<ItemStack> getStorage() {
        return storage;
    }

    public World getWorld() {
        return world;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setSchematic(Schematic schematic) {
        try {
            schematic.parse();
        } catch (IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        this.schematic = schematic;
    }

    public ItemStack getItem() {
        ItemBuilder ib;
        if (url != null) {
            ib = ItemBuilder.fromSkull(url);
        } else {
            ib = new ItemBuilder(material);
        }

        ib.setName(name);
        ib.setLore(lore);
        ib.addNBT("farm", getNameUncolored());

        return ib.toItemStack();
    }

    public boolean paste(Location location, boolean ignoreAirBlocks) {
        boundingBox = getBoundingBox(location);

        if (!canPlace()) return false;

        schematic.paste(location, ignoreAirBlocks);

        world = location.getWorld();

        for (double i = boundingBox.getMinX(); i < boundingBox.getMaxX(); ++i) {
            for (double j = boundingBox.getMinY(); j < boundingBox.getMaxY(); ++j) {
                for (double k = boundingBox.getMinZ(); k < boundingBox.getMaxZ(); ++k) {
                    Location loc = new Location(world, i, j, k);
                    if (world.getBlockAt(loc).getType() == Material.AIR) continue;
                    if (!main.getBlockData().hasData(loc)) {
                        main.getBlockData().addData(loc, "uuid", uuid.toString());
                    }
                }
            }
        }

        main.getFarmManager().addFarm(this);
        save();
        return true;
    }

    public void remove(Location location) {
        for (double i = boundingBox.getMinX(); i < boundingBox.getMaxX(); ++i) {
            for (double j = boundingBox.getMinY(); j < boundingBox.getMaxY(); ++j) {
                for (double k = boundingBox.getMinZ(); k < boundingBox.getMaxZ(); ++k) {
                    Location loc = new Location(location.getWorld(), i, j, k);
                    if (world.getBlockAt(loc).getType() == Material.AIR) continue;
                    main.getBlockData().clearLocation(loc);
                    loc.getBlock().setType(Material.AIR);
                }
            }
        }
        main.getBlockData().clearLocation(location);
        main.getFarmManager().removeFarm(this);
        delete();
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    public BoundingBox getBoundingBox(Location target) {
        return BoundingBox.of(target.getBlock(), target.clone().add(
                schematic.getWidth() - 1,
                schematic.getHeight() - 1,
                schematic.getLength() - 1)
                .getBlock());
    }

    private boolean canPlace() {
        for (Farm farm : main.getFarmManager().getCashedFarms()) {
            if (farm.getBoundingBox() == null) continue;
            if (boundingBox.overlaps(farm.getBoundingBox())) return false;
        }
        return true;
    }

    private void save() {
        main.getFarmsConfig().set("farms." + getUUID() + ".configID", configID);
        main.getFarmsConfig().set("farms." + getUUID() + ".boundingBox", boundingBox);
        main.getFarmsConfig().set("farms." + getUUID() + ".world", world.getName());

        getUpgrades().toConfigFormat()
                .forEach((key, value) -> main.getFarmsConfig().set("farms." + getUUID() + ".upgrades." + key, value));
        main.saveFarms();
    }

    private void delete() {
        main.getFarmsConfig().set("farms." + getUUID(), null);
        main.saveFarms();
    }
}