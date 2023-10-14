package dirkyg.mcrpg.Classes;

import static dirkyg.mcrpg.Utilities.Common.createGUIItem;

import java.util.HashMap;
import java.util.UUID;

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
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.HealerClasses.Cleric;
import dirkyg.mcrpg.Classes.HealerClasses.Healer;
import dirkyg.mcrpg.Classes.HealerClasses.Necromancer;
import dirkyg.mcrpg.Classes.RangerClasses.Hunter;
import dirkyg.mcrpg.Classes.RangerClasses.Ranger;
import dirkyg.mcrpg.Classes.RangerClasses.Sniper;
import dirkyg.mcrpg.Classes.RogueClasses.Assassin;
import dirkyg.mcrpg.Classes.RogueClasses.Rogue;
import dirkyg.mcrpg.Classes.RogueClasses.Trickster;
import dirkyg.mcrpg.Classes.WarriorClasses.Berserker;
import dirkyg.mcrpg.Classes.WarriorClasses.Elemental;
import dirkyg.mcrpg.Classes.WarriorClasses.Warrior;
import dirkyg.mcrpg.Classes.WizardClasses.FireWizard;
import dirkyg.mcrpg.Classes.WizardClasses.IceWizard;
import dirkyg.mcrpg.Classes.WizardClasses.Wizard;

public class ClassManager implements Listener, CommandExecutor {

    private final Inventory classGUI = Bukkit.createInventory(null, 9, "Class Picker");
    public static HashMap<UUID, PlayerClasses> charactersClasses = new HashMap<>();
    public static HashMap<UUID, RPGClass> activeClasses = new HashMap<>();

    public ClassManager() {
        Bukkit.getServer().getPluginCommand("class").setExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            System.out.println("Only players in game can do that!");
            return false;
        }
        if (label.equalsIgnoreCase("class")) {
            player.openInventory(getClassSelectionGUI());
        }

        return true;
    }

    private String getClassStatusString(boolean isActive) {
        if (isActive) {
            return "&aActive";
        } else {
            return "&4Inactive";
        }
    }


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        HumanEntity he = event.getWhoClicked();
        if (!(he instanceof Player player)) {
            return;
        }
        if (event.getView().getTitle().equalsIgnoreCase("Select a Class")) {
            event.setCancelled(true);  // Cancel the event so items can't be taken
            switch (event.getCurrentItem().getType()) {
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
                    break;
            }
        } else if (event.getView().getTitle().startsWith("Select a Subclass for ")) {
            event.setCancelled(true);
            RPGClass activeClass = activeClasses.get(player.getUniqueId());
            RPGClass warrior = getPlayerClassByType(player, Warrior.class);
            RPGClass rogue = getPlayerClassByType(player, Rogue.class);
            RPGClass ranger = getPlayerClassByType(player, Ranger.class);
            RPGClass healer = getPlayerClassByType(player, Healer.class);
            RPGClass wizard = getPlayerClassByType(player, Wizard.class);
            if (event.getCurrentItem().getType() != Material.ARROW && event.getCurrentItem().getType() != Material.AIR) {
                if (activeClass != null) {
                    activeClass.deactivatePlayer();
                }
                player.closeInventory();
            }
            switch (event.getCurrentItem().getType()) {
                case END_CRYSTAL:
                    warrior.setSubClass(Elemental.class);
                    warrior.activatePlayer();
                    activeClasses.put(player.getUniqueId(), warrior);
                    break;
                case NETHERITE_SWORD:
                    warrior.setSubClass(Berserker.class);
                    warrior.activatePlayer();
                    activeClasses.put(player.getUniqueId(), warrior);
                    break;
                case IRON_SWORD:
                    rogue.setSubClass(Assassin.class);
                    rogue.activatePlayer();
                    activeClasses.put(player.getUniqueId(), rogue);
                    break;
                case ENDER_PEARL:
                    rogue.setSubClass(Trickster.class);
                    rogue.activatePlayer();
                    activeClasses.put(player.getUniqueId(), rogue);
                    break;
                case SPYGLASS:
                    ranger.setSubClass(Sniper.class);
                    ranger.activatePlayer();
                    activeClasses.put(player.getUniqueId(), ranger);
                    break;
                case BOW:
                    ranger.setSubClass(Hunter.class);
                    ranger.activatePlayer();
                    activeClasses.put(player.getUniqueId(), ranger);
                    break;
                case ENCHANTED_GOLDEN_APPLE:
                    healer.setSubClass(Cleric.class);
                    healer.activatePlayer();
                    activeClasses.put(player.getUniqueId(), healer);
                    break;
                case CHORUS_FRUIT:
                    healer.setSubClass(Necromancer.class);
                    healer.activatePlayer();
                    activeClasses.put(player.getUniqueId(), healer);
                    break;
                case BLAZE_POWDER:
                    wizard.setSubClass(FireWizard.class);
                    wizard.activatePlayer();
                    activeClasses.put(player.getUniqueId(), wizard);
                    break;
                case ICE:
                    wizard.setSubClass(IceWizard.class);
                    wizard.activatePlayer();
                    activeClasses.put(player.getUniqueId(), wizard);
                    break;
                case ARROW:
                    event.getWhoClicked().openInventory(getClassSelectionGUI());
                    break;
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

    public RPGClass getPlayerClassByType(Player player, Class classType) {
        UUID uuid = player.getUniqueId();
        if (!charactersClasses.containsKey(uuid)) {
            charactersClasses.put(uuid, new PlayerClasses(uuid));
        }
        PlayerClasses pc = charactersClasses.get(player.getUniqueId());
        if (classType.equals(Warrior.class)) {
            return pc.getWarrior();
        } else if (classType.equals(Rogue.class)) {
            return pc.getRogue();
        } else if (classType.equals(Ranger.class)) {
            return pc.getRanger();
        } else if (classType.equals(Healer.class)) {
            return pc.getHealer();
        } else if (classType.equals(Wizard.class)) {
            return pc.getWizard();
        }
        return null;
    }


//    @EventHandler
//    public void onInventoryClick(InventoryClickEvent event) {
//        event.setCancelled(true);
//        ItemStack clickedItem = event.getCurrentItem();
//        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
//            return;
//        }
//        Player player = (Player) event.getWhoClicked();
//        UUID playerUUID = player.getUniqueId();
//        ItemMeta itemMeta = clickedItem.getItemMeta();
//        if (itemMeta == null) {
//            return;
//        }
//        String itemName = itemMeta.getDisplayName();
//        RPGClass newlyActivated = null;
//        String activateMsg = "";
//        if (itemName.contains("Warrior")) {
//            newlyActivated = getPlayerClassByType(player, Warrior.class);
//            activateMsg = "&6The warrior class is now activated.";
//        } else if (itemName.contains("Rogue")) {
//            newlyActivated = getPlayerClassByType(player, new Rogue(null));
//            activateMsg = "&9The rogue class is now activated.";
//        } else if (itemName.contains("Monk")) {
//            newlyActivated = getPlayerClassByType(player, new Healer(null));
//            activateMsg = "&5The monk class is now activated.";
//        } else if (itemName.contains("Wizard")) {
//            newlyActivated = getPlayerClassByType(player, new Wizard(null));
//            activateMsg = "&2The wizard class is now activated.";
//        }
//        RPGClass currentClass = activeClasses.get(playerUUID);
//
//        if (currentClass != null) {
//            currentClass.deactivatePlayer();
//        }
//        if (newlyActivated != null) {
//            newlyActivated.activatePlayer();
//            activeClasses.put(playerUUID, newlyActivated);
//            player.sendMessage(activateMsg));
//        }
//        initializeClassGUI(player);
//    }

    @EventHandler
    public void onPlayerDeath(PlayerRespawnEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        if (!activeClasses.containsKey(uuid)) {
            return;
        }
        RPGClass currentClass = activeClasses.get(uuid);
        currentClass.activatePlayer();
    }
}
