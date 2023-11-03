package dirkyg.mcrpg.Classes.HealerClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isAxe;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.AutoHeal;

public class Healer extends RPGClass implements Listener {

    UUID uuid;
    float baseSpeed = .3f;
    float heavyWeaponSpeed = .15f;

    RPGClass activeClass;
    Cleric cleric;
    Necromancer necromancer;

    AutoHeal autoHeal;

    public Healer (UUID uuid) {
        this.uuid = uuid;
        cleric = new Cleric(uuid);
        necromancer = new Necromancer(uuid);
        autoHeal = new AutoHeal(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10.0);
            player.setWalkSpeed(baseSpeed);
            autoHeal.start();
            autoHeal.startAutoHealing(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, 2));
            setCurrentlyActive(true);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setWalkSpeed(.2f);
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
            autoHeal.stop();
            setCurrentlyActive(false);
        }
    }

    @Override
    public String toString() {
        return "Healer";
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            if (player.getUniqueId() != uuid) {
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    public void processHeavyWeaponsOut(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (isSword(currentItem.getType()) || isAxe(currentItem.getType())) {
            player.setWalkSpeed(heavyWeaponSpeed);
        } else {
            player.setWalkSpeed(baseSpeed);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        if (!player.hasPotionEffect(PotionEffectType.DOLPHINS_GRACE)) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, 1));
        }
        processHeavyWeaponsOut(event);
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
