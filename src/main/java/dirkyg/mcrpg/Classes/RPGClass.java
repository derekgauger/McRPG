package dirkyg.mcrpg.Classes;

import java.util.UUID;

public abstract class RPGClass {

    public UUID uuid;
    public int level = 1;
    public double totalXp = 0;
    private boolean isActive = false;

    public abstract void activatePlayer();
    public abstract void deactivatePlayer();
    public abstract void setSubClass(Class subClassType);

    public void incrementLevel() {
        this.level += 1;
    }

    public void addXp(double xp) {
        this.totalXp += xp;
    }

    public double getTotalXp() {
        return totalXp;
    }

    public int getLevel() {
        return level;
    }

    public boolean isCurrentlyActive() {
        return isActive;
    }

    public void setCurrentlyActive(boolean currentlyActive) {
        this.isActive = currentlyActive;
    }
}
