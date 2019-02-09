package pacman.entries.pacman.PacManControllers.GA;

import pacman.controllers.Controller;
import pacman.entries.pacman.GeneticAlgorithm.GeneticPopulation;
import pacman.entries.pacman.utils.DataTuple;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static pacman.entries.pacman.GeneticAlgorithm.GeneticAlgorithm.GA_PROPERTIES;
import static pacman.entries.pacman.utils.Utils.getProperties;
import static pacman.entries.pacman.utils.Utils.loadFromFile;

public class PMGAController extends Controller<MOVE> {
    private double[] phenoType;

    PMGAController(double[] phenoType) {
        this.phenoType = phenoType;
    }

    public PMGAController() {
        Properties props = getProperties(GA_PROPERTIES);

        GeneticPopulation savedPopulation = (GeneticPopulation) loadFromFile(props.getProperty("game.ga"));
        if (savedPopulation == null)
            throw new IllegalArgumentException("Error! Couldn't load saved population file!");

        System.out.println(1 / savedPopulation.getBestChromosome().getFitness());
        phenoType = savedPopulation.bestPhenoType;
    }

    /**
     * Finding a best move by a highest weight in outputs List
     *
     * @param game
     * @param outputs
     * @return
     */
    public static MOVE getPacManBestMove(Game game, List<Double> outputs) {
        MOVE bestMove = game.getPacmanLastMoveMade();
        int pos = game.getPacmanCurrentNodeIndex();
        double moveWeight = Double.MIN_VALUE;

        for (int i = 0; i < outputs.size(); i++) {
            MOVE move = MOVE.values()[i];
            int adjIdx = game.getNeighbour(pos, move);

            if (adjIdx != -1 && outputs.get(i) > moveWeight) {
                moveWeight = outputs.get(i);
                bestMove = move;
            }
        }
        return bestMove;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE dir = game.getPacmanLastMoveMade();
        DataTuple dataTuple = new DataTuple(game, dir);
        List<Double> inputs = dataTuple.getNormalizedData(0, 2, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24);
        List<Double> weightedMoves = getFourWeightedOutputs(inputs, phenoType);
        return getPacManBestMove(game, weightedMoves);
    }

    /**
     * Converting phenoType array to four weighted outputs for MOVE
     * Phenotype is divided by four (number of moves)
     * then each fraction together with inputs gives sum of products of corresponding values,
     * which results in a weighted MOVE value. It will be calculated four times, once per each
     * phenotype fraction and four weighted values will be returned as a List
     *
     * @param inputs
     * @param phenoType
     * @return
     */
    private List<Double> getFourWeightedOutputs(List<Double> inputs, double[] phenoType) {
        List<Double> fourOutputs = new ArrayList<>();

        int index = 0;
        int size = phenoType.length / 4;

        for (int i = 0; i < 4; i++) {
            double sum = 0;
            for (int j = 0; j < inputs.size(); j++)
                sum += inputs.get(j) * phenoType[j + index];
            fourOutputs.add(sum);
            index += size;
        }
        return fourOutputs;
    }
}
