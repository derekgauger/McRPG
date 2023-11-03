package dirkyg.mcrpg.SpecialAbilities;

import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.UUID;

public class ExcavationRun extends SpecialAbility implements Listener {

    public ExcavationRun(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
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
        if (player.getUniqueId() != playerUUID || !isHappening || player.isSneaking()) {
            return;
        }
        Location from = event.getFrom();
        Location to = event.getTo();
        if (to == null || (from.getBlockX() == to.getBlockX() && from.getBlockZ() == to.getBlockZ())) {
            return;
        }
        Block blockBelow = from.subtract(0, 1, 0).getBlock(); // Block that player was standing on
        if (blockBelow.getType() != Material.AIR) {
            blockBelow.breakNaturally(player.getInventory().getItemInMainHand());
        }
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
