package dirkyg.mcrpg.Classes.RangerClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.SpecialAbilities.EntityReveal;
import dirkyg.mcrpg.SpecialAbilities.FiringRing;
import dirkyg.mcrpg.SpecialAbilities.HitScan;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Swapper;

public class Sniper extends RPGClass implements Listener {

    EntityReveal entityReveal;
    HitScan hitScan;
    FiringRing firingRing;

    public Sniper(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Ranger(uuid);
        entityReveal = new EntityReveal(uuid, this.toString());
        hitScan = new HitScan(uuid, this.toString());
        firingRing = new FiringRing(uuid, this.toString());
        swapper = new Swapper(uuid, this, new SpecialAbility[] { entityReveal, hitScan, firingRing });
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        baseClass.activatePlayer();
        setCurrentlyActive(true);

    }

    @Override
    public void deactivatePlayer() {
        baseClass.deactivatePlayer();
        setCurrentlyActive(false);
    }

    @Override
    public String toString() {
        return "Sniper";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (player.getUniqueId() != uuid || !isCurrentlyActive() || currentItem == null
                || !isSword(currentItem.getType()) || player.isSneaking() || swapper.activeAbility.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            swapper.activeAbility.processAbility(event, player, "Right Click");
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
