package dirkyg.mcrpg.Classes.RangerClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isAxe;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

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
    private final Set<UUID> processedEntities = new HashSet<>();

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
    public String toString() {
        return "Ranger";
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
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity le) || processedEntities.contains(entity.getUniqueId())) {
            return;
        }
        boolean isProjectile = false;
        if (damager instanceof Arrow arrow) {
            damager = (Entity) arrow.getShooter();
            isProjectile = true;

        } else if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
            isProjectile = true;
        }
        if (damager == player && isProjectile) {
            event.setCancelled(true);
            processedEntities.add(entity.getUniqueId());
            double originalDamage = event.getFinalDamage();
            double modifiedDamage = originalDamage * projectileDamageMultipler;
            le.damage(modifiedDamage, player);
            processedEntities.remove(entity.getUniqueId());
        } else if (damager == player) {
            event.setCancelled(true);
            processedEntities.add(entity.getUniqueId());
            double originalDamage = event.getFinalDamage();
            double modifiedDamage = originalDamage * meleeDamageMultiplier;
            le.damage(modifiedDamage, player);
            processedEntities.remove(entity.getUniqueId());
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
