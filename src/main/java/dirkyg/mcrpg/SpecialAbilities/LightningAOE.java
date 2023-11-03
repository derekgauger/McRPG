package dirkyg.mcrpg.SpecialAbilities;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

import dirkyg.mcrpg.McRPG;
import static dirkyg.mcrpg.Utilities.Visuals.*;

public class LightningAOE extends SpecialAbility implements Listener {

    HashMap<UUID, Long> entities = new HashMap<UUID, Long>();
    boolean active = false;

    public LightningAOE(UUID uuid, String classifer) {
        this.classifier = classifer;
        this.playerUUID = uuid;
        this.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        entities.clear();
        active = false;

    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        if (player == null) {
            return;
        }
        // if (iterations % 5 == 0) {
        drawParticleCircle(player.getLocation(), 5, 50, new DustOptions(Color.fromRGB(15, 225, 252), .5f));
        // }
        // Check for hostile mobs in the defined radius around the player
        for (Entity entity : player.getNearbyEntities(5, 5, 5)) {
            long currentTime = System.currentTimeMillis();
            UUID uuid = entity.getUniqueId();
            if (entities.containsKey(uuid) && currentTime < entities.get(uuid)) {
                continue;
            }
            // Calculate distance considering only the X and Z coordinates for a 2D circle
            if (entity instanceof Monster && entity.getLocation().distanceSquared(player.getLocation()) <= 5 * 5) {
                // If a hostile mob is found and it's within the circle, strike it with
                // lightning
                entity.getWorld().strikeLightning(entity.getLocation());
                entities.put(uuid, currentTime + 2500);
            }
        }
    }

    @Override
    public String toString() {
        return "Lightning Strikes";
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }
}
