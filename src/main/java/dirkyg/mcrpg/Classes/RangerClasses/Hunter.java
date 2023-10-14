package dirkyg.mcrpg.Classes.RangerClasses;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;

public class Hunter extends RPGClass implements Listener {

    public Hunter(UUID uuid) {
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
    public void setSubClass(Class subClassType) {

    }
}
