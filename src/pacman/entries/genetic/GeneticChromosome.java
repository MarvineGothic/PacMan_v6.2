package pacman.entries.genetic;

import pacman.entries.GA.PacManGA;
import pacman.entries.utils.GameScore;
import pacman.entries.utils.ParametricPhenotype;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Random;

import static pacman.entries.GA.PacManGA.POP_SIZE;

public class GeneticChromosome implements Serializable {

    private static final long serialVersionUID = -4765878553574173154L;
    GameScore gameScore;
    private boolean[] _genome;
    private double _fitness;

    /**
     * Creates a new GeneticChromosome with the specified number of
     * randomized bits. This constructor is used to generate the initial
     * population.
     *
     * @param bits number of bits
     */
    public GeneticChromosome(PacManGA ga, int bits) {
        _genome = new boolean[bits];
        randomizeChromosome(ga);
    }

    /**
     * Creates a new GeneticChromosome from a specified bit array.
     *
     * @param genome bit array
     */
    public GeneticChromosome(PacManGA ga, boolean[] genome) {
        _genome = genome;
        gameScore = ga.runEvaluationGame(getGenotype());
        //gameScore = ga.evaluatePopulation(getGenotype(), POP_SIZE);
        _fitness = gameScore.getFintess_01();

    }

    /**
     * Randomizes the numbers on the mChromosome array to values 0 or 1
     */
    public void randomizeChromosome(PacManGA ga) {
        // code for randomization of initial weights goes HERE
        for (int i = 0; i < _genome.length; i++) {
            _genome[i] = Math.random() < 0.5;
        }
        gameScore = ga.runEvaluationGame(getGenotype());
        //gameScore = ga.evaluatePopulation(getGenotype(), POP_SIZE);
        _fitness = gameScore.getFintess_01();
    }

    public double getFitness() {
        return _fitness;
    }

    /**
     * Returns a bit string representation (true = '1' and false = '0')
     * of the underlying bit array.
     *
     * @return bit string
     */
    public String getGenotype() {
        StringBuilder sb = new StringBuilder();
        for (boolean bit : _genome)
            sb.append(bit ? '1' : '0');
        return sb.toString();
    }

    public ParametricPhenotype getParameterPhenoType() {
        return new ParametricPhenotype(this);
    }

    /**
     * This method mates this chromosome with a given chromosome. The specified
     * crossover rate determines the likelihood that single-point crossover will
     * occur between the two chromosomes. This method returns an array containing
     * two offspring.
     *
     * @param othr other parent chromosome
     * @param rate crossover probability
     * @return offspring chromosomes
     */
    public GeneticChromosome[] singlePointCross(PacManGA ga, GeneticChromosome othr, double rate) {
        boolean[] c1 = Arrays.copyOf(this._genome, this._genome.length);
        boolean[] c2 = Arrays.copyOf(othr._genome, othr._genome.length);

        if (Math.random() <= rate) {
            int index = (int) (Math.random() * this._genome.length);
            System.arraycopy(this._genome, 0, c2, 0, index);
            System.arraycopy(othr._genome, 0, c1, 0, index);
        }

        return new GeneticChromosome[]{new GeneticChromosome(ga, c1),
                new GeneticChromosome(ga, c2)};
    }

    public GeneticChromosome[] multiPointCross(PacManGA ga, GeneticChromosome othr, double rate) {
        int crossLines = new Random().nextInt(this._genome.length / 5);
        boolean[] c1 = Arrays.copyOf(this._genome, this._genome.length);
        boolean[] c2 = Arrays.copyOf(othr._genome, othr._genome.length);

        for (int i = 0; i < crossLines; i++) {
            if (Math.random() <= rate) {
                int index = (int) (Math.random() * this._genome.length);
                System.arraycopy(this._genome, 0, c2, 0, index);
                System.arraycopy(othr._genome, 0, c1, 0, index);
            }
        }
        return new GeneticChromosome[]{new GeneticChromosome(ga, c1),
                new GeneticChromosome(ga, c2)};
    }

    /**
     * This method randomly flips bits in the genome based on a specified
     * mutation probability. The greater the mutation rate, the more likely
     * mutations will occur.
     *
     * @param rate mutation probability
     */
    public void mutate(PacManGA ga, double rate) {
        for (int i = 0; i < _genome.length; i++)
            if (Math.random() <= rate)
                _genome[i] = !_genome[i];
        gameScore = ga.runEvaluationGame(getGenotype());
        //gameScore = ga.evaluatePopulation(getGenotype(), POP_SIZE);
        _fitness = gameScore.getFintess_01();
    }

}
