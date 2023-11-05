package dirkyg.mcrpg.Classes.WarriorClasses;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.NearDeathExperience;
import dirkyg.mcrpg.SpecialAbilities.Berserk;

public class Warrior extends RPGClass implements Listener {

    UUID uuid;
    private float damageMultiplier = 2.0f;
    private float baseSpeed = .13f;
    private final Set<UUID> processedEntities = new HashSet<>();

    private float projectileReductionMultiplier = .5f;

    NearDeathExperience nearDeathExperience;
    RPGClass activeClass;

    public Warrior(UUID uuid, RPGClass activeClass) {
        this.uuid = uuid;
        this.activeClass = activeClass;
        nearDeathExperience = new NearDeathExperience(uuid);
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
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(32.0);
            nearDeathExperience.start();
            setCurrentlyActive(true);
            player.setInvisible(false);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            nearDeathExperience.stop();
            setCurrentlyActive(false);
            player.setInvisible(false);
        }
    }

    public void processDamageChanges(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        Entity damaged = event.getEntity();
        if (!(damaged instanceof LivingEntity le) || processedEntities.contains(damaged.getUniqueId())) {
            return;
        }
        if (damager instanceof Arrow arrow) {
            double originalDamage = event.getFinalDamage();
            double modifiedDamage = originalDamage * projectileReductionMultiplier;
            processedEntities.add(damaged.getUniqueId());
            le.damage(modifiedDamage, player);
            processedEntities.remove(damaged.getUniqueId());
            event.setCancelled(true);
            return;
        }
        if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
        }
        if (damager == player && !(activeClass.swapper.activeAbility instanceof Berserk
                && activeClass.swapper.activeAbility.isHappening())) {
            double originalDamage = event.getFinalDamage();
            double modifiedDamage = originalDamage * damageMultiplier;
            processedEntities.add(damaged.getUniqueId());
            le.damage(modifiedDamage, player);
            processedEntities.remove(damaged.getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processDamageChanges(event);
    }

    @Override
    public void processClassUpgrade() {
    }
}
