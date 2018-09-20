package pacman.entries.genetic;

import pacman.entries.GA.PacManGA;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import static pacman.entries.GA.PacManGA.*;
import static pacman.entries.utils.Utils.getTotalBits;
import static pacman.entries.utils.Utils.saveToFile;

public class GeneticPopulation implements Serializable {

    private static final long serialVersionUID = -4765878553574173154L;
    transient public GeneticGene[] genes;
    transient public PacManGA ga;
    public GeneticChromosome[] _population;

    public GeneticPopulation(PacManGA ga) {
        this.ga = ga;
        this.genes = ga.genes;
        int bits = getTotalBits(genes);
        _population = new GeneticChromosome[POP_SIZE];
        for (int i = 0; i < _population.length; i++)
            _population[i] = new GeneticChromosome(ga, bits);
        Arrays.sort(_population, new ChromosomeComparator());
    }

    /**
     * Creates a new GeneticPopulation from an existing chromosome array.
     * This is used by the evolvePopulation method to generate new generations.
     *
     * @param savedPopulation population
     */
    public GeneticPopulation(PacManGA ga, GeneticChromosome[] savedPopulation) {
        this.ga = ga;
        this.genes = ga.genes;
        _population = savedPopulation;
        Arrays.sort(_population, new ChromosomeComparator());
    }

    /**
     * This method evolves the population by one generation. It performs
     * elitism, selection, mating, and mutation.
     *
     * @return evolved population
     */
    public GeneticPopulation evolvePopulation() {
        GeneticChromosome[] next = new GeneticChromosome[_population.length];


        // Population size MUST be even, because we do everything in multiples of 2
        int index = (int) (_population.length * ELITISM);
        if (index % 2 != 0) index++;

        // Elitism: Copy the best elements in the population into the next generation.
        // Because the population is sorted, take elements between [0, index)
        System.arraycopy(_population, 0, next, 0, index);

        // While the next generation is not yet full, continue natural selection
        while (index < next.length) {
            // Select two parents using tournament selection
            GeneticChromosome p1 = next[0];
            GeneticChromosome p1a = select();

            GeneticChromosome p2 = next[1];
            GeneticChromosome p2a = select();

            // Mate the parents and mutate their offspring
            GeneticChromosome[] off = p1.multiPointCross(ga, p2, CROSS);
            off[0].mutate(ga, MUTATE);
            off[1].mutate(ga, MUTATE);

            // Put the offspring into the next generation and increment the counter
            System.arraycopy(off, 0, next, index, 2);
            index += 2;
            if (index >= next.length) break;

            // Mate the parents and mutate their offspring
            off = p1.multiPointCross(ga, p1a, CROSS);
            off[0].mutate(ga, MUTATE);
            off[1].mutate(ga, MUTATE);

            // Put the offspring into the next generation and increment the counter
            System.arraycopy(off, 0, next, index, 2);
            index += 2;
            if (index >= next.length) break;

            // Mate the parents and mutate their offspring
            off = p2.multiPointCross(ga, p2a, CROSS);
            off[0].mutate(ga, MUTATE);
            off[1].mutate(ga, MUTATE);

            // Put the offspring into the next generation and increment the counter
            System.arraycopy(off, 0, next, index, 2);
            index += 2;
            if (index >= next.length) break;
        }

        // Return a new generation of the population
        return new GeneticPopulation(ga, next);
    }

    public GeneticPopulation evolvePopulation2() {
        List<GeneticChromosome> nextG = new ArrayList<>(Arrays.asList(_population));

        double highScore = getBestChromosome().getFitness();
        double newScore = 0;
        boolean save = false;
        int count = 0;

        while (true) {
            //System.out.println("Size of population: " + nextG.size());
            for (int i = 0; i < 5; i++) {
                for (int j = i; j < 5 + i; j++) {
                    GeneticChromosome[] off = _population[i].multiPointCross(ga, _population[j], CROSS);
                    off[0].mutate(ga, MUTATE);
                    off[1].mutate(ga, MUTATE);

                    newScore = Math.min(off[0].getFitness(), off[1].getFitness());

                    if (newScore < highScore) {
                        highScore = newScore;
                        System.out.println("We got highScore: " + newScore);
                        save = true;
                        nextG.addAll(Arrays.asList(off));
                    }
                }
            }
            if (save) break;
        }


        System.out.println("We finished");
        nextG.sort(new ChromosomeComparator());

        GeneticChromosome[] next = new GeneticChromosome[POP_SIZE];
        for (int i = 0; i < next.length; i++) {
            next[i] = nextG.get(i);
        }

        Arrays.sort(next, new ChromosomeComparator());
        GeneticPopulation population = new GeneticPopulation(ga, next);
        saveToFile(GEN_POP, population);
        return population;
    }

    /**
     * This method performs tournament selection. Tournament selection involves
     * selecting a group of chromosomes and returning the chromosome with the
     * lowest fitness value.
     *
     * @return selected chromosome
     */
    private GeneticChromosome select() {

        GeneticChromosome winner = null;
        double min = Double.MAX_VALUE;

        for (int i = 0; i < TOURNAMENT_SIZE; i++) {
            int rand = (int) (Math.random() * _population.length);
            double fitness = _population[rand].getFitness();

            if (fitness < min) {
                winner = _population[rand];
                min = fitness;
            }
        }

        return winner;
    }

    /**
     * Returns the average fitness of the population.
     */
    double getAverageFitness() {
        double avg = 0.0;
        for (int i = 0; i < _population.length; i++)
            avg += _population[i].getFitness();
        return avg / _population.length;
    }

    /**
     * Returns the most fit chromosome in the population.
     */
    public GeneticChromosome getBestChromosome() {
        return _population[0];
    }

    /**
     * This class is responsible for comparing two chromosomes. It is used
     * by the evolvePopulation function to sort the population by their fitness values.
     *
     * @author ashwin
     */
    private class ChromosomeComparator implements Comparator<GeneticChromosome> {
        public int compare(GeneticChromosome o1, GeneticChromosome o2) {
            Double f1 = o1.getFitness();
            Double f2 = o2.getFitness();
            return f1.compareTo(f2);
        }
    }
}
