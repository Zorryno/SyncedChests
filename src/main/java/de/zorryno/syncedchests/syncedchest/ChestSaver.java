package de.zorryno.syncedchests.syncedchest;

import de.zorryno.syncedchests.SyncedChests;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.Nullable;

public class ChestSaver {
    private Plugin plugin;
    private FileConfiguration config;
    public ChestSaver (Plugin plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        this.config = plugin.getConfig();
        load();
    }

    public void save() {
        SyncedChests.getAllSyncedChests().forEach(syncedChest -> {
            ItemStack[] contents = syncedChest.getInventory().getContents();
            for (int i = 0; i < contents.length; i++) {
                config.set(syncedChest.getMaterial().name() + ".contents." + i, contents[i]);
            }
            for (int i = 0; i < syncedChest.getChests().size(); i++) {
                config.set(syncedChest.getMaterial().name() + ".locations." + i, syncedChest.getChests().get(i));
            };
        });
        plugin.saveConfig();
    }

    public void load() {
        config.getKeys(false).forEach(materialName -> {
            Material material = Material.getMaterial(materialName);
            SyncedChest syncedChest = new SyncedChest(material);

            ItemStack[] contents = new ItemStack[27];
            for (int i = 0; i < contents.length; i++) {
                contents[i] = config.getItemStack(materialName + ".contents." + i);
            }
            syncedChest.getInventory().setContents(contents);

            ConfigurationSection configurationSection = config.getConfigurationSection(materialName + ".locations");
            for(String key : configurationSection.getKeys(false)) {
                syncedChest.getChests().add(configurationSection.getLocation(key));
            }

            SyncedChests.addSyncedChestInventory(material,syncedChest);
        });
    }
}
