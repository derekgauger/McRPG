package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import static dirkyg.mcrpg.Utilities.BooleanChecks.*;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;

public class TreeCapitator extends Ability implements Listener {

    private final int MAX_NUM_TREE_CAP_BLOCKS = 100;
    private boolean continueTreeCapping;

    public TreeCapitator(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "TreeCapitator";
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID || !isHappening || player.isSneaking()) {
            return;
        }
        ItemStack usedItem = player.getInventory().getItemInMainHand();
        Material usedItemType = usedItem.getType();
        Block brokenBlock = event.getBlock();
        if (isWood(brokenBlock.getType()) && isAxe(usedItemType)) {
            continueTreeCapping = true;
            breakConnectedLogs(brokenBlock, new HashSet<>(), player);
        }
    }

    private void breakConnectedLogs(Block block, Set<Block> checkedBlocks, Player player) {
        if (!continueTreeCapping || checkedBlocks.contains(block)) {
            return;
        }
        if (checkedBlocks.size() >= MAX_NUM_TREE_CAP_BLOCKS) {
            player.sendMessage(colorText("&cYou reached the max number of blocks that can be broken with tree-capitator (" + MAX_NUM_TREE_CAP_BLOCKS + ")!"));
            continueTreeCapping = false;
        }
        if (isLog(block.getType())) {
            checkedBlocks.add(block);
            block.breakNaturally(player.getInventory().getItemInMainHand());
            ItemStack itemInHand = player.getInventory().getItemInMainHand();
            if (itemInHand.containsEnchantment(Enchantment.DURABILITY)) {
                int level = itemInHand.getEnchantmentLevel(Enchantment.DURABILITY);
                if (Math.random() * 100 <= (100 / (level + 1))) {
                    reduceDurability(itemInHand, 2, player);
                }
            } else {
                reduceDurability(itemInHand, 2, player);
            }
            for (int x = -1; x <= 1; x++) {
                for (int y = -1; y <= 1; y++) {
                    for (int z = -1; z <= 1; z++) {
                        Block neighbor = block.getRelative(x, y, z);
                        breakConnectedLogs(neighbor, checkedBlocks, player);
                    }
                }
            }
        }
    }

    private void reduceDurability(ItemStack item, int amount, Player player) {
        if (item != null && item.getType() != Material.AIR) {
            int newDurability = item.getDurability() + amount;
            if (newDurability > item.getType().getMaxDurability() || newDurability < 0) {
                item.setAmount(0);
                player.sendMessage(colorText("&dYour axe broke!"));
                continueTreeCapping = false;
            } else {
                item.setDurability((short) newDurability);
            }
        }
    }

    @Override
    void resetAfterAbilityFinished() {

    }

    @Override
    void processActionDuringAbility() {

    }
}
