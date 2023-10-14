package dirkyg.mcrpg.SpecialAbilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.UUID;

public class HomingArrows extends SpecialAbility implements Listener {

    public float arrowSpeed = 3.5f;

    public HomingArrows(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Homing Arrows";
    }

    @EventHandler
    public void onArrowShoot(ProjectileLaunchEvent event) {
        ProjectileSource shooter = event.getEntity().getShooter();
        if (!(shooter instanceof Entity entityShooter) || entityShooter.getUniqueId() != playerUUID || !isHappening) {
            return;
        }
        if (event.getEntity() instanceof Arrow arrow && entityShooter instanceof Player player) {
            LivingEntity target = getClosestEntityInSight(player, 100);
            if (player.isSneaking()) {
                return;
            }
            if (target != null && !(target instanceof Enderman)) {
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (arrow.isOnGround() || arrow.isDead() || target.isDead()) {
                            this.cancel();
                            return;
                        }
                        Vector toTarget = target.getLocation().add(0, 0.5, 0).subtract(arrow.getLocation()).toVector();
                        arrow.setVelocity(toTarget.normalize().multiply(arrowSpeed));
                    }
                }.runTaskTimer(McRPG.plugin, 1L, 1L);
            }
        }
    }

    public LivingEntity getClosestEntityInSight(Player player, double range) {
        Vector dir = player.getLocation().getDirection();
        RayTraceResult rayTrace = player.getWorld().rayTraceEntities(
                player.getEyeLocation(),
                dir,
                range,
                e -> (e instanceof LivingEntity) && e != player
        );
        if (rayTrace != null) {
            Entity hitEntity = rayTrace.getHitEntity();
            if (hitEntity instanceof LivingEntity) {
                return (LivingEntity) hitEntity;
            }
        }
        return null;
    }

    @Override
    void resetAfterAbilityFinished() {

    }

    @Override
    void processActionDuringAbility() {

    }
}
