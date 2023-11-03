package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isShovel;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isPlaced;
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
import dirkyg.mcrpg.SpecialAbilities.InstaBreak;

public class DiggingSkill extends Skill implements Listener {

    final double xpMultipler = 3;

    SpecialAbility instaBreak;

    public DiggingSkill(UUID uuid) {
        super.uuid = uuid;
        instaBreak = new InstaBreak(uuid, this.toString());
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Digging";
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
        if (DiggingBlockPoints.contains(blockTypeName) && isShovel(usedItemType)) {
            double incrementAmount = DiggingBlockPoints.getEnumpoints(blockTypeName);
            SkillManager.processSkillIncrement(player, this, incrementAmount * SkillManager.diggingXpMultipler);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (player.getUniqueId() != uuid || !player.isSneaking() || instaBreak.isHappening() || !isShovel(currentItemType)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            instaBreak.processAbility(event, player, "Right Click");
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
