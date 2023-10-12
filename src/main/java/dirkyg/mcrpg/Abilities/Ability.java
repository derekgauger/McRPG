package dirkyg.mcrpg.Abilities;

import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Skills.Skill;
import dirkyg.mcrpg.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.UUID;

public abstract class Ability {

    UUID playerUUID;
    final long PROMPT_STAY_UP_TIME = 3000;
    final long AFTER_PROMPT_WAIT_TIME = 350;
    public int coolDown = 3 * 60 * 1000;
    public long duration = 5L;
    boolean isHappening;
    long stopTime;
    long promptInputDelayUtil;
    Skill skill;
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
            Utils.sendActionBar(player, Utils.chat("&cYou cannot use "
                    + abilityName + " - " + skill.toString()
                    + " ability for another " + ((nextAvailableUsageTime - currentTime) / 1000)
                    + " seconds!"));
            return;
        }
        if (System.currentTimeMillis() >= resetPromptTime) {
            isBeingPrompted = true;
        }
        if (isBeingPrompted && currentTime >= nextAvailableUsageTime) {
            Utils.sendActionBar(player, Utils.chat("&d" + actionNeeded + " : Start " + abilityName + " - " + skill.toString() + " ability"));
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
                Utils.sendActionBar(player, Utils.chat("&dYou have " + (((stopTime - System.currentTimeMillis()) / 1000) + 1) + " seconds left on your " + abilityName + " - " + skill.toString()+ " ability!"));
            }
        }.runTaskTimer(McRPG.plugin, 0, 20);
    }

    abstract void resetAfterAbilityFinished();
    abstract void processActionDuringAbility();

//    public int getDuration() {
//        return duration;
//    }
//
//    public void setDuration(int duration) {
//        this.duration = duration;
//    }
//
    public boolean isHappening() {
        return isHappening;
    }

//
//    public void setHappening(boolean happening) {
//        isHappening = happening;
//    }
//
//    public long getStopTime() {
//        return stopTime;
//    }
//
//    public void setStopTime(long stopTime) {
//        this.stopTime = stopTime;
//    }
}
