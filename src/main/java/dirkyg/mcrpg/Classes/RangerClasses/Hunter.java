package dirkyg.mcrpg.Classes.RangerClasses;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityTameEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;

public class Hunter extends RPGClass implements Listener {

    private double petDamageMultipler = 2.0;
    private final Set<UUID> processedEntities = new HashSet<>();
    private List<UUID> pets = new ArrayList<>();

    public Hunter(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Ranger(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @EventHandler
    public void onPetTame(EntityTameEvent event) {
        if (!isCurrentlyActive() || event.getOwner().getUniqueId() != uuid) {
            return;
        }
        LivingEntity pet = (LivingEntity) event.getEntity();
        pet.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, PotionEffect.INFINITE_DURATION, 1));
        pets.add(event.getEntity().getUniqueId());
        
    }

    @EventHandler
    public void onPetDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        if (!isCurrentlyActive() || !(damaged instanceof LivingEntity le) || processedEntities.contains(le.getUniqueId()) || !pets.contains(le.getUniqueId())) {
            return;
        }
        event.setCancelled(true);
        double originalDamage = event.getDamage();
        double modifiedDamage = originalDamage * petDamageMultipler;
        processedEntities.add(le.getUniqueId());
        le.damage(modifiedDamage);
        processedEntities.remove(le.getUniqueId());
    }

    @EventHandler
    public void onPetDie(EntityDeathEvent event) {
        Entity entity = event.getEntity();
        if (pets.contains(entity.getUniqueId())) {
            pets.remove(entity.getUniqueId());
        }
    }

    @Override
    public void activatePlayer() {
        baseClass.activatePlayer();
        setCurrentlyActive(true);
    }

    @Override
    public void deactivatePlayer() {
        baseClass.deactivatePlayer();
        setCurrentlyActive(false);
    }

    @Override
    public String toString() {
        return "Hunter";
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
    }
}
