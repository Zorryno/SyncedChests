package de.zorryno.syncedchests.syncedchest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class SyncedChest implements InventoryHolder {
    private Inventory inventory;
    private Material material;
    private List<Location> chests = new ArrayList<>();

    public SyncedChest(Material material) {
        this.material = material;
        inventory = Bukkit.createInventory(this, InventoryType.CHEST, ChatColor.DARK_GRAY + "SyncedChest: " + material.name());
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public List<Location> getChests() {
        return chests;
    }

    public Material getMaterial() {
        return material;
    }
}
