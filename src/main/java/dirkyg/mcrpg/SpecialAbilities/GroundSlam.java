package dirkyg.mcrpg.SpecialAbilities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityChangeBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class GroundSlam extends SpecialAbility implements Listener {

    public GroundSlam(UUID uuid, String classifer) {
        this.classifier = classifer;
        this.playerUUID = uuid;
        this.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Ground Slam";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID || !isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR && player.isOnGround()) {
            player.setVelocity(new Vector(0.0, 1, 0.0));
            new BukkitRunnable() {
                @Override
                public void run() {
                    if (player.isOnGround()) {
                        slam(player);
                        cancel();
                    }
                }
            }.runTaskTimer(McRPG.plugin, 10L, 1L);
        }
    }

    private void slam(Player player) {
        Random random = new Random();
        int intRadius = (int) Math.ceil(5);
        World world = player.getWorld();
        Location location = player.getLocation();
        for (int x = -intRadius; x <= intRadius; x++) {
            for (int z = -intRadius; z <= intRadius; z++) {
                if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(5, 2)) {
                    Location blockLocation = location.clone().add(x, -1, z);
                    if (blockLocation.getBlock().getType() != Material.AIR) {
                        Vector direction = new Vector(
                                random.nextDouble() - 0.5,
                                random.nextDouble(),
                                random.nextDouble() - 0.5);
                        FallingBlock fallingBlock = blockLocation.getWorld().spawnFallingBlock(blockLocation,
                                blockLocation.getBlock().getBlockData());
                        fallingBlock.setVelocity(direction);
                        fallingBlock.setDropItem(false);
                        fallingBlock.setMetadata("groundSlamBlock", new FixedMetadataValue(McRPG.plugin, true));
                    }
                }
            }
        }
        for (Entity entity : world.getNearbyEntities(location, 5, 5, 5)) {
            if (entity instanceof LivingEntity && !entity.equals(player)
                    && entity.getLocation().distanceSquared(player.getLocation()) <= 5 * 5) {
                ((LivingEntity) entity).damage(5.0, player);
            }
        }
    }

    @EventHandler
    public void onEntityChangeBlock(EntityChangeBlockEvent event) {
        if (event.getEntity() instanceof FallingBlock) {
            FallingBlock fallingBlock = (FallingBlock) event.getEntity();
            if (fallingBlock.hasMetadata("groundSlamBlock")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player) || !isHappening() || player.getUniqueId() != playerUUID) {
            return;
        }
        if (event.getCause().equals(DamageCause.FALL)) {
            event.setCancelled(true);
        }
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }
}
