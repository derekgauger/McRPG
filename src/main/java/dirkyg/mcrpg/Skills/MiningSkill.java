package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.EntityReveal;
import dirkyg.mcrpg.Abilities.InstaBreak;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class MiningSkill extends Skill implements Listener {

    Ability activeAbility;
    InstaBreak instaBreak;
    EntityReveal entityReveal;

    public MiningSkill(UUID uuid) {
        super.uuid = uuid;
        instaBreak = new InstaBreak(uuid, this);
        entityReveal = new EntityReveal(uuid, this);
        activeAbility = instaBreak;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Mining";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (!Utils.isPickaxe(currentItemType)) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        if (activeAbility.isHappening()) {
            return;
        }
        switch (event.getAction()) {
            case RIGHT_CLICK_BLOCK -> {
                activeAbility.processAbility(event, player, "Right Click");
            }
            case RIGHT_CLICK_AIR -> {
                if (instaBreak.isHappening() || entityReveal.isHappening()) {
                    return;
                }
                if (activeAbility instanceof InstaBreak) {
                    activeAbility = entityReveal;
                } else if (activeAbility instanceof EntityReveal) {
                    activeAbility = instaBreak;
                }
                Utils.sendActionBar(player, Utils.chat("&dYour pickaxe ability is now in " + activeAbility.toString() + " mode!"));
            }
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
        if (Utils.isMineMat(brokenBlock.getType()) && Utils.isPickaxe(usedItemType)) {
            SkillManager.processSkillIncrement(player, this, 1);
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
                int entityRevealTimeUpgrade = 3;
                instaBreak.duration += instaBreakTimeUpgrade;
                entityReveal.duration += entityRevealTimeUpgrade;
                if (player == null) {
                    return;
                }
                player.sendMessage(Utils.chat("&6Ability Upgraded | " + instaBreak + " | Duration (" + (instaBreak.duration - instaBreakTimeUpgrade) + " -> " + instaBreak.duration + ") seconds"));
                player.sendMessage(Utils.chat("&6Ability Upgraded | " + entityReveal + " | Duration (" + (entityReveal.duration - entityRevealTimeUpgrade) + " -> " + entityReveal.duration + ") seconds"));
        }
    }
}
