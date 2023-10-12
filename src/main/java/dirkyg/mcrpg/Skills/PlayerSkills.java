package dirkyg.mcrpg.Skills;

import java.util.UUID;

public class PlayerSkills {

    UUID uuid;
    private final LoggingSkill logging;
    private final MiningSkill mining;
    private final StrengthSkill strength;
    private final FarmingSkill farming;
    private final AgilitySkill agility;
    private final CommerceSkill commerce;
    private final BuildingSkill building;
    private final BreedingSkill breeding;
    private final FishingSkill fishing;
    private final DiggingSkill digging;
    private final ArcherySkill archery;
    private final Skill[] allSkills;

    public PlayerSkills(UUID uuid) {
        this.uuid = uuid;
        logging = new LoggingSkill(uuid);
        mining = new MiningSkill(uuid);
        strength = new StrengthSkill(uuid);
        farming = new FarmingSkill(uuid);
        agility = new AgilitySkill(uuid);
        commerce = new CommerceSkill(uuid);
        building = new BuildingSkill(uuid);
        breeding = new BreedingSkill(uuid);
        fishing = new FishingSkill(uuid);
        digging = new DiggingSkill(uuid);
        archery = new ArcherySkill(uuid);
        allSkills = new Skill[] {logging, mining, strength, farming, agility, commerce, building, breeding, fishing, digging, archery};
    }

    public LoggingSkill getLogging() {
        return logging;
    }

    public MiningSkill getMining() {
        return mining;
    }

    public StrengthSkill getStrength() {
        return strength;
    }

    public FarmingSkill getFarming() {
        return farming;
    }

    public AgilitySkill getAgility() {
        return agility;
    }

    public CommerceSkill getCommerce() {
        return commerce;
    }

    public BuildingSkill getBuilding() {
        return building;
    }

    public BreedingSkill getBreeding() {
        return breeding;
    }

    public FishingSkill getFishing() {
        return fishing;
    }

    public DiggingSkill getDigging() {
        return digging;
    }

    public ArcherySkill getArchery() {
        return archery;
    }

    public Skill[] getAllSkills() {
        return allSkills;
    }
}
