package dirkyg.mcrpg.Skills;

import static dirkyg.mcrpg.Utilities.Visuals.colorText;
import static dirkyg.mcrpg.Utilities.Visuals.launchLevelUpFirework;

import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.CustomAnimals.AnimalBeaconManager;

public class SkillManager implements Listener, CommandExecutor {

    public static HashMap<UUID, PlayerSkills> playerSkills = new HashMap<>();
    public static final double breedingXpMultipler = 3;
    public static final double buildingxpMultipler = 2;
    public static final double commereceXpMultipler = 5;
    public static final double diggingXpMultipler = 2;
    public static final double farmingXpMultipler = 2;
    public static final double fishingXpMultipler = 10;
    public static final double loggingXpMultipler = 3;
    public static final double miningXpMultipler = 1;

    public static int[] levelXPs = new int[] {3, 10, 25, 45, 70, // 0-5
                                            100, 140, 190, 250, 320, // 6-10
                                            400, 500, 620, 760, 920, // 11-15
                                            1100, 1315, 1565, 1850, 2170, // 16-20
                                            2525, 2930, 3385, 3890, 4445, // 21-25
                                            5050, 5755, 6560, 7465, 8470, // 26-30
                                            9575, 10830, 12235, 13790, 15495, // 31-35
                                            17350, 19455, 21810, 24415, 27270, // 36-40
                                            30375, 33830, 37635, 41790, 46295, // 41-45
                                            51150, 56505, 62360, 68715, 75570}; // 46-50
    public static AnimalBeaconManager animalBeaconManager;

    public SkillManager() {
        animalBeaconManager = new AnimalBeaconManager();
        animalBeaconManager.start();
        for (Player player : Bukkit.getOnlinePlayers()) {
            playerSkills.put(player.getUniqueId(), new PlayerSkills(player.getUniqueId()));
        }
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
        Bukkit.getServer().getPluginCommand("init").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            McRPG.LOGGER.log(Level.SEVERE, "Only players in game can do that!");
            return false;
        }
        return true;
    }
    private static int calculateLevel(Skill skill) {
        double xp = skill.getTotalXp();
        if (xp < levelXPs[0]) {
            return 0;
        } else if (xp >= levelXPs[levelXPs.length - 1]) {
            return 50;
        }

        for (int i = 0; i < levelXPs.length; i++) {
            int levelXp = levelXPs[i];
            if (levelXp >= xp) {
                return i + 1;
            }
        }
        return -1;
    }

    private static boolean reachedNextLevel(Skill skill) {
        return (skill.getLevel() < calculateLevel(skill));
    }

    public static void processSkillIncrement(Player player, Skill skill, double incrementAmount) {
        skill.addXp(incrementAmount);
        if (reachedNextLevel(skill)) {
            skill.incrementLevel();
            skill.processAbilityUpgrade();
            McRPG.LOGGER.log(Level.INFO, player.getName() + "|" + skill + "|Skill Level: " + skill.getLevel());
            player.sendMessage(colorText("&dIncreased " + skill + " skill to level " + skill.getLevel() + "!"));
            launchLevelUpFirework(player.getLocation());
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        if (!player.hasPlayedBefore()) {
            playerSkills.put(player.getUniqueId(), new PlayerSkills(player.getUniqueId()));
        }
    }
}
