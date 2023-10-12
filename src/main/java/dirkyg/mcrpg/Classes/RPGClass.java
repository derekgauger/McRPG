package dirkyg.mcrpg.Classes;

public abstract class RPGClass {

    private int level = 1;
    private boolean isActive = false;
    private double totalXp;

    abstract void activatePlayer();
    abstract void deactivatePlayer();

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
