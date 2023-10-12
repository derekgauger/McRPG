package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.Skill;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.UUID;

public class Dash extends Ability implements Listener {

    private long dashCoolDown = 2;
    private long nextAvailableUsage;

    public Dash(UUID uuid, Skill skill) {
        super.playerUUID = uuid;
        super.skill = skill;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Dash";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != playerUUID) {
            return;
        }
        if (!isHappening) {
            return;
        }
        ItemStack item = event.getItem();
        if (item == null) {
            return;
        }
        if (player.isSneaking()) {
            return;
        }
        if (System.currentTimeMillis() < nextAvailableUsage) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR && Utils.isSword(item.getType())) {
            dashPlayer(player);
            nextAvailableUsage = System.currentTimeMillis() + dashCoolDown * 1000L;
        }
    }

    private void dashPlayer(Player player) {
        Vector dir = player.getLocation().getDirection().normalize();
        double force = 2.0; // Adjust this value to change the dash force/speed
        dir.multiply(force);
        player.setVelocity(dir);
    }

    @Override
    void resetAfterAbilityFinished() {

    }

    @Override
    void processActionDuringAbility() {

    }
}
