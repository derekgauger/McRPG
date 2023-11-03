package dirkyg.mcrpg.Utilities.BlockPoints;

public enum MiningBlockPoints {

    // Pickaxe
    STONE (1),
    GRANITE (1),
    DIORITE (1),
    ANDESITE (1),
    DEEPSLATE (1),
    COBBLED_DEEPSLATE (1),
    CALCITE (1),
    TUFF (1),
    DRIPSTONE_BLOCK (1),
    CRIMSON_NYLIUM (.1),
    WARPED_NYLIUM (.1),
    COBBLESTONE (1),
    COAL_ORE (1.5),
    DEEPSLATE_COAL_ORE (1.5),
    IRON_ORE (2),
    DEEPSLATE_IRON_ORE (2),
    COPPER_ORE (1.5),
    DEEPSLATE_COPPER_ORE (1.5),
    GOLD_ORE (2),
    DEEPSLATE_GOLD_ORE (2),
    REDSTONE_ORE (1.5),
    DEEPSLATE_REDSTONE_ORE (1.5),
    EMERALD_ORE (8),
    DEEPSLATE_EMERALD_ORE (8),
    LAPIS_ORE (2),
    DEEPSLATE_LAPIS_ORE (2),
    DIAMOND_ORE (10),
    DEEPSLATE_DIAMOND_ORE (10),
    NETHER_GOLD_ORE (1.2),
    NETHER_QUARTZ_ORE (1),
    ANCIENT_DEBRIS (15),
    AMETHYST_BLOCK (3),
    COPPER_BLOCK (2),
    EXPOSED_COPPER (2),
    WEATHERED_COPPER (2),
    OXIDIZED_COPPER (2),
    MOSSY_COBBLESTONE (1),
    OBSIDIAN (3),
    PURPUR_BLOCK (1),
    SPAWNER (20),
    ICE (.3),
    BASALT (1),
    INFESTED_STONE (1),
    INFESTED_COBBLESTONE (1),
    INFESTED_STONE_BRICKS (1),
    INFESTED_MOSSY_STONE_BRICKS (1),
    INFESTED_CRACKED_STONE_BRICKS (1),
    INFESTED_DEEPSLATE (1),
    STONE_BRICKS (1),
    MOSSY_STONE_BRICKS (1),
    CRACKED_STONE_BRICKS (1),
    CHISELED_STONE_BRICKS (1),
    DEEPSLATE_BRICKS (1),
    CRACKED_DEEPSLATE_BRICKS (1),
    DEEPSLATE_TILES (1),
    CRACKED_DEEPSLATE_TILES (1),
    CHISELED_DEEPSLATE (1),
    REINFORCED_DEEPSLATE (1),
    NETHER_BRICKS (1),
    CRACKED_NETHER_BRICKS (1),
    CHISELED_NETHER_BRICKS (1),
    SCULK (2),
    SCULK_CATALYST (2),
    END_STONE (1.5),
    END_STONE_BRICKS (1.5),
    PACKED_ICE (.3),
    PRISMARINE (1.5),
    PRISMARINE_BRICKS (1.5),
    DARK_PRISMARINE (1.5),
    MAGMA_BLOCK (.3),
    RED_NETHER_BRICKS (1),
    BONE_BLOCK (1),
    BLUE_ICE (.4),
    CRYING_OBSIDIAN (3),
    BLACKSTONE (1),
    SANDSTONE (1),
    RED_SANDSTONE (2),
    NETHERRACK (.2),
    BLOCK_OF_RAW_GOLD (2),
    BLOCK_OF_RAW_IRON (2),
    BLOCK_OF_RAW_COPPER (2),
    DEFAULT (0);

    double points;

    MiningBlockPoints(double points) {
        this.points = points;
    }

    public double getpoints() {
        return points;
    }

    public static double getEnumpoints(String points) {
        try {
            return valueOf(points).getpoints();
        } catch (IllegalArgumentException e) {
            return DEFAULT.getpoints();
        }
    }

    public static boolean contains(String name) {
        try {
            MiningBlockPoints.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
