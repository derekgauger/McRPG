package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.CropCircle;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Ageable;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class FarmingSkill extends Skill implements Listener {

    CropCircle cropCircle;

    public FarmingSkill(UUID uuid) {
        super.uuid = uuid;
        cropCircle = new CropCircle(uuid, this);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Farming";
    }


    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        if (cropCircle.isHappening()) {
            return;
        }
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (!Utils.isCrop(currentItemType)) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            cropCircle.processAbility(event, player, "Right Click");
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Player player = event.getPlayer();
        Block brokenBlock = event.getBlock();
        BlockData blockData = brokenBlock.getBlockData();
        if (Utils.isCrop(brokenBlock.getType()) && (blockData instanceof Ageable ageableCrop)) {
            if (ageableCrop.getAge() == ageableCrop.getMaximumAge()) {
                SkillManager.processSkillIncrement(player, this, 2);
            }
        }
    }

    private void upgradeCropTime(Player player) {
        int cropTimeUpgrade = 3;
        cropCircle.duration += cropTimeUpgrade;
        player.sendMessage(Utils.chat("&6Ability Upgraded | " + cropCircle + " | Duration (" + (cropCircle.duration - cropTimeUpgrade) + " -> " + cropCircle.duration + ") seconds"));
    }

    private void upgradeCropRadius(Player player) {
        int cropRadius = 1;
        cropCircle.radius += cropRadius;
        player.sendMessage(Utils.chat("&6Ability Upgraded | " + cropCircle + " | Radius (" + (cropCircle.radius - cropRadius) + " -> " + cropCircle.radius + ")"));
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        switch (getLevel()) {
            case 5, 15, 25, 35, 40, 45, 50 -> {
                upgradeCropTime(player);
            }
            case 10, 20, 30 -> {
                upgradeCropRadius(player);
            }
        }
    }
}