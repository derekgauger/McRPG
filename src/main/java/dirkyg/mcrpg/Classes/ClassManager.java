package dirkyg.mcrpg.Classes;

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
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.UUID;

public class ClassManager implements Listener, CommandExecutor {

    private final Inventory classGUI = Bukkit.createInventory(null, 9, "Class Picker");
    public static HashMap<UUID, PlayerClasses> charactersClasses = new HashMap<>();
    public static HashMap<UUID, RPGClass> activeClasses = new HashMap<>();

    public ClassManager(McRPG plugin) {
        Bukkit.getServer().getPluginCommand("class").setExecutor(this);
//        Bukkit.getServer().getPluginCommand("init").setExecutor(this);
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            System.out.println("Only players in game can do that!");
            return false;
        }
        if (label.equalsIgnoreCase("class")) {
            openInventory(player);
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

    private void initializeClassGUI(HumanEntity entity) {
        classGUI.clear();
        int warriorLevel = 1;
        int rogueLevel = 1;
        int monkLevel = 1;
        int wizardLevel = 1;
        String warriorActiveString = "";
        String rogueActiveString = "";
        String monkActiveString = "";
        String wizardActiveString = "";
        UUID uuid = entity.getUniqueId();
        if (!(entity instanceof Player player)) {
            return;
        }
        if (!charactersClasses.containsKey(uuid)) {
            initializePlayerClasses(player);
        }
        PlayerClasses pc = charactersClasses.get(entity.getUniqueId());
        for (RPGClass rpgclass : pc.getAllClasses()) {
            int level = rpgclass.getLevel();
            String isActiveString = getClassStatusString(rpgclass.isCurrentlyActive());
            if (rpgclass instanceof Warrior) {
                warriorLevel = level;
                warriorActiveString = isActiveString;
            } else if (rpgclass instanceof Rogue) {
                rogueLevel = level;
                rogueActiveString = isActiveString;
            } else if (rpgclass instanceof Monk) {
                monkLevel = level;
                monkActiveString = isActiveString;
            } else if (rpgclass instanceof Wizard) {
                wizardLevel = level;
                wizardActiveString = isActiveString;
            }
        }
        classGUI.addItem(Utils.createGuiItem(Material.GOLDEN_CHESTPLATE, Utils.chat("&6&lWarrior"),
                Utils.chat(warriorActiveString),
                Utils.chat("&3Level: " + warriorLevel),
                Utils.chat("&aBase Abilities:"), Utils.chat("&a - More Damage"), Utils.chat("&a - More default health"),
                Utils.chat("&cBase Drawback:"), Utils.chat("&c - Slower movement")
        ));
        classGUI.addItem(Utils.createGuiItem(Material.FEATHER, Utils.chat("&9&lRogue"),
                Utils.chat(rogueActiveString),
                Utils.chat("&3Level: " + rogueLevel),
                Utils.chat("&aBase Abilities:"), Utils.chat("&a - Double jump"), Utils.chat("&a - Perm speed"),
                Utils.chat("&cBase Drawback:"), Utils.chat("&c - Does less damage")
        ));
        classGUI.addItem(Utils.createGuiItem(Material.GOLDEN_APPLE, Utils.chat("&5&lMonk"),
                Utils.chat(monkActiveString),
                Utils.chat("&3Level: " + monkLevel),
                Utils.chat("&aBase Abilities:"), Utils.chat("&a - Automatic healing"), Utils.chat("&a - No fall damage"),
                Utils.chat("&cBase Drawback:"), Utils.chat("&c - Less health")
        ));
        classGUI.addItem(Utils.createGuiItem(Material.FIRE_CHARGE, Utils.chat("&2&lWizard"),
                Utils.chat(wizardActiveString),
                Utils.chat("&3Level: " + wizardLevel),
                Utils.chat("&aBase Abilities:"), Utils.chat("&a - Fireball spell"), Utils.chat("&a - Smite"),
                Utils.chat("&cBase Drawbacks:"), Utils.chat("&c - Spells can backfire")
        ));
    }

    public void openInventory(HumanEntity entity) {
        initializeClassGUI(entity);
        entity.openInventory(classGUI);
    }

    private void initializePlayerClasses(Player player) {
        UUID uuid = player.getUniqueId();
        charactersClasses.put(uuid, new PlayerClasses(uuid));
    }

    public RPGClass getPlayerClassByType(Player player, RPGClass searchClass) {
        PlayerClasses pc = charactersClasses.get(player.getUniqueId());
        for (RPGClass rpgclass : pc.getAllClasses()) {
            if (rpgclass.getClass() == searchClass.getClass()) {
                return rpgclass;
            }
        }
        return null;
    }

//    @EventHandler
//    public void onPlayerJoin(PlayerJoinEvent event) {
//        Player player = event.getPlayer();
//        if (!player.hasPlayedBefore()) {
//
//        }
//        player.sendMessage(Utils.chat("&dYou can use '/class' to choose a set of skills"));
//    }

    @EventHandler
    public void onInventoryDrag(final InventoryDragEvent e) {
        if (e.getInventory() == classGUI) {
            e.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != classGUI) {
            return;
        }
        event.setCancelled(true);
        ItemStack clickedItem = event.getCurrentItem();
        if (clickedItem == null || clickedItem.getType() == Material.AIR) {
            return;
        }
        Player player = (Player) event.getWhoClicked();
        UUID playerUUID = player.getUniqueId();
        ItemMeta itemMeta = clickedItem.getItemMeta();
        if (itemMeta == null) {
            return;
        }
        String itemName = itemMeta.getDisplayName();
        RPGClass newlyActivated = null;
        String activateMsg = "";
        if (itemName.contains("Warrior")) {
            newlyActivated = getPlayerClassByType(player, new Warrior(null));
            activateMsg = "&6The warrior class is now activated.";
        } else if (itemName.contains("Rogue")) {
            newlyActivated = getPlayerClassByType(player, new Rogue(null));
            activateMsg = "&9The rogue class is now activated.";
        } else if (itemName.contains("Monk")) {
            newlyActivated = getPlayerClassByType(player, new Monk(null));
            activateMsg = "&5The monk class is now activated.";
        } else if (itemName.contains("Wizard")) {
            newlyActivated = getPlayerClassByType(player, new Wizard(null));
            activateMsg = "&2The wizard class is now activated.";
        }
        RPGClass currentClass = activeClasses.get(playerUUID);

        if (currentClass != null) {
            currentClass.deactivatePlayer();
        }
        if (newlyActivated != null) {
            newlyActivated.activatePlayer();
            activeClasses.put(playerUUID, newlyActivated);
            player.sendMessage(Utils.chat(activateMsg));
        }
        initializeClassGUI(player);
    }

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
