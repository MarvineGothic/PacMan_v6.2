package pacman.entries.GA;

import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.entries.genetic.GeneticAlgorithm;
import pacman.entries.genetic.GeneticGene;
import pacman.entries.genetic.GeneticPopulation;
import pacman.entries.pacman.PacManWeightedGA_Ai;
import pacman.entries.utils.GameScore;
import pacman.game.Game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Properties;
import java.util.Random;

import static pacman.entries.utils.Utils.loadFromFile;
import static pacman.entries.utils.Utils.saveToFile;
import static pacman.game.Constants.DELAY;

public class PacManGA extends GeneticAlgorithm implements Serializable {

    private static final long serialVersionUID = 6169876546050658654L;

    public static int MAX_GEN;
    public static int NUMBER_OF_GENES;
    public static int GENE_SIZE;

    public  static int POP_SIZE;
    public static double CROSS;
    public static double MUTATE;
    public static double ELITISM;

    public static int TOURNAMENT_SIZE;


    public PacManGA(Properties properties) {
        // 1. Create array of Genes and initialize each gene with bits:
        GeneticGene[] genes = new GeneticGene[NUMBER_OF_GENES];
        for (int i = 0; i < genes.length; i++)
            genes[i] = new GeneticGene(GENE_SIZE, -3.0, 3.0);
        this.genes = genes;
        this.properties = properties;
    }

    public static void main(String[] args) throws IOException {

        // =========================== Load properties and initialize statics ==================================
        Properties props = new Properties();
        props.load(new FileInputStream(new File(GA_PROPERTIES)));
        props.load(new FileInputStream(new File(PACMAN_PROPERTIES)));
        props.setProperty("game.ga", GEN_POP);        // Point to the generated genetic population

        MAX_GEN = Integer.valueOf(props.getProperty("ga.maxgen"));
        NUMBER_OF_GENES = Integer.parseInt(props.getProperty("chromosome.gene.amount"));
        GENE_SIZE = Integer.parseInt(props.getProperty("chromosome.gene.size"));
        POP_SIZE = Integer.valueOf(props.getProperty("pop.size"));
        CROSS = Double.valueOf(props.getProperty("pop.cross"));
        MUTATE = Double.valueOf(props.getProperty("pop.mutate"));
        ELITISM = Double.valueOf(props.getProperty("pop.elitism"));
        TOURNAMENT_SIZE = Integer.valueOf(props.getProperty("tournament.size"));

        // =================================== Create new GeneticAlgorithm =======================================
        PacManGA pacManGA = new PacManGA(props);

        // =================================== Create initial population =========================================
        // There's an option to get population from last saved generation
        // If load from file is failed, then new population will be created
        GeneticPopulation initialPopulation;
        GeneticPopulation savedPopulation = (GeneticPopulation) loadFromFile(props.getProperty("game.ga"));
        if (savedPopulation != null)
            initialPopulation = new GeneticPopulation(pacManGA, savedPopulation._population);
        else
            initialPopulation = new GeneticPopulation(pacManGA);

        // ================================ Generate new Population ===============================================
        GeneticPopulation newPopulation = pacManGA.run(initialPopulation);

        // save it to file for next use
        saveToFile(GEN_POP, newPopulation);
    }
}
