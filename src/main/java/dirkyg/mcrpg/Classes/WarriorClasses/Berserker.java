package dirkyg.mcrpg.Classes.WarriorClasses;

import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Berserker extends RPGClass implements Listener {

    float knockbackReductionMultiplier = .5f;
    float counterKnockbackMultipler = .9f;

    public Berserker(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        setCurrentlyActive(true);
    }

    @Override
    public void deactivatePlayer() {
        setCurrentlyActive(false);
    }

    @Override
    public void setSubClass(Class subClassType) {

    }

    public void processCounterKnockback(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity && !(damager instanceof Player) && player == damaged) {
            Vector knockback = damager.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            knockback.setY(0.5);
            knockback.multiply(counterKnockbackMultipler);
            damager.setVelocity(knockback);
        }
    }

    @EventHandler
    public void onPlayerVelocityChange(PlayerVelocityEvent event) {
        if (event.getPlayer().getUniqueId() != uuid || !isCurrentlyActive()) {
            return;
        }
        Vector knockback = event.getVelocity();
        knockback.multiply(knockbackReductionMultiplier);
        event.setVelocity(knockback);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isCurrentlyActive()) {
            processCounterKnockback(event);
        }
    }
}
