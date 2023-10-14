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

public class Healer extends RPGClass implements Listener {

    UUID uuid;
    float baseSpeed = .3f;
    float heavyWeaponSpeed = .15f;
    private final int HEAL_AMOUNT = 1;  //half a heart
    private final int HEAL_INTERVAL = 20 * 2; // 3 seconds

    RPGClass activeClass;
    Cleric cleric;
    Necromancer necromancer;

    public Healer (UUID uuid) {
        this.uuid = uuid;
        cleric = new Cleric(uuid);
        necromancer = new Necromancer(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(10.0);
            player.setWalkSpeed(baseSpeed);
            autoHeal(player);
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, PotionEffect.INFINITE_DURATION, 2));
        }
        setCurrentlyActive(true);
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setWalkSpeed(.2f);
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
        setCurrentlyActive(false);
    }

    @Override
    public void setSubClass(Class subClassType) {
        if (activeClass != null) {
            activeClass.deactivatePlayer();
        }
        if (subClassType.equals(Cleric.class)) {
            activeClass = cleric;
        } else if (subClassType.equals(Necromancer.class)) {
            activeClass = necromancer;
        } else {
            return;
        }
        activeClass.activatePlayer();
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

    private void autoHeal(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCurrentlyActive()) {
                    this.cancel();
                    return;
                }
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + HEAL_AMOUNT));
                }
            }
        }.runTaskTimer(McRPG.plugin, 0, HEAL_INTERVAL);
    }
}
