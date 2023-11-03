package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;

public class BreedingSkill extends Skill implements Listener {

    private int spawnPercentage = 100;

    public BreedingSkill(UUID uuid) {
        super.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    private List<PotionEffect> initializePotions() {
        List<PotionEffect> availablePotionEffects = new ArrayList<>();
        availablePotionEffects.add(new PotionEffect(PotionEffectType.SPEED, 20 * 2, 0));
        availablePotionEffects.add(new PotionEffect(PotionEffectType.REGENERATION, 20 * 2, 0));
        availablePotionEffects.add(new PotionEffect(PotionEffectType.FAST_DIGGING, 20 * 2, 0));
        availablePotionEffects.add(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 20 * 2, 0));
        return availablePotionEffects;
    }

    @Override
    public String toString() {
        return "Breeding";
    }

    @EventHandler
    public void onEntityBreed(EntityBreedEvent event) {
        LivingEntity entity = event.getBreeder();
        if (!(entity instanceof Player player) || player.getUniqueId() != uuid) {
            return;
        }
        int randomNumber = getRandomNumber(0, 100);
        if (randomNumber <= spawnPercentage) {
            SkillManager.animalBeaconManager.createNewAnimalBeacon(event.getEntity(), initializePotions());
        }
        SkillManager.processSkillIncrement(player, this, SkillManager.breedingXpMultipler);
    }

    private void upgradeSpecialBreedPercent(Player player) {
        int breedingPercentageUpgrade = 1;
        spawnPercentage += breedingPercentageUpgrade;
        player.sendMessage(colorText("&6Ability Upgraded | Breeding Animal Beacons | Percentage (" + (spawnPercentage - breedingPercentageUpgrade) + " -> " + spawnPercentage + ") %"));
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        switch (getLevel()) {
            case 5:
            case 10:
            case 15:
            case 20:
            case 25:
            case 30:
            case 35:
            case 40:
            case 45:
            case 50:
                upgradeSpecialBreedPercent(player);
        }
    }
}
