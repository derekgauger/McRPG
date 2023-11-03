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
        ItemStack boots = player.getInventory().getBoots();
        ItemStack leggings = player.getInventory().getLeggings();
        ItemStack chestplate = player.getInventory().getChestplate();
        ItemStack helmet = player.getInventory().getHelmet();
        boolean emptyHand = currentItem.getType() == null || currentItem.getType() == Material.AIR;
        boolean hasBoots = !(boots == null) && !(boots.getType() == Material.AIR);
        boolean hasLeggings = !(leggings == null) && !(leggings.getType() == Material.AIR);
        boolean hasChestplate = !(chestplate == null) && !(chestplate.getType() == Material.AIR);
        boolean hasHelmet = !(helmet == null) && !(helmet.getType() == Material.AIR);
        if (emptyHand && !hasBoots && !hasLeggings && !hasChestplate && !hasHelmet && level == 1) {
            player.setInvisible(true);
        } else if (emptyHand && !hasLeggings && !hasChestplate && !hasHelmet && level == 2) {
            player.setInvisible(true);
        } else if (emptyHand && !hasChestplate && !hasHelmet && level == 3) {
            player.setInvisible(true);
        } else if (emptyHand && !hasChestplate && level == 4) {
            player.setInvisible(true);
        } else if (emptyHand && level == 5) {
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
