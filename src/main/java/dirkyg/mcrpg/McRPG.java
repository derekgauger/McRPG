package dirkyg.mcrpg;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import dirkyg.mcrpg.Classes.ClassManager;
import dirkyg.mcrpg.Classes.ClassXpListeners;
import dirkyg.mcrpg.Skills.SkillManager;
import dirkyg.mcrpg.Utilities.Listeners;
import dirkyg.mcrpg.WorldGeneration.SchematicWorldGenerator;

public final class McRPG extends JavaPlugin {

    public static McRPG plugin;
    public static Logger LOGGER;
    
    @Override
    public void onEnable() {
        plugin = this;
        LOGGER = plugin.getLogger();
        File folder = new File(plugin.getDataFolder().getAbsolutePath());
        if (!folder.exists()) {
            if (folder.mkdir()) {
                LOGGER.info("Plugin data folder created!");
            } else {
                LOGGER.log(Level.SEVERE, "Plugin data folder creation failed!");
            }
        } else {
            LOGGER.info("Plugin data folder exists!");
        }
        new Listeners();
        new ClassManager();
        new ClassXpListeners();
        new SkillManager();
        new SchematicWorldGenerator();
        new BukkitRunnable() {
            @Override
            public void run() {
                save();
            }
        }.runTaskTimer(this, 20 * 30, 20 * 30);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void save() {
        try {
            SchematicWorldGenerator.serializeWorldNames();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
