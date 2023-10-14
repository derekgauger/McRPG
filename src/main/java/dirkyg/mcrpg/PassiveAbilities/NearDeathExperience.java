package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;

public class NearDeathExperience extends PassiveAbility implements Listener {

    private final long immunityDuration = 10000; // seconds
    private final long immunityCooldown = 5 * 60 * 1000;
    private long immuneUntil = 0;
    private long immunityCooldownResetTime = 0;

    public NearDeathExperience(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processPlayerHealthTooLow(event);
    }

    public void processPlayerHealthTooLow(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (!(damager instanceof Player) && player == damaged) {
            double newHealth = player.getHealth() - event.getFinalDamage();
            long currentTime = System.currentTimeMillis();
            if (player.hasPotionEffect(PotionEffectType.DAMAGE_RESISTANCE)) {
                return;
            }
            if (newHealth <= 0 && currentTime >= immunityCooldownResetTime) {
                event.setCancelled(true);
                player.setHealth(1);
                immuneUntil = System.currentTimeMillis() + immunityDuration;
                immunityCooldownResetTime = immuneUntil + immunityCooldown;
                PotionEffect immunityEffect = new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, 20 * 10, 4);
                player.addPotionEffect(immunityEffect, true);
            }
        }
    }
}
