package pacman.entries.pacman.utils;

import java.io.Serializable;
import static pacman.entries.pacman.utils.Parameters.*;
public class ParametricPhenotype implements Serializable {
    private static long serialVersionUID = -987654687654321654L;

    public int[] genoType;
    public double bestFitness;

    // add fields for phenotype:
    /*public int P_MIN_DISTANCE;    //if a ghost is this close, run away
    public int P_MIN_DISTANCE_2;
    public int P_PILLS_THRESHOLD;
    public int P_GHOST_EDIBLE_TIME;
    public int P_PILLS_PERCENT;
    public int P_EDIBLE_GHOST_DISTANCE;
    public int P_POWERS_LEFT;
    public int P_JUNCTION_GAP;*/


    public ParametricPhenotype(String mChromosome) {
        //String mChromosome = gene.getGenotype();
// 46
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
