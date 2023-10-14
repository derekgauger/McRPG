package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.DiggingSkill;
import dirkyg.mcrpg.Skills.LoggingSkill;
import dirkyg.mcrpg.Skills.MiningSkill;
import dirkyg.mcrpg.Skills.Skill;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class InstaBreak extends Ability implements Listener {
    public InstaBreak(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onBlockDamage(BlockDamageEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID || player.isSneaking() || !isHappening) {
            return;
        }
        ItemStack itemUsed = player.getInventory().getItemInMainHand();
        Block block = event.getBlock();
        boolean doBreak = false;
        if (classifier.equalsIgnoreCase("Mining")) {
            doBreak = Utils.isMineMat(block.getType()) && Utils.isPickaxe(itemUsed.getType());
        } else if (classifier.equalsIgnoreCase("Logging")) {
            doBreak = Utils.isWood(block.getType()) && Utils.isAxe(itemUsed.getType());
        } else if (classifier.equalsIgnoreCase("Digging")) {
            doBreak = Utils.isDigMat(block.getType()) && Utils.isShovel(itemUsed.getType());
        }
        if (doBreak && player.getGameMode() != GameMode.CREATIVE) {
            event.setCancelled(true);
            event.getBlock().breakNaturally(player.getInventory().getItemInMainHand());
            player.playSound(block.getLocation(), Sound.BLOCK_TUFF_BREAK, 1.0f, 1.0f);
        }
    }


    @Override
    public String toString() {
        return "Insta-break";
    }

    @Override
    void resetAfterAbilityFinished() {

    }

    @Override
    void processActionDuringAbility() {

    }
}
