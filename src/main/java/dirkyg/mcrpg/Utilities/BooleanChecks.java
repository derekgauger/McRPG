package dirkyg.mcrpg.Utilities;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.EntityType;

public class BooleanChecks {

    public static boolean isPickaxe(Material material) {
        return switch (material) {
            case WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> true;
            default -> false;
        };
    }

    public static boolean isShovel(Material material) {
        return switch (material) {
            case WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL -> true;
            default -> false;
        };
    }

    public static boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> true;
            default -> false;
        };
    }

    public static boolean isSword(Material material) {
        return switch (material) {
            case WOODEN_SWORD , STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> true;
            default -> false;
        };
    }

    public static boolean isCrop(Material material) {
        return switch (material) {
            case WHEAT_SEEDS, WHEAT, CARROTS, POTATOES, BEETROOTS, BEETROOT_SEEDS, MELON_SEEDS, PUMPKIN_SEEDS, TORCHFLOWER, TORCHFLOWER_CROP, TORCHFLOWER_SEEDS ->
                    true;
            default -> false;
        };
    }

    public static boolean isWood(Material material) {
        boolean retVal = false;
        switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, CRIMSON_STEM, WARPED_STEM, OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD, CRIMSON_HYPHAE, WARPED_HYPHAE -> {
                retVal = true;
            }
            default -> {}
        };
        if (!retVal && (material.name().contains("LOG")) || material.name().contains("WOOD")){
            retVal = true;
        }
        return retVal;
    }

    public static boolean isMineMat(Material material) {
        boolean retVal = false;
        switch (material) {
            case COAL_ORE, COPPER_ORE, STONE, COBBLESTONE, DIORITE, ANDESITE, GRANITE, IRON_ORE, GOLD_ORE, DIAMOND_ORE, LAPIS_ORE, REDSTONE_ORE, NETHER_QUARTZ_ORE, EMERALD_ORE, OBSIDIAN, END_STONE, ANCIENT_DEBRIS, CRYING_OBSIDIAN, BLACKSTONE, DEEPSLATE, TUFF, CALCITE, DEEPSLATE_LAPIS_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_DIAMOND_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_COPPER_ORE, NETHER_GOLD_ORE, BASALT, SMOOTH_BASALT, SANDSTONE, RED_SANDSTONE, COBBLED_DEEPSLATE -> {
                retVal = true;
            }
            default -> {}
        }
        if (!retVal && (material.name().contains("CONCRETE") || material.name().contains("TERRACOTTA"))) {
            if (!material.name().contains("POWDER")) {
                retVal = true;
            }
        }
        return retVal;
    }

    public static boolean isDigMat(Material material) {
        boolean retVal = false;
        switch (material) {
            case SAND, RED_SAND, GRAVEL, DIRT, COARSE_DIRT, MYCELIUM, SNOW, POWDER_SNOW, SNOW_BLOCK, SOUL_SAND, SOUL_SOIL, ROOTED_DIRT, FARMLAND, MUD, CLAY, DIRT_PATH, PODZOL, GRASS_BLOCK -> {
                retVal = true;
            }
            default -> {}
        };
        if (!retVal && material.name().contains("POWDER")) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean isLog(Material material) {
        return switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, CRIMSON_STEM, WARPED_STEM -> true;
            default -> false;
        };
    }

    public static boolean isBreedable(EntityType entityType) {
        return switch (entityType) {
            case WOLF, CAT, HORSE, DONKEY, LLAMA, SHEEP, COW, GOAT, MUSHROOM_COW, PIG, CHICKEN, RABBIT, TURTLE, PANDA, FOX, BEE, FROG, AXOLOTL, CAMEL, STRIDER, HOGLIN, SNIFFER -> true;
            default -> false;
        };
    }

    public static boolean isPlaced(Block block) {
        return block.hasMetadata("playerPlaced");
    }
}
