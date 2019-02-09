package pacman.entries.pacman.PacManControllers.GA;

import pacman.controllers.Controller;
import pacman.controllers.examples.Legacy2TheReckoning;
import pacman.entries.pacman.GeneticAlgorithm.GeneticAlgorithm;
import pacman.entries.pacman.GeneticAlgorithm.GeneticPopulation;
import pacman.entries.pacman.PacManControllers.BT.PMBTController;
import pacman.entries.pacman.utils.GameScore;
import pacman.entries.pacman.utils.ParametricPhenotype;
import pacman.game.Constants;
import pacman.game.Game;

import java.util.Properties;
import java.util.Random;

import static pacman.entries.pacman.GeneticAlgorithm.GeneticPopulation.GeneticPopulationFactory;
import static pacman.entries.pacman.utils.Utils.*;

public class PMGATrainer extends GeneticAlgorithm {

    private PMGATrainer(Properties properties) {
        super(properties);
    }

    public static void main(String[] args) {

        // =========================== Load properties and initialize statics ==================================
        // Choose between a plain GAlgorithm and a parametric GAlgorithm:
        Properties props;
        if (USE_PARAMETRIC_PHENOTYPE)
            props = getProperties(PARAM_GA_PROPERTIES);
        else
            props = getProperties(GA_PROPERTIES);

        GEN_POP_TRAIN = props.getProperty("game.ga.train");
        GEN_POP = props.getProperty("game.ga");
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

        GeneticPopulation initialPopulation = GeneticPopulationFactory(PMGATrainer, (GeneticPopulation) loadFromFile(GEN_POP_TRAIN));

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

        // choice of controller based on GAlgorithm type: plain(weighted) vs parametric:
        Controller<Constants.MOVE> pacmanController = new PMGAController(getPhenotype(genotype));
        if (USE_PARAMETRIC_PHENOTYPE)
            pacmanController = new PMBTController(new ParametricPhenotype(genotype));

        while (!game.gameOver()) {
            game.advanceGame(pacmanController.getMove(game.copy(), -1),
                    new Legacy2TheReckoning().getMove(game.copy(), -1));
        }
        return new GameScore(game);
    }
}
