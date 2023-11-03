package dirkyg.mcrpg.SpecialAbilities;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;

public class TNTDash extends SpecialAbility implements Listener {

    public float dashForce = 2.0f;
    public long dashCoolDown = 2L;
    long nextAvailableUsage;
    HashMap<UUID, Long> nextAvailableTNTDrops = new HashMap();
    Player player;

    public TNTDash(UUID uuid, String classifier) {
        super.playerUUID = uuid;
        super.classifier = classifier;
        super.abilityName = this.toString();
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "TNT Dash";
    }

    @Override
    void resetAfterAbilityFinished(Player player) {
        // TODO Auto-generated method stub
    }

    public void spawnTNT(Location location, int fuseTicks) {
        // Spawn the TNT
        TNTPrimed tnt = location.getWorld().spawn(location, TNTPrimed.class);
        // Set the fuse ticks
        tnt.setFuseTicks(fuseTicks);
        // Prevent the TNT from damaging blocks
        tnt.setIsIncendiary(false);
        tnt.setMetadata("DashedTNT", new FixedMetadataValue(McRPG.plugin, true));
        // tnt.setYield(0); // Set the explosion yield to 0 to not damage blocks
    }

    @EventHandler
    public void onExplosion(EntityExplodeEvent event) {
        if (!isHappening()) {
            return;
        }
        // Check if the exploding entity is a TNTPrimed
        if (event.getEntity() instanceof TNTPrimed) {
            // Clear the list of blocks to be exploded
            event.blockList().clear();
        }
    }

    @EventHandler
    public void stopExplosiveDamage(EntityDamageByEntityEvent event) {
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (!isHappening() || damaged.getUniqueId() != playerUUID || !(damager instanceof TNTPrimed tnt)) {
            return;
        }
        if (tnt.hasMetadata("DashedTNT")) {
            event.setCancelled(true);
        }
    }

    @Override
    void processActionDuringAbility(int iterations, Player player) {
        if (System.currentTimeMillis() < nextAvailableUsage) {
            if (player.getVelocity().length() >= 1.0f) {
                for (Entity entity : player.getNearbyEntities(1, 2, 1)) {
                    if (entity instanceof LivingEntity le && (!nextAvailableTNTDrops.containsKey(entity.getUniqueId()) || System.currentTimeMillis() >= nextAvailableTNTDrops.get(entity.getUniqueId()))) {
                        Location entityLocation = entity.getLocation();
                        spawnTNT(entityLocation, 20);
                        nextAvailableTNTDrops.put(entity.getUniqueId(), System.currentTimeMillis() + 1000L);
                    }
                }
            }
        }
    }

    @Override
    void initalizeAbility(Player player) {
        // TODO Auto-generated method stub
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();
        if (player.getUniqueId() != playerUUID || !isHappening || item == null || player.isSneaking()
                || System.currentTimeMillis() < nextAvailableUsage) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR && isSword(item.getType())) {
            dashPlayer(player);
            nextAvailableUsage = System.currentTimeMillis() + dashCoolDown * 1000L;
        }
    }

    private void dashPlayer(Player player) {
        Vector dir = player.getLocation().getDirection().normalize();
        dir.multiply(dashForce);
        player.setVelocity(dir);
    }

}
