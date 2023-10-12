package dirkyg.mcrpg.Classes;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Fireball;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

import java.util.UUID;

public class Wizard extends RPGClass implements Listener {

    UUID uuid;
    private ItemStack wand;
    private boolean wandGiven = false;
    private int backfirePercentage = 15;
    private int maxSpellDistance = 120;
    private long spellCoolDownTime = 10000; // 10 seconds
    private long lastSpellUseTime = 0;

    public Wizard (UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null && !wandGiven) {
            wand = Utils.createItem(Material.STICK, Utils.chat("&b" + player.getName() + "'s Wand"), 1, new Enchantment[] {Enchantment.MENDING}, new int[] {1});
            Utils.addItemToInventory(wand, player);
            wandGiven = true;
        }
        setCurrentlyActive(true);
    }

    @Override
    void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        setCurrentlyActive(false);
    }

    private void processLightningSpell(Player player) {
        Block tb = player.getTargetBlockExact(maxSpellDistance);
        Location strikeLocation = null;
        if (tb != null) {
            strikeLocation = tb.getLocation();
        } else {
            Entity entity = Utils.getTargetEntity(player, maxSpellDistance);
            if (entity != null) {
                strikeLocation = entity.getLocation();
            }
        }
        if (strikeLocation == null) {
            player.sendMessage(Utils.chat("&cThe target you have chosen for the spell is too far away!"));
        } else {
            if (backfirePercentage <= Utils.getRandomNumber(0, 100)) {
                strikeLocation.getWorld().spawnEntity(strikeLocation, EntityType.LIGHTNING);
            } else {
                player.getWorld().spawnEntity(player.getLocation(), EntityType.LIGHTNING);
                player.sendMessage(Utils.chat("&cYour lightning spell backfired!"));
            }
        }
    }

    private void processFireballSpell(Player player) {
        if (backfirePercentage <= Utils.getRandomNumber(0, 100)) {
            player.launchProjectile(Fireball.class);
        } else {
            player.getWorld().createExplosion(player.getLocation(), 1.3f, true);
            player.sendMessage(Utils.chat("&cYour fireball spell backfired!"));
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!isCurrentlyActive()) {
            return;
        }
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        ItemStack item = player.getInventory().getItemInMainHand();

        if (item.isSimilar(wand)) {
            long elapsedTimeSinceLastSpell = System.currentTimeMillis() - lastSpellUseTime;
            if (elapsedTimeSinceLastSpell < spellCoolDownTime) {
                player.sendMessage(Utils.chat("&cYou have to wait " + ((spellCoolDownTime - elapsedTimeSinceLastSpell) / 1000) + " more seconds to use a spell!"));
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
}
