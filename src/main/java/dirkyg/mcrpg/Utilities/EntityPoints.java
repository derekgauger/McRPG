package dirkyg.mcrpg.Utilities;

import dirkyg.mcrpg.Utilities.BlockPoints.MiningBlockPoints;

public enum EntityPoints {
    ELDER_GUARDIAN (10),   
    WITHER_SKELETON (4),  
    STRAY (2),
    HUSK (1),
    ZOMBIE_VILLAGER (1),
    EVOKER (5),
    VEX (4),
    VINDICATOR (3),
    ILLUSIONER (3),
    CREEPER (1),
    SKELETON (2),
    SPIDER (1),
    ZOMBIE (1),
    GHAST (5),
    ZOMBIFIED_PIGLIN (1),
    ENDERMAN (2),
    CAVE_SPIDER (1),
    SILVERFISH (1),
    BLAZE (3),
    MAGMA_CUBE (.5),
    ENDER_DRAGON (100),
    WITHER (75),
    WITCH (4),
    ENDERMITE (1),
    GUARDIAN (6),
    SHULKER (1),
    PHANTOM (1),
    DROWNED (1.5),
    PILLAGER (2),
    RAVAGER (10),
    HOGLIN (2),
    PIGLIN (1),
    ZOGLIN (2),
    PIGLIN_BRUTE (3),
    WARDEN (75),
    DEFAULT (0);

    double points;

    EntityPoints(double points) {
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
            EntityPoints.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}