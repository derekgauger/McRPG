package dirkyg.mcrpg.SpecialAbilities;

import dirkyg.mcrpg.Skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

public class SuperSwim extends SpecialAbility {

    public SuperSwim(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
    }

    @Override
    public String toString() {
        return "Super Swim";
    }

    @Override
    void resetAfterAbilityFinished() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.removePotionEffect(PotionEffectType.DOLPHINS_GRACE);
        }
    }

    @Override
    void processActionDuringAbility() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player != null) {
            player.addPotionEffect(new PotionEffect(PotionEffectType.DOLPHINS_GRACE, 20 * 2, 1));
        }
    }
}
