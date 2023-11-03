package dirkyg.mcrpg.Classes.RogueClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isAxe;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.DoubleJump;

public class Rogue extends RPGClass implements Listener {

    UUID uuid;
    float damageMultiplier = .75f;
    float baseSpeed = .35f;
    float heavyWeaponSpeed = .15f;

    RPGClass activeClass;

    DoubleJump doubleJump;

    private final Set<UUID> processedEntities = new HashSet<>();

    public Rogue(UUID uuid) {
        this.uuid = uuid;
        doubleJump = new DoubleJump(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        System.out.println(1);
        if (player != null) {
            player.setWalkSpeed(baseSpeed);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.WATER_BREATHING, PotionEffect.INFINITE_DURATION, 1));
            doubleJump.start();
            setCurrentlyActive(true);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            player.setInvisible(false);
            player.removePotionEffect(PotionEffectType.WATER_BREATHING);
            doubleJump.stop();
            setCurrentlyActive(false);
        }
    }

    @Override
    public String toString() {
        return "Rogue";
    }

    public void reduceDamage(EntityDamageByEntityEvent event) {
        Player player = Bukkit.getPlayer(uuid);
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!(entity instanceof LivingEntity le) || processedEntities.contains(entity.getUniqueId())) {
            return;
        }
        if (damager instanceof Arrow arrow) {
            damager = (Entity) arrow.getShooter();
        } else if (damager instanceof Trident trident) {
            damager = (Entity) trident.getShooter();
        }
        if (damager == player) {
            processedEntities.add(le.getUniqueId());
            double originalDamage = event.getFinalDamage();
            double modifiedDamage = originalDamage * damageMultiplier;
            le.damage(modifiedDamage, player);
            processedEntities.remove(le.getUniqueId());
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onEntityDamageByEntityEvent(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        reduceDamage(event);
    }

    public void processHeavyWeaponsOut(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        if (isSword(currentItem.getType()) || isAxe(currentItem.getType())) {
            player.setWalkSpeed(heavyWeaponSpeed);
        } else {
            player.setWalkSpeed(baseSpeed);
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        if (player.hasPotionEffect(PotionEffectType.WATER_BREATHING)) {
            player.addPotionEffect(
                    new PotionEffect(PotionEffectType.WATER_BREATHING, PotionEffect.INFINITE_DURATION, 1));

        }
        processHeavyWeaponsOut(event);
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
