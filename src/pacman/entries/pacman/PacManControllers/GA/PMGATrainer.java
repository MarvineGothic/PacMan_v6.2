package pacman.entries.pacman.PacManControllers.GA;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.entries.pacman.GeneticAlgorithm.GeneticAlgorithm;
import pacman.entries.pacman.GeneticAlgorithm.GeneticGene;
import pacman.entries.pacman.GeneticAlgorithm.GeneticPopulation;
import pacman.entries.pacman.PacManControllers.BT.PacManBTBuilder;
import pacman.entries.pacman.utils.GameScore;
import pacman.entries.pacman.utils.ParametricPhenotype;
import pacman.game.Constants;
import pacman.game.Game;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Random;

import static pacman.entries.pacman.utils.Utils.loadFromFile;
import static pacman.entries.pacman.utils.Utils.saveToFile;

public class PMGATrainer extends GeneticAlgorithm {

    public static int NUMBER_OF_GENES;
    public static int GENE_SIZE;


    public PMGATrainer(Properties properties) {
        // 1. Create array of Genes and initialize each gene with bits:
        GeneticGene[] genes = new GeneticGene[NUMBER_OF_GENES];
        for (int i = 0; i < genes.length; i++)
            genes[i] = new GeneticGene(GENE_SIZE, -3.0, 3.0);
        this.genes = genes;
        this.properties = properties;
    }

    public static void main(String[] args) throws IOException {

        // =========================== Load properties and initialize statics ==================================
        // Choice between a plain GAlgorithm and a parametric GAlgorithm:
        Properties props = new Properties();
        if (USE_PARAMETRIC_PHENOTYPE)
            props.load(new FileInputStream(new File(PARAM_GA_PROPERTIES)));
        else
            props.load(new FileInputStream(new File(GA_PROPERTIES)));

        GEN_POP = props.getProperty("game.ga");
        //props.load(new FileInputStream(new File(PACMAN_PROPERTIES)));
        //props.setProperty("game.ga", GEN_POP);        // Point to the generated GeneticAlgorithm population

        MAX_GEN = Integer.valueOf(props.getProperty("ga.maxgen"));
        NUMBER_OF_GENES = Integer.parseInt(props.getProperty("chromosome.gene.amount"));
        GENE_SIZE = Integer.parseInt(props.getProperty("chromosome.gene.size"));
        POP_SIZE = Integer.valueOf(props.getProperty("pop.size"));
        CROSS = Double.valueOf(props.getProperty("pop.cross"));
        MUTATE = Double.valueOf(props.getProperty("pop.mutate"));
        ELITISM = Double.valueOf(props.getProperty("pop.elitism"));
        TOURNAMENT_SIZE = Integer.valueOf(props.getProperty("tournament.size"));

        // =================================== Create new GeneticAlgorithm =======================================
        PMGATrainer PMGATrainer = new PMGATrainer(props);

        // =================================== Create initial population =========================================
        // There's an option to get population from last saved generation
        // If load from file is failed, then new population will be created
        GeneticPopulation initialPopulation;

        GeneticPopulation savedPopulation = (GeneticPopulation) loadFromFile(props.getProperty("game.ga"));
        if (savedPopulation != null)
            initialPopulation = new GeneticPopulation(PMGATrainer, savedPopulation._population);
        else
            initialPopulation = new GeneticPopulation(PMGATrainer);

        // ================================ Generate new Population ===============================================
        GeneticPopulation newPopulation = PMGATrainer.run(initialPopulation);

        // save it to file for next use
        saveToFile(GEN_POP, newPopulation);
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
        // GameView gv = new GameView(game).showGame();

        // choice of controller based on GAlgorithm type: plain(weighted) vs parametric:
        Controller<Constants.MOVE> pacmanController = new PMWGAController(genes, getPhenotype(genotype));
        if (USE_PARAMETRIC_PHENOTYPE)
            pacmanController = new PacManBTBuilder(new ParametricPhenotype(genotype));

        while (!game.gameOver()) {
            game.advanceGame(pacmanController.getMove(game.copy(), -1),
                    new Legacy2TheReckoning().getMove(game.copy(), -1));

            /*try {
                Thread.sleep(5);
            } catch (Exception e) {
                System.out.println("RunGame Delay Exception");
            }*/
        }
        //gv.repaint();
        return new GameScore(game);
    }
}
