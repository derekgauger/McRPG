package dirkyg.mcrpg.Utilities.BlockPoints;

public enum DiggingBlockPoints {
    GRASS_BLOCK (1),
    PODZOL (1),
    MYCELIUM (1),
    DIRT_PATH (1),
    DIRT (1),
    COARSE_DIRT (1),
    ROOTED_DIRT (1),
    FARMLAND (1),
    MUD (1),
    CLAY (1),
    SAND (1),
    GRAVEL (1),
    RED_SAND (1),
    SOUL_SAND (1.5),
    SOUL_SOIL (1.5),
    SNOW (.2),
    SNOW_BLOCK (.75),
    POWDER_SNOW (.75),
    DEFAULT (0);

    double points;

    DiggingBlockPoints(double points) {
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
            DiggingBlockPoints.valueOf(name);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
