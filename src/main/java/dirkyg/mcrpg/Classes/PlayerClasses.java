package dirkyg.mcrpg.Classes;

import java.util.UUID;

public class PlayerClasses {

    UUID uuid;
    private final Warrior warrior;
    private final Rogue rogue;
    private final Monk monk;
    private final Wizard wizard;
    private final RPGClass[] allClasses;

    public PlayerClasses(UUID uuid) {
        this.uuid = uuid;
        warrior = new Warrior(uuid);
        monk = new Monk(uuid);
        rogue = new Rogue(uuid);
        wizard = new Wizard(uuid);
        allClasses = new RPGClass[] {warrior, rogue, monk, wizard};
    }

    public Warrior getWarrior() {
        return warrior;
    }

    public Monk getMonk() {
        return monk;
    }

    public Rogue getRogue() {
        return rogue;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public RPGClass[] getAllClasses() {
        return allClasses;
    }
}
