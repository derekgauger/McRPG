package dirkyg.mcrpg.Utilities;

import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

public class Common {

    public static ItemStack createItem(final Material material, final String name, int amount, Enchantment[] enchants, int[] levels, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (!name.equalsIgnoreCase("")) {
            meta.setDisplayName(colorText(name));
        }
        for (int i = 0; i < lore.length; i++) {
            lore[i] = colorText(lore[i]);
        }
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        if (enchants != null) {
            for (int i = 0; i < enchants.length; i++) {
                if (material == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
                    esm.addStoredEnchant(enchants[i], levels[i],true);
                    item.setItemMeta(esm);
                } else {
                    ItemMeta eMeta = item.getItemMeta();
                    eMeta.addEnchant(enchants[i], levels[i], true);
                    item.setItemMeta(eMeta);
                }
            }
        }
        return item;
    }

    public static ItemStack createItem(final Material material, final String name, int amount, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (!name.equalsIgnoreCase("")) {
            meta.setDisplayName(colorText(name));
        }
        for (int i = 0; i < lore.length; i++) {
            lore[i] = colorText(lore[i]);
        }
        meta.setLore(Arrays.asList(lore));
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createGUIItem(final Material material, final String name, int amount, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return item;
        }
        if (!name.equalsIgnoreCase("")) {
            meta.setDisplayName(colorText(name));
        }
        for (int i = 0; i < lore.length; i++) {
            lore[i] = colorText(lore[i]);
        }
        meta.setLore(Arrays.asList(lore));
        meta.addEnchant(Enchantment.DURABILITY, 1, false);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        return item;
    }

    public static void addItemToInventory(ItemStack item, Player player) {
        HashMap<Integer, ItemStack> addedItem = player.getInventory().addItem(item);
        if (!addedItem.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(colorText("&dItem has been dropped at your feet"));
        }
    }

    public static int getRandomNumber(int lower, int upper) {
        Random rand = new Random();
        return rand.nextInt(lower, upper);
    }

    public static Entity getTargetEntity(Player player, double maxDistance) {
        Vector start = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection().normalize();
        RayTraceResult rayTrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), direction, maxDistance, 0.5, (entity) -> !entity.equals(player));
        return (rayTrace != null) ? rayTrace.getHitEntity() : null;
    }
}
