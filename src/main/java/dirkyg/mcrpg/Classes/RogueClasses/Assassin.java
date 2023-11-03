package dirkyg.mcrpg.Classes.RogueClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.InvisibleMovement;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Stun;
import dirkyg.mcrpg.SpecialAbilities.Swapper;

public class Assassin extends RPGClass implements Listener {

    private final Set<UUID> processedEntities = new HashSet<>();

    InvisibleMovement invisibleMovement;
    Stun stun;

    public Assassin(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Rogue(uuid);
        invisibleMovement = new InvisibleMovement(uuid);
        stun = new Stun(uuid, this.toString());
        swapper = new Swapper(uuid, this, new SpecialAbility[] { stun });
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        baseClass.activatePlayer();
        invisibleMovement.start();
        setCurrentlyActive(true);
    }

    @Override
    public void deactivatePlayer() {
        baseClass.deactivatePlayer();
        invisibleMovement.stop();
        setCurrentlyActive(false);
    }

    @Override
    public String toString() {
        return "Assassin";
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (player.getUniqueId() != uuid || !isCurrentlyActive() || currentItem == null
                || !isSword(currentItem.getType()) || player.isSneaking() || swapper.activeAbility.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            swapper.activeAbility.processAbility(event, player, "Right Click");
        }
    }

    @EventHandler
    public void increaseCrit(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Player player) {
            if (player.getUniqueId() != uuid || !isCurrentlyActive() || !(event.getEntity() instanceof LivingEntity le)
                    || processedEntities.contains(event.getEntity().getUniqueId())) {
                return;
            }
            if (player.getFallDistance() > 0.0F && !player.isOnGround() &&
                    !player.isInsideVehicle() && !player.isSprinting() &&
                    !player.isInWater() && !player.isSwimming() &&
                    !player.isClimbing() && !player.hasPotionEffect(PotionEffectType.BLINDNESS) &&
                    !player.getLocation().getBlock().isLiquid()) {
                event.setCancelled(true);
                processedEntities.add(le.getUniqueId());
                le.damage(event.getDamage() * 2, player);
                Bukkit.getScheduler().runTaskLater(McRPG.plugin, () -> {
                    processedEntities.remove(le.getUniqueId());
                }, 1L);
            }
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
    }
}
