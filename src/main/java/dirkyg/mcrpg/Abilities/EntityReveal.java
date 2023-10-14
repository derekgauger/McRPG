package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.Skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class EntityReveal extends Ability{

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
    void resetAfterAbilityFinished() {
        disableEntityGlow();
    }

    @Override
    void processActionDuringAbility() {
        Player player = Bukkit.getPlayer(playerUUID);
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

    private void disableEntityGlow() {
        for (UUID uuid : glowingEntities) {
            Entity entity = Bukkit.getEntity(uuid);
            if (entity != null) {
                entity.setGlowing(false);
            }
        }
        glowingEntities.clear();
    }

}
