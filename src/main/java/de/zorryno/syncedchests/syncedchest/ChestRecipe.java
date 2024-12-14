package de.zorryno.syncedchests.syncedchest;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ChestRecipe {

    private Plugin plugin;

    public ChestRecipe(Plugin plugin) {
        this.plugin = plugin;
    }

    public void registerRecipes() {
        List<Material> materials = Arrays.stream(Material.values()).toList();
        int recipeCount = 0;
        long time = System.currentTimeMillis();
        for(Material material : materials) {
            if(material.isLegacy() || !material.isItem() || material.isEmpty()) continue;
            Bukkit.addRecipe(createRecipe(material));
            recipeCount++;
        }
        plugin.getLogger().info("Added " + recipeCount + " synced Chest Recipes in " + (System.currentTimeMillis() - time) / 1000d + "s");
    }

    private Recipe createRecipe(Material additionalItem) {
        NamespacedKey key = new NamespacedKey(plugin, "syncedchest." + additionalItem.name());

        ShapedRecipe recipe = new ShapedRecipe(key, createChest(additionalItem));
        recipe.shape("aaa", "aca", "aaa");
        recipe.setIngredient('c', Material.CHEST);
        recipe.setIngredient('a', additionalItem);

        return recipe;
    }

    private ItemStack createChest(Material additionalItem) {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta itemMeta = item.getItemMeta();
        itemMeta.setDisplayName(ChatColor.DARK_GRAY + "SyncedChest: " + additionalItem.name());
        item.setItemMeta(itemMeta);
        return item;
    }
}
