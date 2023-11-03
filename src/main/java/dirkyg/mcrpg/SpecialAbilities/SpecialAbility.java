package dirkyg.mcrpg.SpecialAbilities;

import dirkyg.mcrpg.McRPG;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

import static dirkyg.mcrpg.Utilities.Visuals.sendActionBar;

public abstract class SpecialAbility {

    UUID playerUUID;
    final long PROMPT_STAY_UP_TIME = 3000;
    final long AFTER_PROMPT_WAIT_TIME = 350;
    public long coolDown = 1;
    public long duration = 60;
    boolean isHappening;
    long stopTime;
    long promptInputDelayUtil;
    String classifier;
    String abilityName;
    boolean isBeingPrompted = true;

    public void processAbility(Cancellable event, Player player, String actionNeeded) {
        long currentTime = System.currentTimeMillis();
        long nextAvailableUsageTime = stopTime + (coolDown * 60 * 1000);
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
        initalizeAbility(player);
        new BukkitRunnable() {
            int iterations = 0;
            @Override
            public void run() {
                if (System.currentTimeMillis() >= stopTime) {
                    isHappening = false;
                    isBeingPrompted = true;
                }
                if (!isHappening) {
                    this.cancel();
                    resetAfterAbilityFinished(player);
                    return;
                }
                processActionDuringAbility(iterations, player);
                if (iterations % 20 == 0) {
                    sendActionBar(player, "&dYou have " + (((stopTime - System.currentTimeMillis()) / 1000) + 1) + " seconds left on your " + abilityName + " - " + classifier + " ability!");
                }
                iterations++;
            }
        }.runTaskTimer(McRPG.plugin, 0, 1L);
    }

    abstract void resetAfterAbilityFinished(Player player);

    abstract void processActionDuringAbility(int iterations, Player player);

    abstract void initalizeAbility(Player player);

    public boolean isHappening() {
        return isHappening;
    }

}
