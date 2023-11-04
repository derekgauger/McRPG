package dirkyg.mcrpg.SpecialAbilities;

import static dirkyg.mcrpg.Utilities.Visuals.drawParticleCircle;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class FiringRing extends SpecialAbility implements Listener {

    HashMap<UUID, Long> entities = new HashMap<UUID, Long>();
    private final Set<UUID> processedEntities = new HashSet<>();
    boolean active = false;

    public FiringRing(UUID uuid, String classifer) {
        this.classifier = classifer;
        this.playerUUID = uuid;
        this.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        entities.clear();
        active = false;

    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        if (player == null) {
            return;
        }
        drawParticleCircle(player.getLocation(), 5, 50, new DustOptions(Color.fromRGB(250, 184, 2), .5f));
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            long currentTime = System.currentTimeMillis();
            UUID uuid = entity.getUniqueId();
            if (entities.containsKey(uuid) && currentTime < entities.get(uuid)) {
                continue;
            }
            // Calculate distance considering only the X and Z coordinates for a 2D circle
            if (entity instanceof Monster && entity.getLocation().distanceSquared(player.getLocation()) <= 5 * 5) {
                // Create the arrow entity at the shooter's location
                Arrow arrow = player.getWorld().spawn(player.getEyeLocation(), Arrow.class);
                arrow.setShooter(player);
                // Calculate the velocity vector from shooter to target
                Vector velocity = entity.getLocation().subtract(0, entity.getHeight() / 2.0, 0).toVector()
                        .subtract(player.getLocation().toVector()).normalize().multiply(2);

                arrow.setVelocity(velocity);
                arrow.setMetadata("firingRingArrow", new FixedMetadataValue(McRPG.plugin, true));
                entities.put(uuid, currentTime + 1500);
            }
        }
    }

    @EventHandler
    public void entityDamagedByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (!(damager instanceof Arrow) || !(damaged instanceof LivingEntity) || !isHappening()
                || processedEntities.contains(damaged.getUniqueId())) {
            Arrow arrow = (Arrow) damager;
            LivingEntity le = (LivingEntity) damaged;
            if (arrow.hasMetadata("firingRingArrow")) {
                Player player = Bukkit.getPlayer(playerUUID);
                event.setCancelled(true);
                processedEntities.add(le.getUniqueId());
                le.damage(10, player);
                processedEntities.remove(le.getUniqueId());
            }
        }
    }

    @Override
    public String toString() {
        return "Firing Ring";
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }

}
