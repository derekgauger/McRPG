package dirkyg.mcrpg.Classes.WarriorClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;
import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.SpecialAbilities.Dash;
import dirkyg.mcrpg.SpecialAbilities.GroundSlam;
import dirkyg.mcrpg.SpecialAbilities.LightningAOE;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Swapper;

public class Elemental extends RPGClass implements Listener {

    public int specialAttackPercentage = 100;
    public int elementalMeleeDuration = 3;
    private DamageCause[] immunities = new DamageCause[] {DamageCause.LIGHTNING, DamageCause.FREEZE, DamageCause.FIRE, DamageCause.FIRE_TICK};

    LightningAOE lightningAOE;
    Dash dash;
    GroundSlam groundSlam;
    
    private final Set<UUID> processedEntities = new HashSet<>();

    public Elemental(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Warrior(uuid, this);
        dash = new Dash(uuid, this.toString());
        lightningAOE = new LightningAOE(uuid, this.toString());
        groundSlam = new GroundSlam(uuid, this.toString());
        swapper = new Swapper(uuid, this, new SpecialAbility[] {dash, lightningAOE, groundSlam});
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        baseClass.activatePlayer();
        setCurrentlyActive(true);;
    }

    @Override
    public void deactivatePlayer() {
        baseClass.deactivatePlayer();
        setCurrentlyActive(false);
    }

    @Override
    public String toString() {
        return "Elemental";
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (player.getUniqueId() != uuid || !isCurrentlyActive() || currentItem == null || !isSword(currentItem.getType()) || player.isSneaking() || swapper.activeAbility.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            swapper.activeAbility.processAbility(event, player, "Right Click");
        }
    }

    public void lightningMelee(EntityDamageByEntityEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();
        Location location = entity.getLocation();
        processedEntities.add(entity.getUniqueId());
        event.setCancelled(true);
        entity.damage(event.getDamage(), player);
        entity.getWorld().spawnEntity(location, EntityType.LIGHTNING);
        DustOptions dustOptions = new DustOptions(Color.fromRGB(110, 213, 250), 1);
        new BukkitRunnable() {
            int iterations = 0;
            @Override
            public void run() {
                if (iterations == elementalMeleeDuration * 20 || entity.isDead()) {
                    processedEntities.remove(entity.getUniqueId());
                    this.cancel();
                    return;
                }
                entity.getWorld().spawnParticle(Particle.REDSTONE, entity.getLocation(), 3, 1, 1.5, 1, 0.1, dustOptions);
                iterations += 1;
            
            }
        }.runTaskTimer(McRPG.plugin, 0, 1);
    }

    public void fireMelee(EntityDamageByEntityEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();
        entity.setFireTicks(5 * 20);
        processedEntities.add(entity.getUniqueId());
        event.setCancelled(true);
        entity.damage(event.getDamage(), player);
        new BukkitRunnable() {
            int iterations = 0;
            @Override
            public void run() {
                if (iterations == elementalMeleeDuration * 20 || entity.isDead()) {
                    processedEntities.remove(entity.getUniqueId());
                    this.cancel();
                    return;
                }
                entity.getWorld().spawnParticle(Particle.SMALL_FLAME, entity.getLocation(), 3, 1, 1, 1, 0.1);
                iterations += 1;
            
            }
        }.runTaskTimer(McRPG.plugin, 0, 1);
    }

    public void iceMelee(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(damager instanceof Player player) || !(entity instanceof LivingEntity le)) {
            return;
        }
        Location location = le.getLocation();
        processedEntities.add(le.getUniqueId());
        event.setCancelled(true);
        le.damage(event.getDamage(), player);
        new BukkitRunnable() {
            int iterations = 0;
            @Override
            public void run() {
                if (iterations == elementalMeleeDuration * 20 || le.isDead()) {
                    processedEntities.remove(le.getUniqueId());
                    this.cancel();
                    return;
                }
                le.getWorld().spawnParticle(Particle.SNOWFLAKE, le.getLocation(), 3, 1, 1.5, 1, 0.1);
                if (iterations  % 20 == 0) {
                    le.damage(1, player);
                }
                le.teleport(location);
                iterations += 1;
            }
        }.runTaskTimer(McRPG.plugin, 0, 1);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!isCurrentlyActive() || damager.getUniqueId() != uuid || !(damager instanceof Player player) || !(entity instanceof LivingEntity le) || processedEntities.contains(entity.getUniqueId())) {
            return;
        }
        if (getRandomNumber(0, 100) <= specialAttackPercentage) {
            switch (getRandomNumber(0, 3)) {
                case 0:
                    lightningMelee(event);
                    break;
                    case 1:
                    fireMelee(event);
                    break;
                    case 2:
                    iceMelee(event);
                    break;
            }
        }
    }

    @EventHandler
    public void onDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player) || !isCurrentlyActive()) {
            return;
        }
        DamageCause dc = event.getCause();
        if (Arrays.asList(immunities).contains(dc)) {
            event.setCancelled(true);
        }
    }
}