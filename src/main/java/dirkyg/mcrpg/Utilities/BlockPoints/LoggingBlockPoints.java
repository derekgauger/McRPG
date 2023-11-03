package dirkyg.mcrpg.Utilities.BlockPoints;

public enum LoggingBlockPoints {
    OAK_LOG (1),
    SPRUCE_LOG (1),
    BIRCH_LOG (1),
    JUNGLE_LOG (1),
    ACACIA_LOG (1),
    DARK_OAK_LOG (1),
    CRIMSON_STEM (2),
    WARPED_STEM (2),
    MANGROVE_LOG (2),
    CHERRY_LOG (2),
    MUSHROOM_STEM (2),
    DEFAULT (0);

    double points;

    LoggingBlockPoints(double points) {
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
            LoggingBlockPoints.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}