package dirkyg.mcrpg.SpecialAbilities;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Classes.RPGClass;
import static dirkyg.mcrpg.Utilities.Visuals.*;

public class Swapper implements Listener {

    UUID uuid;
    RPGClass rpgClass;
    public SpecialAbility activeAbility;
    SpecialAbility[] abilities;
    int currentAbilityIndex = 0;

    public Swapper(UUID uuid, RPGClass rpgClass, SpecialAbility[] abilities) {
        this.uuid = uuid;
        this.rpgClass = rpgClass;
        this.abilities = abilities;
        this.activeAbility = abilities[0];
        Bukkit.getPluginManager().registerEvents(this, McRPG.plugin);;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (player.getUniqueId() != uuid || !rpgClass.isCurrentlyActive() || !player.isSneaking() || activeAbility.isHappening()) {
            return;
        }
        if (event.getAction() == Action.RIGHT_CLICK_AIR) {
            currentAbilityIndex += 1;
            if (currentAbilityIndex == abilities.length) {
                currentAbilityIndex = 0;
            }
            activeAbility = abilities[currentAbilityIndex];
            sendActionBar(player, "&d" + activeAbility + " Mode");
        }
    }

}
