package dirkyg.mcrpg.Classes.HealerClasses;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;

public class Cleric extends RPGClass implements Listener {

    public Cleric(UUID uuid) {
        this.uuid = uuid;
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);
    }

    @Override
    public void activatePlayer() {

    }

    @Override
    public void deactivatePlayer() {

    }

    @Override
    public String toString() {
        return "Cleric";
    }

    @Override
    public void processClassUpgrade() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processClassUpgrade'");
    }
}
