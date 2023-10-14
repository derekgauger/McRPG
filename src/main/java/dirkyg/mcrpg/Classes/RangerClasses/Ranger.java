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

public class Ranger extends RPGClass implements Listener {

    float projectileDamageMultipler = 3.0f;
    float meleeDamageMultiplier = .75f;
    float baseSpeed = .275f;
    float archerBiomeSpeed = .325f;
    float heavyWeaponSpeed = .15f;
    float climbSpeed = .3f;
    Biome[] archerBiomes = new Biome[] {Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.DARK_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
                                        Biome.TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.SNOWY_TAIGA, Biome.JUNGLE,
                                        Biome.BAMBOO_JUNGLE, Biome.SPARSE_JUNGLE};

    RPGClass activeClass;
    Hunter hunter;
    Sniper sniper;

    public Ranger(UUID uuid) {
        this.uuid = uuid;
        hunter = new Hunter(uuid);
        sniper = new Sniper(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(baseSpeed);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
        }
        setCurrentlyActive(true);
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
        }
        setCurrentlyActive(false);
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

    public void processClimb(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            Material blockInFront = player.getEyeLocation().add(player.getLocation().getDirection()).getBlock().getType();
            Material blockAboveFront = player.getEyeLocation().add(player.getLocation().getDirection()).add(0, 1, 0).getBlock().getType();
            Material blockTwoAboveFront = player.getEyeLocation().add(player.getLocation().getDirection()).add(0, 2, 0).getBlock().getType();
            if (blockInFront.isSolid() || blockAboveFront.isSolid()) {
                Vector velocity = player.getVelocity();
                velocity.setY(climbSpeed);
                if (!blockTwoAboveFront.isSolid() && blockAboveFront.isSolid()) {
                    velocity.add(player.getLocation().getDirection().multiply(0.2));
                }
                player.setVelocity(velocity);
            }
        }
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
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processClimb(event);
        processSpeedChanges(event);
    }

    public void processMoreProjectileDamage(EntityDamageByEntityEvent event) {
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

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processMoreProjectileDamage(event);
    }
}
