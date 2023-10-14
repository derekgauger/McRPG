package dirkyg.mcrpg.Classes;

import dirkyg.mcrpg.Classes.HealerClasses.Healer;
import dirkyg.mcrpg.Classes.RangerClasses.Ranger;
import dirkyg.mcrpg.Classes.RogueClasses.Rogue;
import dirkyg.mcrpg.Classes.WarriorClasses.Warrior;
import dirkyg.mcrpg.Classes.WizardClasses.Wizard;

import java.util.UUID;

public class PlayerClasses {

    UUID uuid;
    private final Warrior warrior;
    private final Rogue rogue;
    private final Ranger ranger;
    private final Healer healer;
    private final Wizard wizard;
    private final RPGClass[] allClasses;

    public PlayerClasses(UUID uuid) {
        this.uuid = uuid;
        warrior = new Warrior(uuid);
        rogue = new Rogue(uuid);
        ranger = new Ranger(uuid);
        healer = new Healer(uuid);
        wizard = new Wizard(uuid);
        allClasses = new RPGClass[] {warrior, rogue, healer, wizard};
    }

    public RPGClass[] getAllClasses() {
        return allClasses;
    }

    public Warrior getWarrior() {
        return warrior;
    }

    public Rogue getRogue() {
        return rogue;
    }

    public Ranger getRanger() {
        return ranger;
    }

    public Healer getHealer() {
        return healer;
    }

    public Wizard getWizard() {
        return wizard;
    }
}
