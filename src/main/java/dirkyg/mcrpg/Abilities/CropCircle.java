package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.Skill;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import java.util.UUID;

public class CropCircle extends Ability implements Listener {

    public int radius = 0;

    public CropCircle(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Crop Circle";
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID || !isHappening || player.isSneaking()) {
            return;
        }
        Block block = event.getBlockPlaced();
        if (Utils.isCrop(block.getType())) {
            plantCropCircle(block, radius);
        }
    }

    private void plantCropCircle(Block center, int radius) {
        Material cropType = center.getType();
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                if (Math.pow(x, 2) + Math.pow(z, 2) <= Math.pow(radius, 2)) {
                    Block block = center.getRelative(x, -1, z);
                    if (block.getType() == Material.FARMLAND) {
                        block.getRelative(0, 1, 0).setType(cropType);
                    }
                }
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
