package dirkyg.mcrpg.PassiveAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.McRPG;

public class InvisibleStanding extends PassiveAbility implements Listener {

    private long timeUntilInvisible = 20 * 2; // ticks
    private long lastMovementTime;
    private boolean isRunning = false;

    public InvisibleStanding(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        Location mainLocation = player.getLocation();
        if (isRunning) {
            return;
        }
        isRunning = true;
        player.setInvisible(false);
        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (!isSameBlockLocation(mainLocation, player.getLocation())) {
                    this.cancel();
                    isRunning = false;
                    player.setInvisible(false);
                    return;
                }
                if (ticks >= timeUntilInvisible) {
                    player.setInvisible(true);
                } else {
                    player.setInvisible(false);
                }
                ticks++;
            }
        }.runTaskTimer(McRPG.plugin, 0, 1L);
    }

    private boolean isSameBlockLocation(Location from, Location to) {
        return from.getBlockX() == to.getBlockX()
                && from.getBlockY() == to.getBlockY()
                && from.getBlockZ() == to.getBlockZ();
    }
}
