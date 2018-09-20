package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.entries.genetic.GeneticGene;
import pacman.entries.utils.GameData;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.List;

public class PacManWeightedGA_Ai extends Controller<MOVE> {
    public GeneticGene[] geneticGenes;
    public double[] phenoType;

    public PacManWeightedGA_Ai(GeneticGene[] geneticGenes, double[] phenoType) {
        this.geneticGenes = geneticGenes;
        this.phenoType = phenoType;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE dir = game.getPacmanLastMoveMade();
        GameData gameData = new GameData(game, dir);
        List<Double> inputs = gameData.getNormalizedData();
        List<Double> weightedMoves = getFourWeightedOutputs(inputs, phenoType);
        return getPacManBestMove(game, weightedMoves);
    }

    public List<Double> getFourWeightedOutputs(List<Double> inputs, double[] phenoType) {
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

    private MOVE getPacManBestMove(Game game, List<Double> outputs) {
        MOVE bestMove = game.getPacmanLastMoveMade();
        int pos = game.getPacmanCurrentNodeIndex();
        double fitness = Double.MIN_VALUE;

        for (int i = 0; i < outputs.size(); i++) {
            MOVE move = MOVE.values()[i];
            int adjIdx = game.getNeighbour(pos, move);

            if (adjIdx != -1 && outputs.get(i) > fitness) {
                fitness = outputs.get(i);
                bestMove = move;
            }
        }
        return bestMove;
    }
}
