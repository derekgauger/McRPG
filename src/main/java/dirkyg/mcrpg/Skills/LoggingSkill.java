package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.InstaBreak;
import dirkyg.mcrpg.Abilities.TreeCapitator;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
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

import java.util.UUID;

public class LoggingSkill extends Skill implements Listener {

    Ability treeCapitator;

    public LoggingSkill(UUID uuid) {
        super.uuid = uuid;
        treeCapitator = new TreeCapitator(uuid, this.toString());
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Logging";
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
        if (Utils.isWood(brokenBlock.getType()) && Utils.isAxe(usedItemType)) {
            SkillManager.processSkillIncrement(player, this, 1);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (player.getUniqueId() != uuid || !player.isSneaking() || treeCapitator.isHappening() || !Utils.isAxe(currentItemType)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            treeCapitator.processAbility(event, player, "Right Click");
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
                int treeCapTimeUpgrade = 3;
                treeCapitator.duration += treeCapTimeUpgrade;
                if (player == null) {
                    return;
                }
                player.sendMessage(Utils.chat("&6Ability Upgraded | " + treeCapitator + " | Duration (" + (treeCapitator.duration - treeCapTimeUpgrade) + " -> " + treeCapitator.duration + ") seconds"));
        }
    }
}
