package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.Abilities.Ability;
import dirkyg.mcrpg.Abilities.SuperSwim;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.*;

import java.util.UUID;

public class AgilitySkill extends Skill implements Listener {

    Ability superSwim;

    public AgilitySkill(UUID uuid) {
        super.uuid = uuid;
        superSwim = new SuperSwim(uuid, this);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Agility";
    }


    @EventHandler
    public void sneakEvent(PlayerToggleSneakEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        if (superSwim.isHappening()) {
            return;
        }
        if (player.isInWater() && event.isSneaking()) {
            superSwim.processAbility(event, player, "Sneak");
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        Location to = event.getTo();
        Location from = event.getFrom();
        if (to == null) {
            return;
        }
        if (to.getY() > from.getY() && !player.isFlying() && !player.isInWater()) {
            if (to.getY() - from.getY() > .4) {
                SkillManager.processSkillIncrement(player, this, .1);
            }
        }
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (player.getUniqueId() != uuid) {
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                SkillManager.processSkillIncrement(player, this, 2);
            }
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
                int superSwimTimeUpgrade = 5;
                superSwim.duration += superSwimTimeUpgrade;
                if (player == null) {
                    return;
                }
                player.sendMessage(Utils.chat("&6Ability Upgraded | " + superSwim + " | Duration (" + (superSwim.duration - superSwimTimeUpgrade) + " -> " + superSwim.duration + ") seconds"));
        }
    }
}
