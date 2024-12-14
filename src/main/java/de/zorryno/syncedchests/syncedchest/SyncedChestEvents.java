package de.zorryno.syncedchests.syncedchest;

import com.destroystokyo.paper.event.block.BlockDestroyEvent;
import de.zorryno.syncedchests.SyncedChests;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.HumanEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.inventory.HopperInventorySearchEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import java.util.*;

public class SyncedChestEvents implements Listener {
    private Plugin plugin;

    private final List<Material> syncedChestItemMaterials = new ArrayList<>();

    public SyncedChestEvents(Plugin plugin) {
        this.plugin = plugin;
        fillMaterialList();
    }

    private void fillMaterialList() {
        List<Material> materials = Arrays.stream(Material.values()).toList();
        for(Material material : materials) {
            if (material.isLegacy() || !material.isItem() || material.isEmpty()) continue;
            syncedChestItemMaterials.add(material);
        }
    }

    @EventHandler
    public void onHopperInteraction(HopperInventorySearchEvent event) {
        if(!(event.getSearchBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        event.setInventory(SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName)).getInventory());
    }

    @EventHandler
    public void onCrafterInteraction(InventoryMoveItemEvent event) {
        Location destinationLocation = event.getDestination().getLocation();
        if(destinationLocation == null) return;
        if(!(destinationLocation.getBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        SyncedChest syncedChest = SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName));

        HashMap<Integer, ItemStack> overflownItems = syncedChest.getInventory().addItem(event.getItem());
        if(!overflownItems.isEmpty()) {
            overflownItems.forEach((integer, itemStack) -> destinationLocation.getWorld().dropItemNaturally(destinationLocation, itemStack));
        }
        Bukkit.getScheduler().runTaskLater(plugin, ()-> event.getDestination().remove(event.getItem()), 1);
    }

    @EventHandler
    public void onSyncedChestPlace(BlockPlaceEvent event) {
        if(!(event.getBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName)).getChests().add(event.getBlockPlaced().getLocation());
    }

    @EventHandler
    public void onSyncedChestBreak(BlockDestroyEvent event) {
        if(!(event.getBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        SyncedChest syncedChest = SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName));

        destroySyncedChest(syncedChest, chest.getLocation());
    }

    @EventHandler
    public void onSyncedChestBreak(BlockBreakEvent event) {
        if(!(event.getBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        SyncedChest syncedChest = SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName));

        destroySyncedChest(syncedChest, chest.getLocation());
    }

    @EventHandler
    public void onSyncedChestBreak(EntityExplodeEvent event) {
        if(event.getExplosionResult().equals(ExplosionResult.KEEP) || event.getExplosionResult().equals(ExplosionResult.TRIGGER_BLOCK)) return;
        event.blockList().forEach(block -> {
            if(!(block.getState() instanceof Chest chest)) return;
            if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;

            String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
            SyncedChest syncedChest = SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName));

            destroySyncedChest(syncedChest, chest.getLocation());
        });
    }

    private void destroySyncedChest(SyncedChest syncedChest, Location location) {
        syncedChest.getChests().remove(location);
        if(syncedChest.getChests().isEmpty()) {
            for(ItemStack item : syncedChest.getInventory().getContents()) {
                if(item == null) continue;
                location.getWorld().dropItemNaturally(location, item);
            }
            syncedChest.getInventory().clear();
            new ArrayList<HumanEntity>(syncedChest.getInventory().getViewers()).forEach(HumanEntity::closeInventory);
        }
    }
    @EventHandler
    public void onSyncedChestOpen(PlayerInteractEvent event) {
        if(!event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) return;
        if(!(event.getClickedBlock().getState() instanceof Chest chest)) return;
        if(!(chest.getCustomName() != null && chest.getCustomName().startsWith(ChatColor.DARK_GRAY + "SyncedChest: "))) return;
        if(event.getPlayer().isSneaking()) return;

        String materialName = ChatColor.stripColor(chest.getCustomName()).substring(13);
        event.setCancelled(true);

        SyncedChest syncedChestInventory = SyncedChests.getSyncedChestInventory(Material.getMaterial(materialName));
        syncedChestInventory.getChests().forEach(location -> location.getWorld().playSound(location, Sound.BLOCK_CHEST_OPEN, 1, 1));
        event.getPlayer().openInventory(syncedChestInventory.getInventory());
    }

    @EventHandler
    public void onSyncedChestClose(InventoryCloseEvent event) {
        if(!(event.getInventory().getHolder() instanceof SyncedChest syncedChest)) return;

        syncedChest.getChests().forEach(location -> location.getWorld().playSound(location, Sound.BLOCK_CHEST_CLOSE, 1, 1));
    }
}
