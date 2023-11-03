package dirkyg.mcrpg.SpecialAbilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utilities.BlockPoints.DiggingBlockPoints;
import dirkyg.mcrpg.Utilities.BlockPoints.LoggingBlockPoints;
import dirkyg.mcrpg.Utilities.BlockPoints.MiningBlockPoints;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

import static dirkyg.mcrpg.Utilities.BooleanChecks.*;

public class InstaBreak extends SpecialAbility implements Listener {
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
        Material type = block.getType();
        String materialName = type.toString();
        boolean doBreak = false;
        if (classifier.equalsIgnoreCase("Mining")) {
            doBreak = MiningBlockPoints.contains(materialName) && isPickaxe(itemUsed.getType());
        } else if (classifier.equalsIgnoreCase("Logging")) {
            doBreak = LoggingBlockPoints.contains(materialName) && isAxe(itemUsed.getType());
        } else if (classifier.equalsIgnoreCase("Digging")) {
            doBreak = DiggingBlockPoints.contains(materialName) && isShovel(itemUsed.getType());
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
    void resetAfterAbilityFinished(Player player) {

    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {

    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }
}
