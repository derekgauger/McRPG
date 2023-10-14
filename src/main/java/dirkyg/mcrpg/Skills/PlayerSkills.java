package dirkyg.mcrpg.Skills;

import java.util.UUID;

public class PlayerSkills {

    UUID uuid;
    private final LoggingSkill logging;
    private final MiningSkill mining;
    private final FarmingSkill farming;
    private final CommerceSkill commerce;
    private final BuildingSkill building;
    private final BreedingSkill breeding;
    private final FishingSkill fishing;
    private final DiggingSkill digging;
    private final Skill[] allSkills;

    public PlayerSkills(UUID uuid) {
        this.uuid = uuid;
        logging = new LoggingSkill(uuid);
        mining = new MiningSkill(uuid);
        farming = new FarmingSkill(uuid);
        commerce = new CommerceSkill(uuid);
        building = new BuildingSkill(uuid);
        breeding = new BreedingSkill(uuid);
        fishing = new FishingSkill(uuid);
        digging = new DiggingSkill(uuid);
        allSkills = new Skill[] {logging, mining, farming, commerce, building, breeding, fishing, digging};
    }
}
