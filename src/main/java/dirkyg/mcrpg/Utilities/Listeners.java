package dirkyg.mcrpg.Utilities;

import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import dirkyg.mcrpg.McRPG;

public class Listeners implements Listener {

    public Listeners() {
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onEntityHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getEntity().getCustomName() !=  null && !event.getEntity().getCustomName().equalsIgnoreCase("")) {
            if (!event.getEntity().getCustomName().contains("❤")) {
                return;
            }
        }
        Entity entity = event.getEntity();
        double health = ((LivingEntity) entity).getHealth() - event.getFinalDamage();
        health = Math.round(health * 10.0) / 10.0;
        if (health < 0.0) {
            health = 0.0;
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(colorText((String.format("%.1f", health) + " &c❤ &f" + entity.getType())));
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        if (item.getType() == Material.DIRT || item.getType() == Material.STONE || item.getType() == Material.NETHERRACK) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getEntity().getNearbyEntities(2, 2, 2).stream().anyMatch(e -> e.getType().toString().equals("FIREWORK"))) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        block.setMetadata("playerPlaced", new FixedMetadataValue(McRPG.plugin, true));
    }

}
