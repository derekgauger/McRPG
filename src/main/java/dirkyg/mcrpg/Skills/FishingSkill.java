package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.Common.createItem;
import static dirkyg.mcrpg.Utilities.Common.getRandomNumber;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;
import static dirkyg.mcrpg.Utilities.Visuals.sendActionBar;

import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;


public class FishingSkill extends Skill implements Listener {

    private int percentChangeCustomDrop = 1;

    public FishingSkill(UUID uuid) {
        super.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Fishing";
    }

    private HashMap<ItemStack, String> initializeItemsAndMsgs() {
        HashMap<ItemStack, String> items = new HashMap<>();
        items.put(createItem(Material.GOLDEN_APPLE, "", 1), "&dYou found a golden apple from fishing!");
        items.put(createItem(Material.DIAMOND, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.EMERALD, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.IRON_INGOT, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.GOLD_INGOT, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.COPPER_INGOT, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.COAL, "", 1), "&dYou found a diamond from fishing!");
        items.put(createItem(Material.NAME_TAG, "", 1), "&dYou found a name tag from fishing!");
        items.put(createItem(Material.SALMON, "", 2), "&dYou got two salmon from one cast!");
        items.put(createItem(Material.COD, "", 2), "&dYou got two cod from one cast!");
        items.put(createItem(Material.PUFFERFISH, "", 2), "&dYou got two pufferfish from one cast!");
        for (Material m : Material.values()) {
            if (m.toString().contains("MUSIC_DISC")) {
                items.put(createItem(m, "", 1), "&dYou found a record!");
            }
        }
        items.put(createItem(Material.PANDA_SPAWN_EGG, "", 1), "&dYou found a panda spawn egg from fishing... Somehow...");
        items.put(createItem(Material.TURTLE_EGG, "", 1), "&dYou found a turtle egg from fishing... Somehow...");
        items.put(createItem(Material.PARROT_SPAWN_EGG, "", 1), "&dYou found a parrot spawn egg from fishing... Somehow...");
        return items;
    }

    @EventHandler
    public void onPlayerFish(PlayerFishEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid) {
            return;
        }
        if (event.getState() == PlayerFishEvent.State.CAUGHT_FISH) {
            if (event.getCaught() instanceof Item) {
                int randomNum = getRandomNumber(0, 100);
                if (randomNum < percentChangeCustomDrop) {
                    HashMap<ItemStack, String> itemMsgs = initializeItemsAndMsgs();
                    Random rand = new Random();
                    ItemStack item = (ItemStack) itemMsgs.keySet().toArray()[rand.nextInt(itemMsgs.size())];
                    Vector direction = event.getPlayer().getLocation().add(0, 1.5, 0).toVector().subtract(event.getHook().getLocation().toVector()).normalize();
                    double distance = event.getHook().getLocation().distance(event.getPlayer().getLocation());
                    direction.multiply(distance * 0.125);
                    event.getHook().getWorld().dropItem(event.getHook().getLocation(), item).setVelocity(direction);
                    event.getCaught().remove();
                    String msg = itemMsgs.get(item);
                    sendActionBar(player, msg);
                }
                SkillManager.processSkillIncrement(player, this, SkillManager.fishingXpMultipler);
            }
        }
    }

    private void upgradeFishingPercentage(Player player) {
        int fishingPercentageUpgrade = 2;
        percentChangeCustomDrop += fishingPercentageUpgrade;
        player.sendMessage(colorText("&6Ability Upgraded | Building Jump Boost | Percentage (" + (percentChangeCustomDrop - fishingPercentageUpgrade) + " -> " + percentChangeCustomDrop + ") %"));
    }

    @Override
    public void processAbilityUpgrade() {
        Player player = Bukkit.getPlayer(uuid);
        if (player == null) {
            return;
        }
        switch (getLevel()) {
            case 5:
            case 10:
            case 15:
            case 20:
            case 25:
            case 30:
            case 35:
            case 40:
            case 45:
            case 50:
                upgradeFishingPercentage(player);
        }
    }
}
