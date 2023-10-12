package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.Skill;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class ExcavationRun extends Ability implements Listener {

    public ExcavationRun(UUID uuid, Skill skill) {
        super.playerUUID = uuid;
        super.skill = skill;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Excavation Run";
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID) {
            return;
        }
        if (!isHappening) {
            return;
        }
        if (player.isSneaking()) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null) {
            return;
        }
        if (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ()) {
            return;
        }
        Block blockBelow = from.subtract(0, 1, 0).getBlock();  // Block that player was standing on
        if (blockBelow.getType() != Material.AIR) {
            blockBelow.breakNaturally(player.getInventory().getItemInMainHand());
        }
    }

    @Override
    void resetAfterAbilityFinished() {

    }

    @Override
    void processActionDuringAbility() {

    }
}
