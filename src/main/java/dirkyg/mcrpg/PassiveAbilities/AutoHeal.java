package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;

public class AutoHeal extends PassiveAbility implements Listener {
    private final int HEAL_AMOUNT = 1;  //half a heart
    private final int HEAL_INTERVAL = 20 * 2; // 3 seconds

    public AutoHeal(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    public void startAutoHealing(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCurrentlyActive()) {
                    this.cancel();
                    return;
                }
                double max_health = player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue();
                if (player.getHealth() <  max_health) {
                    player.setHealth(Math.min(max_health, player.getHealth() + HEAL_AMOUNT));
                }
            }
        }.runTaskTimer(McRPG.plugin, 0, HEAL_INTERVAL);
    }
}
