package pacman.entries.pacman.GeneticAlgorithm;

import pacman.entries.pacman.utils.GameScore;

import java.math.BigInteger;
import java.util.Properties;

import static pacman.entries.pacman.utils.Utils.ROOT;

public abstract class GeneticAlgorithm {

    public static final String GA_PROPERTIES = ROOT + "/Files/properties/ga.properties";
    public static final String PARAM_GA_PROPERTIES = ROOT + "/Files/properties/p_ga.properties";
    private static final int EVALUATION_ITERATIONS = 10;
    protected static String GEN_POP = "";
    protected static String GEN_POP_TRAIN = "";
    protected static boolean USE_PARAMETRIC_PHENOTYPE = false;
    protected static int MAX_GEN;

    protected static int NUMBER_OF_GENES;
    protected static int GENE_SIZE;
    protected static int CHROMOSOME_LENGTH;

    protected static int POP_SIZE;
    protected static double CROSS;
    protected static double MUTATE;
    protected static double ELITISM;

    protected static int TOURNAMENT_SIZE;
    public Properties properties;
    private double lower, upper;

    public GeneticAlgorithm(Properties properties) {
        CHROMOSOME_LENGTH = NUMBER_OF_GENES * GENE_SIZE;
        this.lower = -3.0;
        this.upper = 3.0;
        this.properties = properties;
    }

    /**
     * Prints the header for the tabular data.
     */
    private static void printHeader() {
        System.out.println();
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
            pop = pop.evolvePopulation();
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
    protected double[] getPhenotype(String genotype) {
        double[] phenotype = new double[NUMBER_OF_GENES];
        int index = 0;

        for (int i = 0; i < NUMBER_OF_GENES; i++) {
            String bitstring = genotype.substring(index, index + GENE_SIZE);
            index += GENE_SIZE;
            phenotype[i] = decode(bitstring);
        }

        return phenotype;
    }

    /**
     * Encoding a PhenoType(weight) to GenoType
     *
     * @param val
     * @return
     */
    public String encode(double val) {
        double norm = (val - lower) / (upper - lower);
        BigInteger bin = BigInteger.valueOf(Math.round(norm * (Math.pow(2, GENE_SIZE) - 1)));
        StringBuilder bits = new StringBuilder(bin.toString(2));

        while (bits.length() < GENE_SIZE)
            bits.insert(0, "0");

        return bits.toString();
    }

    /**
     * Decoding a GenoType (a bit string) to PhenoType (a double weight)
     *
     * @param bitstring
     * @return
     */
    public double decode(String bitstring) {
        double bin = new BigInteger(bitstring, 2).doubleValue();
        return lower + (upper - lower) * bin / (Math.pow(2, GENE_SIZE) - 1);
    }
}
