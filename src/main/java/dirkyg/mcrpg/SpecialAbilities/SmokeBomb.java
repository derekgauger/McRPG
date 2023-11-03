package dirkyg.mcrpg.SpecialAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class SmokeBomb extends SpecialAbility implements Listener {

    Location smokeBombLocation;

    public SmokeBomb(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Smoke Bomb";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        // TODO Auto-generated method stub
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        preventMobTargeting();
        if (iterations % 5 == 0) {
            smokeBombLocation.getWorld().spawnParticle(Particle.SMOKE_LARGE, smokeBombLocation, 1000, 2, 1, 2, 0.05);
        }
    }

    @Override
    void initalizeAbility(Player player) {
        if (player != null) {
            smokeBombLocation = player.getLocation();
        }
    }

    private void preventMobTargeting() {
        for (Entity entity : smokeBombLocation.getWorld().getNearbyEntities(smokeBombLocation, 5, 2, 5)) {
            if (entity instanceof Monster monster) {
                // Check if the monster is targeting a player within the smoke
                if (monster.getTarget() instanceof Player player) {
                    Location monsterLocation = monster.getLocation();
                    Vector directionAway = monsterLocation.toVector().subtract(smokeBombLocation.toVector())
                            .normalize();
                    monster.setVelocity(directionAway);
                    monster.setTarget(null);
                }
            }
        }
    }
}
