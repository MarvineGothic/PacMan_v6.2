package pacman.entries.pacman.utils;

import java.io.Serializable;

public class Parameters implements Serializable {
    private static final long serialVersionUID = -2584408698436514643L;

    public static int MIN_DISTANCE = 30;    //if a ghost is this close, run away
    public static int MIN_DISTANCE_2 = 25;
    public static int PILLS_THRESHOLD = 0;
    public static double PILLS_PERCENT = 20;
    public static int GHOST_EDIBLE_TIME = 0;
    public static int EDIBLE_GHOST_DISTANCE = 20;
    public static int POWERS_LEFT = 1;
    public static int JUNCTION_GAP = 2;
    public static void printParameters(){
        System.out.printf("%d %d %d %s %d %d %d %d", MIN_DISTANCE, MIN_DISTANCE_2, PILLS_THRESHOLD, PILLS_PERCENT, GHOST_EDIBLE_TIME, EDIBLE_GHOST_DISTANCE, POWERS_LEFT, JUNCTION_GAP);
    }
}
