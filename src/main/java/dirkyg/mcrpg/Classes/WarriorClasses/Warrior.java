package dirkyg.mcrpg.Classes.WarriorClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.NearDeathExperience;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Dash;

public class Warrior extends RPGClass implements Listener {

    UUID uuid;
    private final float damageMultiplier = 2.0f;
    private final float baseSpeed = .13f;

    private float projectileReductionMultiplier = .5f;

    RPGClass activeClass;
    Berserker berserker;
    Elemental elemental;

    SpecialAbility dash;
    NearDeathExperience nearDeathExperience;

    public Warrior(UUID uuid) {
        this.uuid = uuid;
        berserker = new Berserker(uuid);
        elemental = new Elemental(uuid);
        dash = new Dash(uuid, this.toString());
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
        }
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
        if (item == null || !isSword(item.getType()) || !player.isSneaking() || dash.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            dash.processAbility(event, player, "Right Click");
        }
    }

    public void processDamageChanges(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        if (damager instanceof Arrow arrow) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * projectileReductionMultiplier;
            event.setDamage(modifiedDamage);
        }
        if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
        }
        if (damager == player) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * damageMultiplier;
            event.setDamage(modifiedDamage);
        }
    }


    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processDamageChanges(event);
    }
}
