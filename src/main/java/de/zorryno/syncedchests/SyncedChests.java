package de.zorryno.syncedchests;

import de.zorryno.syncedchests.syncedchest.ChestRecipe;
import de.zorryno.syncedchests.syncedchest.ChestSaver;
import de.zorryno.syncedchests.syncedchest.SyncedChest;
import de.zorryno.syncedchests.syncedchest.SyncedChestEvents;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class SyncedChests extends JavaPlugin {

    private static HashMap<Material, SyncedChest> syncedChestInventories = new HashMap<>();
    private ChestSaver chestSaver;

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(new SyncedChestEvents(this), this);
        new ChestRecipe(this).registerRecipes();
        chestSaver = new ChestSaver(this);
    }

    @Override
    public void onDisable() {
        chestSaver.save();
        unregisterEvents();
    }

    private void unregisterEvents() {
        HandlerList.unregisterAll(this);
    }

    public static List<SyncedChest> getAllSyncedChests() {
        List<SyncedChest> syncedChests = new ArrayList<>();
        syncedChestInventories.forEach((material, syncedChest) -> syncedChests.add(syncedChest));
        return syncedChests;
    }

    public static SyncedChest getSyncedChestInventory(Material material) {
        SyncedChest inventory = syncedChestInventories.get(material);
        if(inventory == null) {
            inventory = new SyncedChest(material);
            syncedChestInventories.put(material, inventory);
        }

        return inventory;
    }

    public static void addSyncedChestInventory(Material material, SyncedChest syncedChest) {
        syncedChestInventories.put(material, syncedChest);
    }
}
