package dirkyg.mcrpg.Classes;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Warrior extends RPGClass implements Listener {

    UUID uuid;
    private final double damageMultiplier = 2.0;
    private float baseSpeed = .15f;
    private float fastWalkSpeed = .3f;
    private long immunityDuration = 10000; // seconds
    private long immuneUntil = 0;
    private long immunityCooldown = 5 * 60 * 1000;
    private long immunityCooldownResetTime = 0;

    public Warrior(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(baseSpeed);
            player.setMaxHealth(32.0);
        }
        setCurrentlyActive(true);

    }

    @Override
    void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.setMaxHealth(20.0);
        }
        setCurrentlyActive(false);
    }

    public void processPlayerDamageMultiplier(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        if (damager instanceof Arrow arrow) {
            damager = (Entity) arrow.getShooter();
        } else if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
        }
        if (damager == player) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * damageMultiplier;
            event.setDamage(modifiedDamage);
        }
    }

    public void processCounterKnockback(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity && !(damager instanceof Player) && player == damaged) {
            Vector knockback = damager.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            knockback.setY(0.5);
            knockback.multiply(0.9f);
            damager.setVelocity(knockback);
        }
    }

    public void processNoKnockback(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity && !(damager instanceof Player) && player == damaged) {
            event.setCancelled(true);
            double newHealth = player.getHealth() - event.getFinalDamage();
            if (newHealth < 0) newHealth = 0;
            player.setHealth(newHealth);
        }
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

    @EventHandler
    public void processFasterSpeed(PlayerMoveEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (Utils.isSword(currentItem.getType())) {
            player.setWalkSpeed(fastWalkSpeed);
        } else {
            player.setWalkSpeed(baseSpeed);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processPlayerDamageMultiplier(event);
        processCounterKnockback(event);
        processNoKnockback(event);
        processPlayerHealthTooLow(event);
    }
}
