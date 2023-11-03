package dirkyg.mcrpg.Classes.WizardClasses;

import static dirkyg.mcrpg.Utilities.Common.addItemToInventory;
import static dirkyg.mcrpg.Utilities.Common.createGUIItem;
import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;
import static dirkyg.mcrpg.Utilities.Common.getTargetEntity;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;

public class Wizard extends RPGClass implements Listener {

    UUID uuid;
    private ItemStack wand;
    private int backfirePercentage = 15;
    private int maxSpellDistance = 120;
    private long spellCoolDownTime = 10000; // 10 seconds
    private long lastSpellUseTime = 0;
    float damageMultiplier = .5f;

    RPGClass activeClass;
    FireWizard fireWizard;
    IceWizard iceWizard;

    private final Set<UUID> processedEntities = new HashSet<>();

    public Wizard(UUID uuid) {
        this.uuid = uuid;
        fireWizard = new FireWizard(uuid);
        iceWizard = new IceWizard(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            wand = createGUIItem(Material.STICK, ("&b" + player.getName() + "'s Wand"), 1);
            addItemToInventory(wand, player);
            setCurrentlyActive(true);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            setCurrentlyActive(false);
        }
    }

    @Override
    public String toString() {
        return "Wizard";
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
    public void onEntityDamageEntity(EntityDamageByEntityEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        reduceDamage(event);
    }

    private void processLightningSpell(Player player) {
        Block tb = player.getTargetBlockExact(maxSpellDistance);
        Location strikeLocation = null;
        if (tb != null) {
            strikeLocation = tb.getLocation();
        } else {
            Entity entity = getTargetEntity(player, maxSpellDistance);
            if (entity != null) {
                strikeLocation = entity.getLocation();
            }
        }
        if (strikeLocation == null) {
            player.sendMessage(colorText("&cThe target you have chosen for the spell is too far away!"));
        } else {
            if (backfirePercentage <= getRandomNumber(0, 100)) {
                strikeLocation.getWorld().spawnEntity(strikeLocation, EntityType.LIGHTNING);
            } else {
                player.getWorld().spawnEntity(player.getLocation(), EntityType.LIGHTNING);
                player.sendMessage(colorText("&cYour lightning spell backfired!"));
            }
        }
    }

    private void processFireballSpell(Player player) {
        if (backfirePercentage <= getRandomNumber(0, 100)) {
            player.launchProjectile(Fireball.class);
        } else {
            player.getWorld().createExplosion(player.getLocation(), 1.3f, true);
            player.sendMessage(colorText("&cYour fireball spell backfired!"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();
        if (item.isSimilar(wand)) {
            long elapsedTimeSinceLastSpell = System.currentTimeMillis() - lastSpellUseTime;
            if (elapsedTimeSinceLastSpell < spellCoolDownTime) {
                player.sendMessage(colorText("&cYou have to wait " + ((spellCoolDownTime - elapsedTimeSinceLastSpell) / 1000) + " more seconds to use a spell!"));
                return;
            }
            lastSpellUseTime = System.currentTimeMillis();
            switch (event.getAction()) {
                case LEFT_CLICK_AIR, LEFT_CLICK_BLOCK -> {
                    processFireballSpell(player);
                }
                case RIGHT_CLICK_AIR, RIGHT_CLICK_BLOCK -> {
                    processLightningSpell(player);
                }
            }
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
