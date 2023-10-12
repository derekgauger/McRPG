package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.Dash;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class StrengthSkill extends Skill implements Listener {

    Dash dash;

    public StrengthSkill(UUID uuid) {
        super.uuid = uuid;
        dash = new Dash(uuid, this);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Strength";
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (!(event.getDamager() instanceof Player player)) {
            return;
        }
        if (player.getUniqueId() != uuid) {
            return;
        }
        SkillManager.processSkillIncrement(player, this, 2);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (item == null || !Utils.isSword(item.getType())) {
            return;
        }
        if (!player.isSneaking()) {
            return;
        }
        if (dash.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            dash.processAbility(event, player, "Right Click");
        }
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
        switch (getLevel()) {
            case 5:
            case 10:
            case 15:
            case 20:
            case 25:
            case 30:
            case 35:
            case 40:
            case 45:
            case 50:
                int dashTimeUpgrade = 5;
                dash.duration += dashTimeUpgrade;
                if (player == null) {
                    return;
                }
                player.sendMessage(Utils.chat("&6Ability Upgraded | " + dash + " | Duration (" + (dash.duration - dashTimeUpgrade) + " -> " + dash.duration + ") seconds"));
        }
    }
}
