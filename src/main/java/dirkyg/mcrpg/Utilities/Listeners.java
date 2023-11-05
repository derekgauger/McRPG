package dirkyg.mcrpg.Utilities;

import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.Arrays;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;

public class Listeners implements Listener {

    public Listeners() {
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        processFireworkImmunity(event);
        processHealthBar(event);
    }

    public void processFireworkImmunity(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (event.getEntity().getNearbyEntities(2, 2, 2).stream().anyMatch(e -> e.getType().toString().equals("FIREWORK"))) {
                event.setCancelled(true);
            }
        }
    }

    public void processHealthBar(EntityDamageEvent event) {
        if (event.getEntity() instanceof Monster) {
            Monster monster = (Monster) event.getEntity();
            // Ensure the mob has been damaged
            if (event.getFinalDamage() > 0) {
                String healthBar = "";
                double healthPercentage = (monster.getHealth() - event.getFinalDamage()) / monster.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue();
                int health = (int) Math.ceil(healthPercentage * 10);  // 10 is an arbitrary scale for the health bar
                healthBar = generateHealthBar(health, 10);
                monster.setCustomName(healthBar);
                monster.setCustomNameVisible(true);
                // Hide health bar after 5 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        monster.setCustomNameVisible(false);
                    }
                }.runTaskLater(McRPG.plugin, 5 * 20);  // 5 seconds * 20 ticks/second
            }
        }
    }

    private String generateHealthBar(int current, int max) {
        StringBuilder healthBar = new StringBuilder();
        current = Math.max(current, 0);
        double healthPercentage = (double) current / max;
        String color;
        if (healthPercentage > 0.66) {
            color = "&a";
        } else if (healthPercentage > 0.33) {
            color = "&e";
        } else {
            color = "&c";
        }
        for (int i = 0; i < current; i++) {
            healthBar.append(color).append("█");
        }
        int remaining = max - current;
        for (int i = 0; i < remaining; i++) {
            healthBar.append("&7▒");
        }
        return colorText(healthBar.toString());
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
    public void onBlockPlace(BlockPlaceEvent event) {
        Block block = event.getBlockPlaced();
        block.setMetadata("playerPlaced", new FixedMetadataValue(McRPG.plugin, true));
    }
}
