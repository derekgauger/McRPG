package dirkyg.mcrpg.Classes.WarriorClasses;

import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.UUID;

public class Elemental extends RPGClass implements Listener {

    public Elemental(UUID uuid) {
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
