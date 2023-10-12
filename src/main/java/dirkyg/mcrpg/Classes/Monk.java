package dirkyg.mcrpg.Classes;

import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public class Monk extends RPGClass implements Listener {

    UUID uuid;
    private final int HEAL_AMOUNT = 1;  //half a heart
    private final int HEAL_INTERVAL = 20 * 2; // 3 seconds

    public Monk (UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setMaxHealth(14.0);
            autoHeal(player);
        }
        setCurrentlyActive(true);

    }

    @Override
    void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setMaxHealth(20.0);
        }
        setCurrentlyActive(false);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        if (event.getEntity() instanceof Player player) {
            if (player.getUniqueId() != uuid) {
                return;
            }
            if (event.getCause() == EntityDamageEvent.DamageCause.FALL) {
                event.setCancelled(true);
            }
        }
    }

    private void autoHeal(Player player) {
        new BukkitRunnable() {
            @Override
            public void run() {
                if (!isCurrentlyActive()) {
                    this.cancel();
                    return;
                }
                if (player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(Math.min(player.getMaxHealth(), player.getHealth() + HEAL_AMOUNT));
                }
            }
        }.runTaskTimer(McRPG.plugin, 0, HEAL_INTERVAL);
    }
}
