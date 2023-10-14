package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class Climb extends PassiveAbility implements Listener {

    float climbSpeed = .4f;

    public Climb(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processClimb(event);
    }

    public void processClimb(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            // Check for the block in front of the player's eyes and the block above that
            Material blockInFront = player.getEyeLocation().add(player.getLocation().getDirection()).getBlock().getType();
            Material blockAboveFront = player.getEyeLocation().add(player.getLocation().getDirection()).add(0, 1, 0).getBlock().getType();
            Material blockTwoAboveFront = player.getEyeLocation().add(player.getLocation().getDirection()).add(0, 2, 0).getBlock().getType();
            // If either the block at eye level or the one above that is solid, then allow climbing.
            if (blockInFront.isSolid() || blockAboveFront.isSolid()) {
                Vector velocity = player.getVelocity();
                velocity.setY(climbSpeed);  // adjust this value to control the "climb" speed
                // Check if player is near the top of the wall
                if (!blockTwoAboveFront.isSolid() && blockAboveFront.isSolid()) {
                    // Give a slight forward boost to clear the wall
                    velocity.add(player.getLocation().getDirection().multiply(0.2));
                }
                player.setVelocity(velocity);
            }
        }
    }
}

