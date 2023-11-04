package dirkyg.mcrpg.SpecialAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Damageable;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;


public class HitScan extends SpecialAbility implements Listener {

    public float hitScanDamage = 10f;

    public HitScan(UUID uuid, String classifier) {
        this.classifier = classifier;
        this.abilityName = this.toString();
        this.playerUUID = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Hit Scan";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
    }

    @Override
    void initalizeAbility(Player player) {
    }

    @EventHandler
    public void onBowShoot(EntityShootBowEvent event) {
        if (!(event.getEntity() instanceof Player player) || player.getUniqueId() != playerUUID || !isHappening()) {
            return;
        }
        event.setCancelled(true);
        performHitScan(player, event.getForce());
    }

    private void performHitScan(Player player, float force) {
        Location start = player.getEyeLocation();
        Vector direction = start.getDirection();
        start.add(direction.multiply(1.5));
        RayTraceResult rayTraceResult = player.getWorld().rayTraceEntities(
                start,
                direction,
                120, // Max distance
                0.5 // Ray size
        );
        if (rayTraceResult != null) {
            Entity hitEntity = rayTraceResult.getHitEntity();
            // Check if the entity is not the player
            if (hitEntity != null && !hitEntity.equals(player)) {
                // Apply damage to the hit entity
                ((Damageable) hitEntity).damage(hitScanDamage, player); // Damage could be scaled with force
                // Play sound and particle effects if needed
                player.getWorld().playSound(player.getLocation(), Sound.ENTITY_ARROW_SHOOT, 1.0F, 1.0F);
            }
        }
    }
}
