package dirkyg.mcrpg.Classes.RogueClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;
import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.SpecialAbilities.SmokeBomb;
import dirkyg.mcrpg.SpecialAbilities.SpecialAbility;
import dirkyg.mcrpg.SpecialAbilities.Swapper;
import dirkyg.mcrpg.SpecialAbilities.TNTDash;

public class Trickster extends RPGClass implements Listener {

    int posionMeleePercentage = 100;
    long poisonMeleeDuration = 5;
    private final Set<UUID> processedEntities = new HashSet<>();

    SmokeBomb smokeBomb;
    TNTDash tntDash;

    public Trickster(UUID uuid) {
        this.uuid = uuid;
        baseClass = new Rogue(uuid);
        smokeBomb = new SmokeBomb(uuid, this.toString());
        tntDash = new TNTDash(uuid, this.toString());
        swapper = new Swapper(uuid, this, new SpecialAbility[] {smokeBomb, tntDash});
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
        return "Trickster";
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

    public void posionMelee(EntityDamageByEntityEvent event) {
        LivingEntity entity = (LivingEntity) event.getEntity();
        Player player = (Player) event.getDamager();
        processedEntities.add(entity.getUniqueId());
        event.setCancelled(true);
        entity.damage(event.getDamage(), player);
        DustOptions dustOptions = new DustOptions(Color.fromRGB(145, 13, 166), 1);
        new BukkitRunnable() {
            int iterations = 0;

            @Override
            public void run() {
                if (iterations == poisonMeleeDuration * 20 || entity.isDead()) {
                    processedEntities.remove(entity.getUniqueId());
                    this.cancel();
                    return;
                }
                if (iterations % 20 == 0) {
                    entity.damage(1, player);
                    entity.setVelocity(new Vector(0, 0, 0));
                }
                entity.getWorld().spawnParticle(Particle.REDSTONE, entity.getLocation(), 3, 1, 1.5, 1, 0.1,
                        dustOptions);
                iterations += 1;
            }
        }.runTaskTimer(McRPG.plugin, 0, 1);
    }

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        if (!isCurrentlyActive() || damager.getUniqueId() != uuid || !(damager instanceof Player player)
                || !(entity instanceof LivingEntity le) || processedEntities.contains(entity.getUniqueId())) {
            return;
        }
        if (getRandomNumber(0, 100) <= posionMeleePercentage) {
            posionMelee(event);
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
