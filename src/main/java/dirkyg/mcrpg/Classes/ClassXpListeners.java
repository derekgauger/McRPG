package dirkyg.mcrpg.Classes;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utilities.EntityPoints;

public class ClassXpListeners implements Listener {
    
    public ClassXpListeners() {
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        Entity killed = event.getEntity();
        Entity killer = event.getEntity().getKiller();
        if (killer instanceof Player player) {
            UUID uuid = player.getUniqueId();
            if (ClassManager.playerClasses.containsKey(uuid) && EntityPoints.contains(killed.getType().toString())) {
                double points = EntityPoints.getEnumpoints(killed.getType().toString());
                PlayerClasses pc = ClassManager.playerClasses.get(uuid);
                ClassManager.processClassIncrement(player, pc.activeClass, points);
            }
        }
    }

}
