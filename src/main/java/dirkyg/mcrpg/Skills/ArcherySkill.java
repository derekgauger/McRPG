package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.HomingArrows;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class ArcherySkill extends Skill implements Listener {

    HomingArrows homingArrows;

    public ArcherySkill(UUID uuid) {
        super.uuid = uuid;
        homingArrows = new HomingArrows(uuid, this);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Archery";
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
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Material currentItemType = currentItem.getType();
        if (currentItemType != Material.BOW && currentItemType != Material.CROSSBOW) {
            return;
        }
        if (homingArrows.isHappening()) {
            return;
        }
        if (event.getAction() == Action.LEFT_CLICK_AIR) {
            homingArrows.processAbility(event, player, "Left Click");
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Arrow arrow) {

            if (arrow.getShooter() instanceof Player player) {
                if (player.getUniqueId() != uuid) {
                    return;
                }
                SkillManager.processSkillIncrement(player, this, 3);
            }
        }
    }

    private void upgradeHomingArrowTime(Player player) {
        int homingArrowsTimeUpgrade = 5;
        homingArrows.duration += homingArrowsTimeUpgrade;
        player.sendMessage(Utils.chat("&6Ability Upgraded | " + homingArrows + " | Duration (" + (homingArrows.duration - homingArrowsTimeUpgrade) + " -> " + homingArrows.duration + ") seconds"));
    }

    private void upgradeHomingArrowSpeed(Player player) {
        float homingArrowsSpeedUpgrade = .5f;
        homingArrows.arrowSpeed += homingArrowsSpeedUpgrade;
        player.sendMessage(Utils.chat("&6Ability Upgraded | " + homingArrows + " | Speed (" + (homingArrows.arrowSpeed - homingArrowsSpeedUpgrade) + " -> " + homingArrows.arrowSpeed + ")"));
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
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
                upgradeHomingArrowTime(player);
                upgradeHomingArrowSpeed(player);
        }
    }
}
