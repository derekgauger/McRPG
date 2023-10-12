package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.UUID;

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
        if (player.getUniqueId() != uuid) {
            return;
        }
        Block block = event.getBlockPlaced();
        if (Utils.isCrop(block.getType())) {
            return;
        }
        if (block.getType().isSolid()) {
            int randomNumber = Utils.getRandomNumber(0, 100);
            if (randomNumber < percentJumpBoostChance) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.JUMP, 20 * 5, 1));
            }
            SkillManager.processSkillIncrement(player, this, 1);
        }
    }

    private void upgradeJumpBoostPercentage(Player player) {
        int buildingPercentageUpgrade = 2;
        percentJumpBoostChance += buildingPercentageUpgrade;
        player.sendMessage(Utils.chat("&6Ability Upgraded | Building Jump Boost | Percentage (" + (percentJumpBoostChance - buildingPercentageUpgrade) + " -> " + percentJumpBoostChance + ") %"));
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
