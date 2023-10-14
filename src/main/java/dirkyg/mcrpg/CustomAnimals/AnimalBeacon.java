package dirkyg.mcrpg.CustomAnimals;

import java.util.UUID;

import org.bukkit.potion.PotionEffect;

public class AnimalBeacon {

    PotionEffect potionEffect;
    UUID entityUUID;
    long creationTime;

    public AnimalBeacon(UUID uuid, PotionEffect potionEffect) {
        this.entityUUID = uuid;
        this.potionEffect = potionEffect;
        this.creationTime = System.currentTimeMillis();
    }
}
