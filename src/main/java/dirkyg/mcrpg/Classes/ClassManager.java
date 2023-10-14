package dirkyg.mcrpg.Classes;

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
import dirkyg.mcrpg.Classes.WizardClasses.Fire;
import dirkyg.mcrpg.Classes.WizardClasses.Ice;
import dirkyg.mcrpg.Classes.WizardClasses.Wizard;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
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

import java.util.HashMap;
import java.util.UUID;

import static dirkyg.mcrpg.Utils.createItem;

public class ClassManager implements Listener, CommandExecutor {

    private final Inventory classGUI = Bukkit.createInventory(null, 9, "Class Picker");
    public static HashMap<UUID, PlayerClasses> charactersClasses = new HashMap<>();
    public static HashMap<UUID, RPGClass> activeClasses = new HashMap<>();

    public ClassManager(McRPG plugin) {
        Bukkit.getServer().getPluginCommand("class").setExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
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
                    wizard.setSubClass(Fire.class);
                    wizard.activatePlayer();
                    activeClasses.put(player.getUniqueId(), wizard);
                    break;
                case ICE:
                    wizard.setSubClass(Ice.class);
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
        inv.setItem(0, createItem(Material.NETHERITE_AXE, Utils.chat("&6&lWarrior"), 1, null, null));
        inv.setItem(1, createItem(Material.FEATHER, Utils.chat("&b&lRogue"), 1, null, null));
        inv.setItem(2, createItem(Material.BOW, Utils.chat("&a&lRanger"), 1, null, null));
        inv.setItem(3, createItem(Material.GOLDEN_APPLE, Utils.chat("&d&lHealer"), 1, null, null));
        inv.setItem(4, createItem(Material.STICK, Utils.chat("&5&lWizard"), 1, null, null));
        inv.setItem(8, createItem(Material.BARRIER, Utils.chat("&c&lExit"), 1, null, null));
        return inv;
    }

    private Inventory getSubClassSelectionGUI(String className) {
        Inventory inv = Bukkit.createInventory(null, 9, "Select a Subclass for " + className);
        if (className.contains("Warrior")) {
            inv.setItem(0, createItem(Material.END_CRYSTAL, Utils.chat("Elemental"), 1, null, null));
            inv.setItem(1, createItem(Material.NETHERITE_SWORD, Utils.chat("Berserker"), 1, null, null));
        }
        if (className.contains("Rogue")) {
            inv.setItem(0, createItem(Material.IRON_SWORD, Utils.chat("Assassin"), 1, null, null));
            inv.setItem(1, createItem(Material.ENDER_PEARL, Utils.chat("Trickster"), 1, null, null));
        }
        if (className.contains("Ranger")) {
            inv.setItem(0, createItem(Material.SPYGLASS, Utils.chat("Sniper"), 1, null, null));
            inv.setItem(1, createItem(Material.BOW, Utils.chat("Hunter"), 1, null, null));
        }
        if (className.contains("Healer")) {
            inv.setItem(0, createItem(Material.ENCHANTED_GOLDEN_APPLE, Utils.chat("Cleric"), 1, null, null));
            inv.setItem(1, createItem(Material.CHORUS_FRUIT, Utils.chat("Necromancer"), 1, null, null));
        }
        if (className.contains("Wizard")) {
            inv.setItem(0, createItem(Material.BLAZE_POWDER, Utils.chat("Fire Wizard"), 1, null, null));
            inv.setItem(1, createItem(Material.ICE, Utils.chat("Ice Wizard"), 1, null, null));
        }
        inv.setItem(8, createItem(Material.ARROW, Utils.chat("&6Back one page"), 1, null, null));
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
//            player.sendMessage(Utils.chat(activateMsg));
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
