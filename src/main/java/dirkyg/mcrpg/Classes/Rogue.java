package dirkyg.mcrpg.Classes;

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
import org.bukkit.util.Vector;

import java.util.UUID;

public class Rogue extends RPGClass implements Listener {

    UUID uuid;
    double damageMultiplier = .75;
    boolean doubleJumpUsed = false;
    long noFallDamageCooldown = 0L;
    float knockBackMultiplier = 1.5f;
    float baseSpeed = .35f;
    float fastWalkSpeed = .45f;


    public Rogue(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        player.setWalkSpeed(baseSpeed);
        setCurrentlyActive(true);
    }

    @Override
    void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.setMaxHealth(20.0);
        }
        setCurrentlyActive(false);
        player.setInvisible(false);
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
        if (player.getUniqueId() != uuid) {
            return;
        }
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

    public void processNothingInHand(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.AIR) {
            player.setWalkSpeed(fastWalkSpeed);
        } else {
            player.setWalkSpeed(baseSpeed);
        }
    }

    public void processInvisible(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.AIR) {
            player.setInvisible(true);
        } else {
            player.setInvisible(false);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        processDoubleJump(event);
        processNothingInHand(event);
        processInvisible(event);
    }

    @EventHandler
    public void onToggleFlight(PlayerToggleFlightEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
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
        if (event.getEntity() instanceof Player) {
            Player player = (Player) event.getEntity();
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
