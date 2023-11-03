package dirkyg.mcrpg.SpecialAbilities;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EntityReveal extends SpecialAbility {

    HashSet<UUID> glowingEntities;

    public EntityReveal(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        glowingEntities = new HashSet<>();
    }

    @Override
    public String toString() {
        return "Entity Reveal";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        disableEntityGlow();
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        if (player != null) {
            enableEntityGlow(player.getNearbyEntities(200, 200, 200));
        }
    }

    public void enableEntityGlow(List<Entity> entities) {
        for (Entity entity : entities) {
            UUID uuid = entity.getUniqueId();
            if (!glowingEntities.contains(uuid)) {
                entity.setGlowing(true);
                glowingEntities.add(uuid);
            }
        }
    }

    public void disableEntityGlow() {
        for (UUID uuid : glowingEntities) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.setGlowing(false);
            }
        }
        glowingEntities.clear();
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }

}
