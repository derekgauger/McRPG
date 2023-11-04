package dirkyg.mcrpg.SpecialAbilities;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;

public class Berserk extends SpecialAbility implements Listener {

    public int damageMultipler = 5;
    private final Set<UUID> processedEntities = new HashSet<>();
    EntityReveal entityReveal;

    public Berserk(UUID uuid, String classifier) {
        this.classifier = classifier;
        this.abilityName = this.toString();
        this.playerUUID = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Berserk";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        if (player != null) {
            player.setWalkSpeed(.2f);
            entityReveal.disableEntityGlow();
        }
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        if (player != null) {
            if (player.getWalkSpeed() != .4f) {
                player.setWalkSpeed(.4f);
            }
            if (!player.hasPotionEffect(PotionEffectType.ABSORPTION)) {
                player.addPotionEffect(new PotionEffect(PotionEffectType.ABSORPTION, (int) duration * 20, 2));
            }
            entityReveal.enableEntityGlow(player.getNearbyEntities(200, 200, 200));
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager.getUniqueId() != playerUUID || !(damager instanceof Player player) || !isHappening()
                || !(damaged instanceof LivingEntity le) || processedEntities.contains(damaged.getUniqueId())) {
            return;
        }
        processedEntities.add(damaged.getUniqueId());
        le.damage(event.getFinalDamage() * damageMultipler, player);
        processedEntities.remove(damaged.getUniqueId());
        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        Entity entity = event.getEntity();
        if (!(entity instanceof Player player) || player.getUniqueId() != playerUUID || !isHappening()) {
            return;
        }
        event.setCancelled(true);
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }
}
