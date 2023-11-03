package dirkyg.mcrpg.SpecialAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;

public class Stun extends SpecialAbility implements Listener {

    public float stunDuration = 3;

    public Stun(UUID uuid, String classifier) {
        this.classifier = classifier;
        this.abilityName = this.toString();
        this.playerUUID = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Stun";
    }

    @EventHandler
    public void stunMelee(EntityDamageByEntityEvent event) {
        if (!isHappening() || event.getDamager().getUniqueId() != playerUUID) {
            return;
        }
        LivingEntity entity = (LivingEntity) event.getEntity();
        Location location = entity.getLocation();
        DustOptions dustOptions = new DustOptions(Color.fromRGB(119, 160, 166), 1);
        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations == stunDuration * 20 || entity.isDead()) {
                    this.cancel();
                }
                entity.getWorld().spawnParticle(Particle.REDSTONE, entity.getLocation(), 3, 1, 1.5, 1, 0.1,
                        dustOptions);
                // if (iterations % 20 == 0) { #Make it so you level this up eventually
                // entity.damage(1);
                // }
                entity.teleport(location);
                iterations += 1;
            }
        }.runTaskTimer(McRPG.plugin, 0, 1);
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        // TODO Auto-generated method stub
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        // TODO Auto-generated method stub
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }
}
