package dirkyg.mcrpg.Skills;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.util.UUID;

public class CommerceSkill extends Skill implements Listener {

    private int percentChanceCashBack = 1;

    public CommerceSkill(UUID uuid) {
        super.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public String toString() {
        return "Commerce";
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity entity = event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        if (!(entity instanceof Player player) || player.getUniqueId() != uuid || inventory == null) {
            return;
        }
        ItemStack currentItem = event.getCurrentItem();
        if (currentItem == null || currentItem.getType() == Material.AIR) {
            return;
        }
        if (inventory instanceof MerchantInventory) {
            if (event.getRawSlot() == 2) {
                ItemStack firstTradeSlot = inventory.getContents()[0];
                ItemStack secondTradeSlot = inventory.getContents()[0];
                if (firstTradeSlot.getType() != Material.EMERALD && secondTradeSlot.getType() != Material.EMERALD) {
                    int randomNumber = Utils.getRandomNumber(0, 100);
                    if (randomNumber < percentChanceCashBack) {
                        player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.EMERALD));
                        Utils.sendActionBar(player, Utils.chat("&dYou got some cash back!"));
                    }
                }
                SkillManager.processSkillIncrement(player, this, 3);
            }
        }
    }

    private void upgradeCashBackPercentage(Player player) {
        int cashBackPercentageUpgrade = 2;
        percentChanceCashBack += cashBackPercentageUpgrade;
        player.sendMessage(Utils.chat("&6Ability Upgraded | Commerce Cash Back | Percentage (" + (percentChanceCashBack - cashBackPercentageUpgrade) + " -> " + percentChanceCashBack + ") %"));
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
                upgradeCashBackPercentage(player);
        }
    }
}
