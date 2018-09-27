package pacman.entries.pacman.GeneticAlgorithm;

import pacman.entries.pacman.utils.GameScore;

import java.util.Properties;

public abstract class GeneticAlgorithm {

    public static final String ROOT = "./src/pacman/entries/pacman";
    public static final String PACMAN_PROPERTIES = ROOT + "/Files/properties/pacman.properties";
    public static final String GA_PROPERTIES = ROOT + "/Files/properties/ga.properties";
    public static final String PARAM_GA_PROPERTIES = ROOT + "/Files/properties/p_ga.properties";
    public static final int EVALUATION_ITERATIONS = 10;
    public static String GEN_POP = "";
    public static boolean USE_PARAMETRIC_PHENOTYPE = false;
    public static int MAX_GEN;

    public static int POP_SIZE;
    public static double CROSS;
    public static double MUTATE;
    public static double ELITISM;

    public static int TOURNAMENT_SIZE;

    public GeneticGene[] genes;
    public Properties properties;

    /**
     * Prints the header for the tabular data.
     */
    private static void printHeader() {
        System.out.printf("%6s\t%10s\t%15s\t%15s\t%s\n", "Gen", "Score", "Min", "Avg", "Genotype");
    }

    /**
     * Prints the specified population into tabular form.
     */
    private static void printGen(int gen, GeneticPopulation pop) {
        GeneticChromosome best = pop.getBestChromosome();
        double min = best.getFitness();
        double avg = pop.getAverageFitness();

        System.out.printf("%6d\t%10d\t%15.8f\t%15.8f\t%s\n", gen, best.gameScore.score, min, avg, best.getGenotype());
    }

    /**
     * Runs the binary GeneticAlgorithm algorithm with the specified fitness calculator
     * and the specified properties. This method returns the best chromosome.
     *
     * @return most optimal chromosome
     */
    public GeneticPopulation run(GeneticPopulation pop) {
        int gen = 0;
        printHeader();
        printGen(gen, pop);
        while (gen < MAX_GEN) {
            pop = pop.evolvePopulation2();
            gen++;
            printGen(gen, pop);
        }

        return pop;
    }

    /**
     * This method runs a game and returns an object with game results
     * Results saved:
     * - game score;
     * - game total time;
     * - game level;
     *
     * @return
     */
    public abstract GameScore runEvaluationGame(String genotype);


    @Deprecated
    public GameScore evaluatePopulation(String genotype, int pop_size) {
        GameScore bestScore = null;

        for (int i = 0; i < pop_size; i++) {
            double avg = 0;
            int maxScore = Integer.MIN_VALUE;


            for (int j = 0; j < EVALUATION_ITERATIONS; j++) {
                GameScore gameScore = runEvaluationGame(genotype);
                if (gameScore.score > maxScore) {
                    maxScore = gameScore.score;
                    bestScore = gameScore;
                }
                avg += gameScore.score;
            }
            avg /= EVALUATION_ITERATIONS;
            System.out.println("\t" + i + ": GameScore: " + avg);
        }
        return bestScore;
    }

    /**
     * Returns the phenotype (actual values) of the various genes in the genotype.
     * This is used by the fitness function to determine the fitness of a parameter set.
     *
     * @param genotype bitstring
     * @return phenotype
     */
    public double[] getPhenotype(String genotype) {
        double[] phenotype = new double[genes.length];
        int index = 0;

        for (int i = 0; i < genes.length; i++) {
            String bitstring = genotype.substring(index, index + genes[i].getBits());
            index += genes[i].getBits();
            phenotype[i] = genes[i].decode(bitstring);
        }

        return phenotype;
    }

}
