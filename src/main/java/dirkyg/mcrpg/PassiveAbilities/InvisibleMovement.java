package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;

public class InvisibleMovement extends PassiveAbility implements Listener {

    public InvisibleMovement(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    public void processInvisible(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (currentItem.getType() == Material.AIR) {
            player.setInvisible(true);
        } else {
            player.setInvisible(false);
        }
    }

    
    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processInvisible(event);
    }
}
