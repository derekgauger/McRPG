package dirkyg.mcrpg.Classes.RangerClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isAxe;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.Climb;

public class Ranger extends RPGClass implements Listener {

    float projectileDamageMultipler = 3.0f;
    float meleeDamageMultiplier = .75f;
    float baseSpeed = .275f;
    float archerBiomeSpeed = .325f;
    float heavyWeaponSpeed = .15f;
    Biome[] archerBiomes = new Biome[] {Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.DARK_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
                                        Biome.TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.SNOWY_TAIGA, Biome.JUNGLE,
                                        Biome.BAMBOO_JUNGLE, Biome.SPARSE_JUNGLE};

    RPGClass activeClass;
    Hunter hunter;
    Sniper sniper;

    Climb climb;

    public Ranger(UUID uuid) {
        this.uuid = uuid;
        hunter = new Hunter(uuid);
        sniper = new Sniper(uuid);
        climb = new Climb(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(baseSpeed);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
            climb.start();
            setCurrentlyActive(true);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            climb.stop();
            setCurrentlyActive(false);
        }
    }

    @Override
    public void setSubClass(Class subClassType) {
        if (activeClass != null) {
            activeClass.deactivatePlayer();
        }
        if (subClassType.equals(Hunter.class)) {
            activeClass = hunter;
        } else if (subClassType.equals(Sniper.class)) {
            activeClass = sniper;
        } else {
            return;
        }
        activeClass.activatePlayer();
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processSpeedChanges(event);
    }

    public void processSpeedChanges(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        if (isSword(currentItem.getType()) || isAxe(currentItem.getType())) {
            player.setWalkSpeed(heavyWeaponSpeed);
        } else {
            if (Arrays.asList(archerBiomes).contains(currentBiome)) {
                player.setWalkSpeed(archerBiomeSpeed);
            } else {
                player.setWalkSpeed(baseSpeed);
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processDamageChanges(event);
    }

    public void processDamageChanges(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        boolean isProjectile = false;
        if (damager instanceof Arrow arrow) {
            damager = (Entity) arrow.getShooter();
            isProjectile = true;

        } else if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
            isProjectile = true;
        }
        if (damager == player && isProjectile) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * projectileDamageMultipler;
            event.setDamage(modifiedDamage);
        } else if (damager == player) {
            double originalDamage = event.getDamage();
            double modifiedDamage = originalDamage * meleeDamageMultiplier;
            event.setDamage(modifiedDamage);
        }
    }
}
