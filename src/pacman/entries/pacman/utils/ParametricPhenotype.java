package pacman.entries.pacman.utils;

import static pacman.entries.pacman.utils.Parameters.*;

public class ParametricPhenotype {

    public ParametricPhenotype(String mChromosome) {

        int i = 0;
        int j = 4;
        GHOST_EDIBLE_TIME = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        MIN_DISTANCE = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        MIN_DISTANCE_2 = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        PILLS_THRESHOLD = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        PILLS_PERCENT = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        EDIBLE_GHOST_DISTANCE = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        POWERS_LEFT = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        JUNCTION_GAP = Integer.parseInt(mChromosome.substring(i, j), 2);
    }
}
