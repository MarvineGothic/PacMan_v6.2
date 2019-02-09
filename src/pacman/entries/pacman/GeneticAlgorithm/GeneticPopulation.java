package pacman.entries.pacman.GeneticAlgorithm;

import java.io.Serializable;
import java.util.*;

import static pacman.entries.pacman.GeneticAlgorithm.GeneticAlgorithm.*;
import static pacman.entries.pacman.utils.Parameters.printParameters;
import static pacman.entries.pacman.utils.Utils.saveToFile;

public class GeneticPopulation implements Serializable {

    private static final long serialVersionUID = -4765878553574173154L;
    public HashSet<String> genomesHistory;
    transient public GeneticAlgorithm ga;
    public GeneticChromosome[] _population;
    public double[] bestPhenoType;
    private transient double highScore;
    private transient double newScore;
    private transient boolean save = false;

    public GeneticPopulation(GeneticAlgorithm ga) {
        this.ga = ga;
        this.genomesHistory = new HashSet<>();

        _population = new GeneticChromosome[POP_SIZE];
        for (int i = 0; i < _population.length; i++) {
            GeneticChromosome cr;
            do {
                cr = new GeneticChromosome(CHROMOSOME_LENGTH);
            } while (!evaluate(cr));
            _population[i] = cr;
        }
        Arrays.sort(_population, new ChromosomeComparator());

        bestPhenoType = ga.getPhenotype(getBestChromosome().getGenotype());
        saveToFile(GEN_POP, this);
    }

    public static GeneticPopulation GeneticPopulationFactory(GeneticAlgorithm ga, GeneticPopulation gp) {
        if (gp == null)
            return new GeneticPopulation(ga);
        else
            return gp.setGa(ga);
    }

    /**
     * This method mates this chromosome with a given chromosome. The specified
     * crossover rate determines the likelihood that single-point crossover will
     * occur between the two chromosomes. This method returns an array containing
     * two offspring.
     *
     * @param rate
     * @param one
     * @param two
     * @param singlePointCross
     * @return
     */
    public static GeneticChromosome[] multiPointCross(double rate, GeneticChromosome one, GeneticChromosome two, boolean singlePointCross) {

        if (one.getGenotype().equals(two.getGenotype())) {
            System.out.println("same initial");
            System.out.println(one.getGenotype());
            System.out.println(two.getGenotype());
        }

        int crossLines = new Random().nextInt(one.get_genome().length / 5);
        if (singlePointCross) crossLines = 1;
        boolean[] c1 = Arrays.copyOf(one.get_genome(), one.get_genome().length);
        boolean[] c2 = Arrays.copyOf(two.get_genome(), two.get_genome().length);


        for (int i = 0; i < crossLines; i++) {
            if (Math.random() <= rate) {
                int index = (int) (Math.random() * one.get_genome().length);
                System.arraycopy(one.get_genome(), 0, c2, 0, index);
                System.arraycopy(two.get_genome(), 0, c1, 0, index);
            }
        }

        return new GeneticChromosome[]{new GeneticChromosome(c1),
                new GeneticChromosome(c2)};
    }

    public GeneticPopulation setGa(GeneticAlgorithm ga) {
        this.ga = ga;
        return this;
    }

    /**
     * More greedy evolving approach.
     * It evolves population and checks every new offspring.
     * If it's better than previous, then it quits a while loop and saves new, better population
     * to the file.
     *
     * @return
     */
    public GeneticPopulation evolvePopulation() {
        List<GeneticChromosome> nextG = new ArrayList<>(Arrays.asList(_population));

        highScore = getBestChromosome().getFitness();
        newScore = 0;
        save = false;
        int iterations = 0;

        do {
            iterations++;
            for (int i = 0; i < 3; i++) {
                for (int j = i + 1; j < 3 + i; j++) {
                    // ============= CROSSOVER =============
                    GeneticChromosome one = _population[i];
                    GeneticChromosome two = _population[j];
                    if (one.getGenotype().equals(two.getGenotype())) {
                        System.out.println("same initial");
                        two.mutate(MUTATE);
                        System.out.println(one.getGenotype());
                        System.out.println(two.getGenotype());
                    }
                    GeneticChromosome[] off = multiPointCross(CROSS, one, two, false);
                    checkScoreAndMutate(off, nextG);

                    // ============= TOURNAMENT =============
                    /*if (!save) {
                        off = multiPointCross(CROSS, one, select(one), false);
                        checkScoreAndMutate(off, nextG);
                    }*/
                }
            }
            System.out.println(iterations);
        } while (!save && iterations < 1);


        System.out.print("We finished ");
        if (!save) System.out.printf("due to the limit of %d iterations\n", iterations);
        System.out.println(" Chromosome cash size: " + genomesHistory.size());

        // sort new generation and kill the worst chromosomes:
        nextG.sort(new ChromosomeComparator());
        GeneticChromosome[] next = new GeneticChromosome[POP_SIZE];
        for (int i = 0; i < next.length; i++) {
            next[i] = nextG.get(i);
        }

        _population = next;
        bestPhenoType = ga.getPhenotype(getBestChromosome().getGenotype());

        saveToFile(GEN_POP_TRAIN, this);
        HashSet<String> genomesHistoryCopy = genomesHistory;
        genomesHistory = new HashSet<>();
        saveToFile(GEN_POP, this);
        genomesHistory = genomesHistoryCopy;
        return this;
    }

    /**
     * Inner method for evolvePopulation()
     *
     * @param off
     * @param nextG
     */
    private void checkScoreAndMutate(GeneticChromosome[] off, List<GeneticChromosome> nextG) {
        evaluate(off[0]);
        evaluate(off[1]);

        // ============= FIRST CHECK: NO MUTATION =============
        newScore = Math.min(off[0].getFitness(), off[1].getFitness());
        if (newScore > highScore) {
            off[0].mutate(MUTATE);
            off[1].mutate(MUTATE);
        }
        evaluate(off[0]);
        evaluate(off[1]);

        // ============= SECOND CHECK: WITH MUTATION =============
        newScore = Math.min(off[0].getFitness(), off[1].getFitness());
        if (off[0].gameScore != null && off[0].gameScore.score > 0.6 * (1 / highScore))
            nextG.addAll(Collections.singletonList(off[0]));
        if (off[1].gameScore != null && off[1].gameScore.score > 0.6 * (1 / highScore))
            nextG.addAll(Collections.singletonList(off[1]));

        if (newScore < highScore) {
            highScore = newScore;
            System.out.println("We got highScore: " + newScore);
            printParameters();
            save = true;
        }
    }

    private boolean evaluate(GeneticChromosome off) {
        int size = genomesHistory.size();
        genomesHistory.add(off.getGenotype());
        if (size < genomesHistory.size()) {
            off.evaluateChromosome(ga);
            return true;
        }
        return false;
    }

    /**
     * This method performs tournament selection. Tournament selection involves
     * selecting a group of chromosomes and returning the chromosome with the
     * lowest fitness value.
     *
     * @return selected chromosome
     */
    private GeneticChromosome select(GeneticChromosome first) {

        GeneticChromosome winner = first;
        double min = Double.MAX_VALUE;

        while (winner.equals(first))
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
        for (GeneticChromosome a_population : _population) avg += a_population.getFitness();
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
