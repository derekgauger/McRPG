package dirkyg.mcrpg.PassiveAbilities;

import static dirkyg.mcrpg.Utilities.Visuals.sendActionBar;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;

public class BowCharge extends PassiveAbility implements Listener {

    private ChargeBarTask chargeTask = null;
    private Player chargingPlayer = null;
    private static final String ARROW_CHARGE_METAKEY = "arrowChargePercentage";
    private final Set<UUID> processedEntities = new HashSet<>();

    public BowCharge(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onEntityShootBow(EntityShootBowEvent event) {
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
            if (player.equals(chargingPlayer) && chargeTask != null && isCurrentlyActive() && player.getUniqueId() == uuid) {
                // Retrieve the ticks held from the ChargeBarTask
                long ticksHeld = ((ChargeBarTask) chargeTask).getTicksHeld();
                // Calculate the charge percentage
                double chargePercentage = Math.min(100.0, (double) ticksHeld / (0 + 44) * 100); // Assuming 20 ticks per
                // Store the charge percentage in the arrow metadata
                event.getProjectile().setMetadata(ARROW_CHARGE_METAKEY,
                        new FixedMetadataValue(McRPG.plugin, chargePercentage / 100));
                cancelChargeTask();
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity le) || !isCurrentlyActive() || processedEntities.contains(le.getUniqueId())) {
            return;
        }
        if (event.getDamager() instanceof Arrow) {
            Arrow arrow = (Arrow) event.getDamager();
            if (arrow.getShooter() instanceof Player player && player.getUniqueId() == uuid && arrow.hasMetadata(ARROW_CHARGE_METAKEY)) {
                double chargePercentage = arrow.getMetadata(ARROW_CHARGE_METAKEY).get(0).asDouble();
                // Apply the damage multiplier based on charge
                double damage = event.getFinalDamage() * (1.5 + chargePercentage);
                processedEntities.add(le.getUniqueId());
                le.damage(damage, player);
                processedEntities.remove(le.getUniqueId());
                event.setDamage(0);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        if ((event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK) &&
                event.getItem() != null && event.getItem().getType() == Material.BOW && chargingPlayer == null) {
            chargingPlayer = player;
            chargeTask = new ChargeBarTask(player);
            chargeTask.runTaskTimer(McRPG.plugin, 0L, 1L);
        }
    }

    @EventHandler
    public void onPlayerReleaseItem(PlayerDropItemEvent event) {
        if (event.getItemDrop().getItemStack().getType() == Material.BOW && event.getPlayer().equals(chargingPlayer)) {
            cancelChargeTask();
        }
    }

    @EventHandler
    public void onPlayerItemHeldChange(PlayerItemHeldEvent event) {
        if (event.getPlayer().equals(chargingPlayer)) {
            cancelChargeTask();
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player && event.getWhoClicked().equals(chargingPlayer)) {
            cancelChargeTask();
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (event.getPlayer().equals(chargingPlayer)) {
            cancelChargeTask();
        }
    }

    // Helper method to cancel the charge task
    private void cancelChargeTask() {
        Bukkit.broadcastMessage("Cancelling");
        if (chargeTask != null) {
            chargeTask.cancel();
            if (chargingPlayer != null) {
                sendActionBar(chargingPlayer, ""); // Clear the action bar
            }
            chargeTask = null;
            chargingPlayer = null;
        }
    }

    class ChargeBarTask extends BukkitRunnable {
        private final Player player;
        private long ticks = 0;

        public ChargeBarTask(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            // Check if player is still holding the bow
            ItemStack mainHand = player.getInventory().getItemInMainHand();

            if (mainHand != null && mainHand.getType() == Material.BOW) {
                // Increment the charge and display the action bar
                ticks++;
                sendActionBar(player, buildChargeBar(ticks, 0, 44)); // Adjusted the ticks to match 1.1 seconds full
                                                                     // draw
            } else {
                // Stop the task if the player is no longer holding the bow
                cancelChargeTask();
            }
        }

        public long getTicksHeld() {
            return ticks;
        }
    }

    private String buildChargeBar(long ticks, long fullDrawTicks, long overchargeTicks) {
        // Calculate the current charge percentage
        double percentage = Math.min(100.0, (double) ticks / (fullDrawTicks + overchargeTicks) * 100);

        StringBuilder bar = new StringBuilder();

        // Build the charge bar with color changes according to the percentage
        for (int i = 0; i < 100; i += 2) {
            if (i < percentage) {
                if (percentage < 33) {
                    bar.append(ChatColor.GREEN + "|");
                } else if (percentage < 67) {
                    bar.append(ChatColor.YELLOW + "|");
                } else {
                    bar.append(ChatColor.RED + "|");
                }
            } else {
                bar.append(ChatColor.GRAY + "|");
            }
        }

        bar.append(ChatColor.WHITE.toString()).append(" ").append(Math.round(percentage)).append("%");
        return bar.toString();
    }

}
