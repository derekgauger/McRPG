package dirkyg.mcrpg.Classes.WarriorClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerVelocityEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.SpecialAbilities.Berserk;
import dirkyg.mcrpg.SpecialAbilities.Dash;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Swapper;

public class Berserker extends RPGClass implements Listener {

    float knockbackReductionMultiplier = .5f;
    float counterKnockbackMultipler = .9f;

    Dash dash;
    Berserk berserk;

    public Berserker(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Warrior(uuid, this);
        dash = new Dash(uuid, this.toString());
        berserk = new Berserk(uuid, this.toString());
        swapper = new Swapper(uuid, this, new SpecialAbility[] {dash, berserk});
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
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
        return "Berserker - " + baseClass.toString();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (player.getUniqueId() != uuid || !isCurrentlyActive() || currentItem == null || !isSword(currentItem.getType()) || player.isSneaking() || swapper.activeAbility.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            swapper.activeAbility.processAbility(event, player, "Right Click");
        }
    }

    public void processCounterKnockback(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damaged = event.getEntity();
        Entity damager = event.getDamager();
        if (damager instanceof LivingEntity && player == damaged) {
            Vector knockback = damager.getLocation().toVector().subtract(player.getLocation().toVector()).normalize();
            knockback.setY(0.5);
            knockback.multiply(counterKnockbackMultipler);
            damager.setVelocity(knockback);
        }
    }

    @EventHandler
    public void processKnockbackReduction(PlayerVelocityEvent event) {
        if (event.getPlayer().getUniqueId() != uuid || !isCurrentlyActive()) {
            return;
        }
        Vector knockback = event.getVelocity();
        knockback.multiply(knockbackReductionMultiplier);
        event.setVelocity(knockback);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isCurrentlyActive()) {
            processCounterKnockback(event);
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
