package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isCrop;
import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;

public class BuildingSkill extends Skill implements Listener {

    private int percentJumpBoostChance = 2;

    public BuildingSkill(UUID uuid) {
        super.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Building";
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlockPlaced();
        block.setMetadata("playerPlaced", new FixedMetadataValue(McRPG.plugin, true));
        if (player.getUniqueId() != uuid || isCrop(block.getType())) {
            return;
        }
        if (block.getType().isSolid()) {
            int randomNumber = getRandomNumber(0, 100);
            if (randomNumber < percentJumpBoostChance) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 1));
            }
            SkillManager.processSkillIncrement(player, this, 1);
        }
    }

    private void upgradeJumpBoostPercentage(Player player) {
        int buildingPercentageUpgrade = 2;
        percentJumpBoostChance += buildingPercentageUpgrade;
        player.sendMessage(colorText("&6Ability Upgraded | Building Jump Boost | Percentage (" + (percentJumpBoostChance - buildingPercentageUpgrade) + " -> " + percentJumpBoostChance + ") %"));
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
                upgradeJumpBoostPercentage(player);
        }
    }
}
