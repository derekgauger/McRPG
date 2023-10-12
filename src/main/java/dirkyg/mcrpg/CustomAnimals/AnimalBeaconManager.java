package dirkyg.mcrpg.CustomAnimals;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AnimalBeaconManager {

    List<AnimalBeacon> animalBeacons = new ArrayList<>();
    private final int duration = 5 * 60 * 1000;

    public AnimalBeaconManager() {}

    public void start() {
        new BukkitRunnable() {
            @Override
            public void run() {
                Collection<? extends Player> onlinePlayers = Bukkit.getOnlinePlayers();
                for (Player player : onlinePlayers) {
                    List<Entity> entities = player.getNearbyEntities(20, 20, 20);
                    for (Entity entity : entities) {
                        for (AnimalBeacon animalBeacon : animalBeacons) {
                            if (entity.getUniqueId() == animalBeacon.entityUUID) {
                                long currentTime = System.currentTimeMillis();
                                player.addPotionEffect(animalBeacon.potionEffect);
                                entity.setCustomNameVisible(true);
                                entity.setCustomName(animalBeacon.potionEffect.getType().getName() + " " + entity.getType().toString());
                                if (currentTime >= animalBeacon.creationTime + duration) {
                                    if (entity instanceof LivingEntity livingEntity) {
                                        if (livingEntity.getHealth() != 0) {
                                            livingEntity.setHealth(0);
                                            animalBeacons.remove(animalBeacon);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }.runTaskTimer(McRPG.plugin, 0L, 20L);
    }

    public void createNewAnimalBeacon(Entity entity, List<PotionEffect> availablePotionEffects) {
        int randomNumber = Utils.getRandomNumber(0, availablePotionEffects.size());
        PotionEffect randomPotionEffect = availablePotionEffects.get(randomNumber);
        AnimalBeacon animalBeacon = new AnimalBeacon(entity.getUniqueId(), randomPotionEffect);
        animalBeacons.add(animalBeacon);
    }
}
