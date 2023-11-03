package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isPlaced;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isPickaxe;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.Utilities.BlockPoints.DiggingBlockPoints;
import dirkyg.mcrpg.Utilities.BlockPoints.MiningBlockPoints;
import dirkyg.mcrpg.SpecialAbilities.InstaBreak;

public class MiningSkill extends Skill implements Listener {

    SpecialAbility instaBreak;

    public MiningSkill(UUID uuid) {
        super.uuid = uuid;
        instaBreak = new InstaBreak(uuid, this.toString());
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Mining";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (player.getUniqueId() != uuid || !isPickaxe(currentItemType) || !player.isSneaking() || instaBreak.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            instaBreak.processAbility(event, player, "Right Click");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack usedItem = player.getInventory().getItemInMainHand();
        Material usedItemType = usedItem.getType();
        Block brokenBlock = event.getBlock();
        String blockTypeName = brokenBlock.getType().toString();
        if (isPlaced(brokenBlock)) {
            return;
        }
        if (MiningBlockPoints.contains(blockTypeName) && isPickaxe(usedItemType)) {
            double incrementAmount = MiningBlockPoints.getEnumpoints(blockTypeName);
            SkillManager.processSkillIncrement(player, this, incrementAmount * SkillManager.miningXpMultipler);
            if (incrementAmount >= 5) {
                McRPG.LOGGER.info(player.getName() + " mined " + blockTypeName);
            }
        }
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
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
                int instaBreakTimeUpgrade = 3;
                instaBreak.duration += instaBreakTimeUpgrade;
                if (player == null) {
                    return;
                }
                player.sendMessage(colorText("&6Ability Upgraded | " + instaBreak + " | Duration (" + (instaBreak.duration - instaBreakTimeUpgrade) + " -> " + instaBreak.duration + ") seconds"));
        }
    }
}
