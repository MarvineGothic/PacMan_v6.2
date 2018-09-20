package pacman.entries.genetic;

import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.entries.pacman.PacManWeightedGA_Ai;
import pacman.entries.utils.GameScore;
import pacman.game.Game;

import java.util.Properties;
import java.util.Random;

import static pacman.entries.GA.PacManGA.MAX_GEN;
import static pacman.game.Constants.DELAY;

public class GeneticAlgorithm {

    public static final String GA_PROPERTIES = "./src/pacman/entries/properties/ga.properties";
    public static final String PACMAN_PROPERTIES = "./src/pacman/entries/properties/pacman.properties";
    public static final String GEN_POP = "./src/pacman/entries/assets/ga/gen_pop1.ser";
    public static final int EVALUATION_ITERATIONS = 10;

    public GeneticGene[] genes;
    public Properties properties;

    /**
     * Runs the binary genetic algorithm with the specified fitness calculator
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
     * This method runs a game and returns an object with game results
     * Results saved:
     * - game score;
     * - game total time;
     * - game level;
     *
     * @return
     */
    public GameScore runEvaluationGame(String genotype) {
        Random rnd = new Random(0);
        Game game = new Game(rnd.nextLong());

        while (!game.gameOver()) {
            game.advanceGame(new PacManWeightedGA_Ai(genes, getPhenotype(genotype)).getMove(game.copy(), System.currentTimeMillis() + DELAY),
                    new Legacy2TheReckoning().getMove(game.copy(), System.currentTimeMillis() + DELAY));

        }
        return new GameScore(game);
    }


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
