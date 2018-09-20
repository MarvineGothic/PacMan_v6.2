package pacman.entries.utils;

import pacman.entries.genetic.GeneticChromosome;

import java.io.Serializable;

public class ParametricPhenotype implements Serializable {
    private static long serialVersionUID = -987654687654321654L;

    public int[] genoType;
    public double bestFitness;

    // add fields for phenotype:
    public int P_MIN_DISTANCE;    //if a ghost is this close, run away
    public int P_MIN_DISTANCE_2;
    public int P_PILLS_THRESHOLD;
    public int P_GHOST_EDIBLE_TIME;


    public ParametricPhenotype(GeneticChromosome gene) {
        String mChromosome = gene.getGenotype();

        int i = 0;
        int j = 4;
        P_GHOST_EDIBLE_TIME = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        P_MIN_DISTANCE = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 6;
        P_MIN_DISTANCE_2 = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 5;
        P_PILLS_THRESHOLD = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 1;
       /* fleeBeAfraid = Integer.parseInt(mChromosome.substring(i, j), 2) == 1;
        i = j;
        j += 1;
        eatBeAfraid = Integer.parseInt(mChromosome.substring(i, j), 2) == 1;
        i = j;
        j += 7;
        fleeDepth = Integer.parseInt(mChromosome.substring(i, j), 2);
        i = j;
        j += 7;
        eatDepth = Integer.parseInt(mChromosome.substring(i, j), 2);*/
    }
}
