package dirkyg.mcrpg.Classes.RogueClasses;

import dirkyg.mcrpg.Classes.RPGClass;
import dirkyg.mcrpg.McRPG;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import java.util.UUID;

public class Assassin extends RPGClass implements Listener {

    public Assassin(UUID uuid) {
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
