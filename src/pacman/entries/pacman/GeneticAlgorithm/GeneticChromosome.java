package pacman.entries.pacman.GeneticAlgorithm;

import pacman.entries.pacman.utils.GameScore;
import pacman.entries.pacman.utils.ParametricPhenotype;

import java.io.Serializable;
import java.util.Objects;

public class GeneticChromosome implements Serializable {

    private static final long serialVersionUID = -4765878553574173154L;
    GameScore gameScore;
    private boolean[] _genome;
    private double _fitness = Double.MAX_VALUE;
    private double avgFitness;

    /**
     * Creates a new GeneticChromosome with the specified number of
     * randomized bits. This constructor is used to generate the initial
     * population.
     *
     * @param bits number of bits
     */
    public GeneticChromosome(int bits) {
        _genome = new boolean[bits];
        randomizeChromosome();
    }

    /**
     * Creates a new GeneticChromosome from a specified bit array.
     *
     * @param genome bit array
     */
    public GeneticChromosome(boolean[] genome) {
        _genome = genome;
    }

    public void evaluateChromosome(GeneticAlgorithm ga) {
        gameScore = ga.runEvaluationGame(getGenotype());
        _fitness = gameScore.getFintess_01();
    }

    /**
     * Randomizes the numbers on the mChromosome array to values 0 or 1
     */
    public void randomizeChromosome() {
        for (int i = 0; i < _genome.length; i++) {
            _genome[i] = Math.random() < 0.5;
        }
    }

    public void averageFitness(GeneticAlgorithm ga) {
        int bestScore = 0;
        double avgFitnessSum = 0;
        for (int i = 0; i < 5; i++) {
            GameScore tempScore = ga.runEvaluationGame(getGenotype());
            int score = tempScore.score;
            if (score > bestScore) {
                gameScore = tempScore;
                _fitness = tempScore.getFintess_01();
                bestScore = score;
            }
            avgFitnessSum += tempScore.getFintess_01();
            System.out.println(i + ") Score: " + tempScore.score);

        }
        avgFitness = avgFitnessSum / 5.0;
        System.out.println("Average Fitness: " + 1 / avgFitness);
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

    /**
     * Every time must create new instance
     * because it initializes GLOBAL parameters in Parameters Class
     *
     * @return
     */
    @Deprecated
    public ParametricPhenotype getParameterPhenoType() {
        return new ParametricPhenotype(this.getGenotype());
    }

    /**
     * This method randomly flips bits in the genome based on a specified
     * mutation probability. The greater the mutation rate, the more likely
     * mutations will occur.
     *
     * @param rate mutation probability
     */
    public void mutate(double rate) {
        for (int i = 0; i < _genome.length; i++)
            if (Math.random() <= rate)
                _genome[i] = !_genome[i];
    }

    public boolean[] get_genome() {
        return _genome;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneticChromosome that = (GeneticChromosome) o;
        return Objects.equals(getGenotype(), that.getGenotype());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGenotype());
    }
}
