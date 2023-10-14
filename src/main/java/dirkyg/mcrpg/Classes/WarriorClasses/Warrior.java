package dirkyg.mcrpg.Classes.WarriorClasses;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.Dash;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.Classes.WarriorClasses.Berserker;
import dirkyg.mcrpg.Classes.WarriorClasses.Elemental;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class Warrior extends RPGClass implements Listener {

    UUID uuid;
    private final float damageMultiplier = 2.0f;
    private final float baseSpeed = .13f;
    private final long immunityDuration = 10000; // seconds
    private final long immunityCooldown = 5 * 60 * 1000;
    private long immuneUntil = 0;
    private long immunityCooldownResetTime = 0;
    private float projectileReductionMultiplier = .5f;

    RPGClass activeClass;
    Berserker berserker;
    Elemental elemental;

    Ability dash;

    public Warrior(UUID uuid) {
        this.uuid = uuid;
        berserker = new Berserker(uuid);
        elemental = new Elemental(uuid);
        dash = new Dash(uuid, this.toString());
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Warrior";
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
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.setMaxHealth(20.0);
        }
        setCurrentlyActive(false);
    }

    @Override
    public void setSubClass(Class subClassType) {
        if (activeClass != null) {
            activeClass.deactivatePlayer();
        }
        if (subClassType.equals(Berserker.class)) {
            activeClass = berserker;
        } else if (subClassType.equals(Elemental.class)) {
            activeClass = elemental;
        } else {
            return;
        }
        activeClass.activatePlayer();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !Utils.isSword(item.getType()) || !player.isSneaking() || dash.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            dash.processAbility(event, player, "Right Click");
        }
    }

    public void processPlayerDamageMultiplier(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
        }
        if (damager == player) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * damageMultiplier;
            event.setDamage(modifiedDamage);
        }
    }

    public void processPlayerProjectileDamageReduction(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        if (damager instanceof Arrow arrow) {
            damager = (Entity) arrow.getShooter();
        }
        if (damager == player) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * projectileReductionMultiplier;
            event.setDamage(modifiedDamage);
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
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processPlayerDamageMultiplier(event);
        processPlayerHealthTooLow(event);
        processPlayerProjectileDamageReduction(event);
    }
}
