package dirkyg.mcrpg.Classes;

import static dirkyg.mcrpg.Utilities.Common.createGUIItem;
import static dirkyg.mcrpg.Utilities.Visuals.colorText;
import static dirkyg.mcrpg.Utilities.Visuals.launchLevelUpFirework;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.HealerClasses.Cleric;
import dirkyg.mcrpg.Classes.HealerClasses.Necromancer;
import dirkyg.mcrpg.Classes.RangerClasses.Hunter;
import dirkyg.mcrpg.Classes.RangerClasses.Sniper;
import dirkyg.mcrpg.Classes.RogueClasses.Assassin;
import dirkyg.mcrpg.Classes.RogueClasses.Trickster;
import dirkyg.mcrpg.Classes.WarriorClasses.Berserker;
import dirkyg.mcrpg.Classes.WarriorClasses.Elemental;
import dirkyg.mcrpg.Classes.WizardClasses.FireWizard;
import dirkyg.mcrpg.Classes.WizardClasses.IceWizard;

public class ClassManager implements Listener, CommandExecutor {

    public static HashMap<UUID, PlayerClasses> playerClasses = new HashMap<>();
    public static final int maxLevel = 5;
    public static int[] levelXPs = new int[] {5000, 12500, 25000, 40000};

    public ClassManager() {
        Bukkit.getServer().getPluginCommand("class").setExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            McRPG.LOGGER.log(Level.SEVERE, "Only players in game can do that!");
            return false;
        }
        if (label.equalsIgnoreCase("class")) {
            player.openInventory(getClassSelectionGUI());
        }

        return true;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (!(he instanceof Player player)) {
            return;
        }
        if (event.getView().getTitle().equalsIgnoreCase("Select a Class")) {
            event.setCancelled(true);  // Cancel the event so items can't be taken
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) {
                return;
            }
            Material type = currentItem.getType();
            switch (type) {
                case NETHERITE_AXE:
                    player.openInventory(getSubClassSelectionGUI("Warrior"));
                    break;
                case FEATHER:
                    player.openInventory(getSubClassSelectionGUI("Rogue"));
                    break;
                case BOW:
                    player.openInventory(getSubClassSelectionGUI("Ranger"));
                    break;
                case GOLDEN_APPLE:
                    player.openInventory(getSubClassSelectionGUI("Healer"));
                    break;
                case STICK:
                    player.openInventory(getSubClassSelectionGUI("Wizard"));
                    break;
                case BARRIER:
                    player.closeInventory();
                    return;
                default:
                    break;
            }
        } else if (event.getView().getTitle().startsWith("Select a Subclass for ")) {
            event.setCancelled(true);
            UUID uuid = player.getUniqueId();
            if (!playerClasses.containsKey(uuid)) {
                playerClasses.put(uuid, new PlayerClasses(uuid));
            }
            PlayerClasses pc = playerClasses.get(uuid);
            RPGClass activeClass = pc.activeClass;
            ItemStack currentItem = event.getCurrentItem();
            if (currentItem == null) {
                return;
            }
            Material type = currentItem.getType();
            if (type != Material.ARROW && type != Material.AIR) {
                if (activeClass != null) {
                    activeClass.deactivatePlayer();
                }
                player.closeInventory();
            }
            switch (type) {
                case END_CRYSTAL:
                    pc.activeClass = getPlayerClassByType(player, Elemental.class);
                    pc.activeClass.activatePlayer();
                    break;
                case NETHERITE_SWORD:
                    pc.activeClass = getPlayerClassByType(player, Berserker.class);
                    pc.activeClass.activatePlayer();
                    break;
                case IRON_SWORD:
                    pc.activeClass = getPlayerClassByType(player, Assassin.class);
                    pc.activeClass.activatePlayer();
                    break;
                case ENDER_PEARL:
                    pc.activeClass = getPlayerClassByType(player, Trickster.class);
                    pc.activeClass.activatePlayer();
                    break;
                case SPYGLASS:
                    pc.activeClass = getPlayerClassByType(player, Sniper.class);
                    pc.activeClass.activatePlayer();
                    break;
                case BOW:
                    pc.activeClass = getPlayerClassByType(player, Hunter.class);
                    pc.activeClass.activatePlayer();
                    break;
                case ENCHANTED_GOLDEN_APPLE:
                    pc.activeClass = getPlayerClassByType(player, Cleric.class);
                    pc.activeClass.activatePlayer();
                    break;
                case CHORUS_FRUIT:
                    pc.activeClass = getPlayerClassByType(player, Necromancer.class);
                    pc.activeClass.activatePlayer();
                    break;
                case BLAZE_POWDER:
                    pc.activeClass = getPlayerClassByType(player, FireWizard.class);
                    pc.activeClass.activatePlayer();
                    break;
                case ICE:
                    pc.activeClass = getPlayerClassByType(player, IceWizard.class);
                    pc.activeClass.activatePlayer();
                    break;
                case ARROW:
                    event.getWhoClicked().openInventory(getClassSelectionGUI());
                    return;
                default:
                    break;
            }
            if (!pc.activeClass.equals(activeClass)) {
                player.sendMessage(colorText("&dYou have selected the " + pc.activeClass + " class!"));
                McRPG.LOGGER.info(player.getName() + "|Activated Class: " + pc.activeClass);
            }
        }
    }

    private Inventory getClassSelectionGUI() {
        Inventory inv = Bukkit.createInventory(null, 9, "Select a Class");
        inv.setItem(0, createGUIItem(Material.NETHERITE_AXE, "&6&lWarrior", 1));
        inv.setItem(1, createGUIItem(Material.FEATHER, "&b&lRogue", 1));
        inv.setItem(2, createGUIItem(Material.BOW, "&a&lRanger", 1));
        inv.setItem(3, createGUIItem(Material.GOLDEN_APPLE, "&d&lHealer", 1));
        inv.setItem(4, createGUIItem(Material.STICK, "&5&lWizard", 1));
        inv.setItem(8, createGUIItem(Material.BARRIER, "&c&lExit", 1));
        return inv;
    }

    private Inventory getSubClassSelectionGUI(String className) {
        Inventory inv = Bukkit.createInventory(null, 9, "Select a Subclass for " + className);
        if (className.contains("Warrior")) {
            inv.setItem(0, createGUIItem(Material.END_CRYSTAL, "&6&lElemental", 1));
            inv.setItem(1, createGUIItem(Material.NETHERITE_SWORD, "&6&lBerserker", 1));
        }
        if (className.contains("Rogue")) {
            inv.setItem(0, createGUIItem(Material.IRON_SWORD, "&b&lAssassin", 1));
            inv.setItem(1, createGUIItem(Material.ENDER_PEARL, "&b&lTrickster", 1));
        }
        if (className.contains("Ranger")) {
            inv.setItem(0, createGUIItem(Material.SPYGLASS, "&a&lSniper", 1));
            inv.setItem(1, createGUIItem(Material.BOW, "&a&lHunter", 1));
        }
        if (className.contains("Healer")) {
            inv.setItem(0, createGUIItem(Material.ENCHANTED_GOLDEN_APPLE, "&d&lCleric", 1));
            inv.setItem(1, createGUIItem(Material.CHORUS_FRUIT, "&d&lNecromancer", 1));
        }
        if (className.contains("Wizard")) {
            inv.setItem(0, createGUIItem(Material.BLAZE_POWDER, "&5&lFire Wizard", 1));
            inv.setItem(1, createGUIItem(Material.ICE, "&5&lIce Wizard", 1));
        }
        inv.setItem(8, createGUIItem(Material.ARROW, "&6Back one page", 1));
        return inv;
    }

    public <T> RPGClass getPlayerClassByType(Player player, Class<T> classType) {
        UUID uuid = player.getUniqueId();
        if (!playerClasses.containsKey(uuid)) {
            playerClasses.put(uuid, new PlayerClasses(uuid));
        }
        PlayerClasses pc = playerClasses.get(player.getUniqueId());
        for (RPGClass c : pc.allClasses) {
            if (classType.equals(c.getClass())) {
                return c;
            }
        }
        return null;
    }

    private static int calculateLevel(RPGClass activeClass) {
        double xp = activeClass.getTotalXp();
        if (xp < levelXPs[0]) {
            return 0;
        } else if (xp >= levelXPs[levelXPs.length - 1]) {
            return maxLevel;
        }

        for (int i = 0; i < levelXPs.length; i++) {
            int levelXp = levelXPs[i];
            if (levelXp >= xp) {
                return i + 1;
            }
        }
        return -1;
    }

    private static boolean reachedNextLevel(RPGClass activeClass) {
        McRPG.LOGGER.info("lvl: " + activeClass.getLevel() + " | " + calculateLevel(activeClass));
        return (activeClass.getLevel() < calculateLevel(activeClass));
    }

    public static void processClassIncrement(Player player, RPGClass activeClass, double incrementAmount) {
        activeClass.addXp(incrementAmount);
        if (reachedNextLevel(activeClass)) {
            activeClass.incrementLevel();
            activeClass.processClassUpgrade(); 
            McRPG.LOGGER.log(Level.INFO, player.getName() + "|" + activeClass + "|Class Level: " + activeClass.getLevel());
            player.sendMessage(colorText("&dIncreased " + activeClass + "class to level " + activeClass.getLevel() + "!"));
            launchLevelUpFirework(player.getLocation());
        }
    }
}
