package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class DoubleJump extends PassiveAbility implements Listener {

    boolean doubleJumpUsed = false;
    long noFallDamageCooldown = 0L;

    public DoubleJump(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processDoubleJump(event);
    }

    public void processDoubleJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getGameMode() == GameMode.CREATIVE || player.isFlying()) {
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
                Vector jump = player.getLocation().getDirection().multiply(.9).setY(1);
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
