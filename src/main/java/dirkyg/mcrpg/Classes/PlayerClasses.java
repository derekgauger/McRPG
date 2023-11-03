package dirkyg.mcrpg.Classes;

import java.util.UUID;

import dirkyg.mcrpg.Classes.HealerClasses.*;
import dirkyg.mcrpg.Classes.RangerClasses.*;
import dirkyg.mcrpg.Classes.RogueClasses.*;
import dirkyg.mcrpg.Classes.WarriorClasses.*;
import dirkyg.mcrpg.Classes.WizardClasses.*;

public class PlayerClasses {

    UUID uuid;
    RPGClass activeClass;
    final Elemental elemental;
    final Berserker berserker;
    final Assassin assassin;
    final Trickster trickster;
    final Sniper sniper;
    final Hunter hunter;
    final Cleric cleric;
    final Necromancer necromancer;
    final FireWizard fireWizard;
    final IceWizard iceWizard;
    final RPGClass[] allClasses;

    public PlayerClasses(UUID uuid) {
        this.uuid = uuid;
        elemental = new Elemental(uuid);
        berserker = new Berserker(uuid);
        assassin = new Assassin(uuid);
        trickster = new Trickster(uuid);
        sniper = new Sniper(uuid);
        hunter = new Hunter(uuid);
        cleric = new Cleric(uuid);
        necromancer = new Necromancer(uuid);
        fireWizard = new FireWizard(uuid);
        iceWizard = new IceWizard(uuid);
        allClasses = new RPGClass[] {elemental, berserker, assassin, trickster, sniper, hunter, cleric, necromancer, fireWizard, iceWizard};
    }

    public RPGClass[] getAllClasses() {
        return allClasses;
    }
}
