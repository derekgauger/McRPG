package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static dirkyg.mcrpg.Utilities.Visuals.colorText;
import static dirkyg.mcrpg.Utilities.Visuals.sendActionBar;

public abstract class Ability {

    UUID playerUUID;
    final long PROMPT_STAY_UP_TIME = 3000;
    final long AFTER_PROMPT_WAIT_TIME = 350;
    public int coolDown = 3 * 60 * 1000;
    public long duration = 5L;
    boolean isHappening;
    long stopTime;
    long promptInputDelayUtil;
    String classifier;
    String abilityName;
    boolean isBeingPrompted = true;

    public void processAbility(Cancellable event, Player player, String actionNeeded) {
        long currentTime = System.currentTimeMillis();
        long nextAvailableUsageTime = stopTime + coolDown;
        long resetPromptTime = promptInputDelayUtil + PROMPT_STAY_UP_TIME;
        if (currentTime < promptInputDelayUtil) {
            return;
        }
        if (isBeingPrompted && currentTime < nextAvailableUsageTime) {
            sendActionBar(player, "&cYou cannot use "
                    + abilityName + " - " + classifier
                    + " ability for another " + ((nextAvailableUsageTime - currentTime) / 1000)
                    + " seconds!");
            return;
        }
        if (System.currentTimeMillis() >= resetPromptTime) {
            isBeingPrompted = true;
        }
        if (isBeingPrompted && currentTime >= nextAvailableUsageTime) {
            sendActionBar(player, "&d" + actionNeeded + " : Start " + abilityName + " - " + classifier + " Ability");
            promptInputDelayUtil = currentTime + AFTER_PROMPT_WAIT_TIME;
            isBeingPrompted = false;
            event.setCancelled(true);
            return;
        }
        isHappening = true;
        event.setCancelled(true);
        stopTime = currentTime + (duration * 1000L);
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() >= stopTime) {
                    isHappening = false;
                    isBeingPrompted = true;
                }
                if (!isHappening) {
                    this.cancel();
                    resetAfterAbilityFinished();
                    return;
                }
                processActionDuringAbility();
                sendActionBar(player, "&dYou have " + (((stopTime - System.currentTimeMillis()) / 1000) + 1) + " seconds left on your " + abilityName + " - " + classifier + " ability!");
            }
        }.runTaskTimer(McRPG.plugin, 0, 20);
    }

    abstract void resetAfterAbilityFinished();
    abstract void processActionDuringAbility();

    public boolean isHappening() {
        return isHappening;
    }

}
