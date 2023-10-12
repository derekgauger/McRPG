package dirkyg.mcrpg.Skills;

import java.util.UUID;

public abstract class Skill {

    UUID uuid;
    private int level;
    private double totalXp;

    abstract void processAbilityUpgrade();

    public int getLevel() {
        return level;
    }

    public void incrementLevel() {
        this.level += 1;
    }

    public void addXp(double xp) {
        this.totalXp += xp;
    }

    public double getTotalXp() {
        return totalXp;
    }


}
