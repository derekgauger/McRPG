package dirkyg.mcrpg.Classes.RogueClasses;

import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.Classes.WarriorClasses.Berserker;
import dirkyg.mcrpg.Classes.WarriorClasses.Elemental;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import net.minecraft.server.level.EntityPlayer;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Rogue extends RPGClass implements Listener {

    UUID uuid;
    float damageMultiplier = .75f;
    boolean doubleJumpUsed = false;
    long noFallDamageCooldown = 0L;
    float knockBackMultiplier = 1.5f;
    float baseSpeed = .35f;
    float heavyWeaponSpeed = .15f;
    float climbSpeed = .3f;

    RPGClass activeClass;
    Assassin assassin;
    Trickster trickster;

    public Rogue(UUID uuid) {
        this.uuid = uuid;
        assassin = new Assassin(uuid);
        trickster = new Trickster(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        player.setWalkSpeed(baseSpeed);
        setCurrentlyActive(true);
        player.setMaxHealth(14);
        player.addPotionEffect(new PotionEffect(PotionEffectType.WATER_BREATHING, PotionEffect.INFINITE_DURATION, 1));
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.setMaxHealth(20.0);
        }
        setCurrentlyActive(false);
        player.setInvisible(false);
        player.removePotionEffect(PotionEffectType.WATER_BREATHING);
    }

    @Override
    public void setSubClass(Class subClassType) {
        if (activeClass != null) {
            activeClass.deactivatePlayer();
        }
        if (subClassType.equals(Assassin.class)) {
            activeClass = assassin;
        } else if (subClassType.equals(Trickster.class)) {
            activeClass = trickster;
        } else {
            return;
        }
        activeClass.activatePlayer();
    }

    public void reduceDamage(EntityDamageByEntityEvent event) {
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

    public void processMoreKnockback(EntityDamageByEntityEvent event) {
        if (event.getDamager().getUniqueId() != uuid) {
            return;
        }
        if (event.getEntity() instanceof LivingEntity target) {
            Vector knockback = target.getLocation().toVector().subtract(event.getDamager().getLocation().toVector()).normalize();
            knockback.setY(0.5);
            knockback.multiply(knockBackMultiplier);
            target.setVelocity(knockback);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        reduceDamage(event);
        processMoreKnockback(event);
    }

    public void processDoubleJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() != GameMode.SURVIVAL || player.isFlying()) {
            return;
        }
        if (player.isOnGround()) {
            doubleJumpUsed = false;
            player.setAllowFlight(false);
        } else {
            if (!doubleJumpUsed) {
                player.setAllowFlight(true);
            }
        }
    }

    public void processInvisible(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.AIR) {
            player.setInvisible(true);
        } else {
            player.setInvisible(false);
        }
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

    public void processHeavyWeaponsOut(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (Utils.isSword(currentItem.getType()) || Utils.isAxe(currentItem.getType())) {
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
        processDoubleJump(event);
        processInvisible(event);
        processClimb(event);
        processHeavyWeaponsOut(event);
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        if (player.getGameMode() == GameMode.SURVIVAL) {
            event.setCancelled(true);
            player.setFlying(false);
            player.setAllowFlight(false);
            if (!doubleJumpUsed) {
                Vector jump = player.getLocation().getDirection().multiply(0.75).setY(1);
                player.setVelocity(player.getVelocity().add(jump));
                doubleJumpUsed = true;
                noFallDamageCooldown = System.currentTimeMillis() + 5000;
            }
        }
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
                if (System.currentTimeMillis() <= noFallDamageCooldown) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
