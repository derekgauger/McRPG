package dirkyg.mcrpg.Classes.RangerClasses;

import static dirkyg.mcrpg.Utilities.BooleanChecks.isAxe;
import static dirkyg.mcrpg.Utilities.BooleanChecks.isSword;

import java.util.Arrays;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.block.Biome;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.PassiveAbilities.BowCharge;
import dirkyg.mcrpg.PassiveAbilities.Climb;

public class Ranger extends RPGClass implements Listener {

    float projectileDamageMultipler = 3.0f;
    float meleeDamageMultiplier = .75f;
    float baseSpeed = .275f;
    float archerBiomeSpeed = .325f;
    float heavyWeaponSpeed = .15f;
    Biome[] archerBiomes = new Biome[] {Biome.FOREST, Biome.FLOWER_FOREST, Biome.BIRCH_FOREST, Biome.DARK_FOREST, Biome.OLD_GROWTH_BIRCH_FOREST,
                                        Biome.TAIGA, Biome.OLD_GROWTH_PINE_TAIGA, Biome.OLD_GROWTH_SPRUCE_TAIGA, Biome.SNOWY_TAIGA, Biome.JUNGLE,
                                        Biome.BAMBOO_JUNGLE, Biome.SPARSE_JUNGLE};

    Climb climb;
    BowCharge bowCharge;

    public Ranger(UUID uuid) {
        this.uuid = uuid;
        climb = new Climb(uuid);
        bowCharge = new BowCharge(uuid);
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(baseSpeed);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(14);
            climb.start();
            setCurrentlyActive(true);
            bowCharge.start();
            player.setInvisible(false);
        }
    }

    @Override
    public void deactivatePlayer() {
        Player player = Bukkit.getPlayer(uuid);
        if (player != null) {
            player.setWalkSpeed(.2f);
            player.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(20.0);
            climb.stop();
            bowCharge.stop();
            setCurrentlyActive(false);
            player.setInvisible(false);
        }
    }

    @Override
    public String toString() {
        return "Ranger";
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        if (!isCurrentlyActive() || player.getUniqueId() != uuid) {
            return;
        }
        processSpeedChanges(event);
    }

    public void processSpeedChanges(PlayerMoveEvent event) {
        Player player = event.getPlayer();
        ItemStack currentItem = player.getInventory().getItemInMainHand();
        Biome currentBiome = player.getLocation().getBlock().getBiome();
        if (isSword(currentItem.getType()) || isAxe(currentItem.getType())) {
            player.setWalkSpeed(heavyWeaponSpeed);
        } else {
            if (Arrays.asList(archerBiomes).contains(currentBiome)) {
                player.setWalkSpeed(archerBiomeSpeed);
            } else {
                player.setWalkSpeed(baseSpeed);
            }
        }
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
