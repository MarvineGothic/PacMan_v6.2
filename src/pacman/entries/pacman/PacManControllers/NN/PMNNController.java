package pacman.entries.pacman.PacManControllers.NN;

import pacman.controllers.Controller;
import pacman.entries.pacman.NeuralNetwork.NeuralNet;
import pacman.entries.pacman.NeuralNetwork.StateActionPair;
import pacman.entries.pacman.utils.DataTuple;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.Collections;
import java.util.List;

import static pacman.entries.pacman.PacManControllers.GA.PMGAController.getPacManBestMove;
import static pacman.entries.pacman.PacManControllers.NN.PMNNTrainer.createSAP;
import static pacman.entries.pacman.utils.Utils.*;

public class PMNNController extends Controller<MOVE> {

    public NeuralNet neuralNet;

    public PMNNController() {

        neuralNet = (NeuralNet) loadFromFile(getProperties(PACMAN_PROPERTIES).getProperty("game.neural"));
        if (neuralNet == null) {
            System.out.println("Error! PacMan Neural Net is not ready.");
            System.exit(0);
        }
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE dir = game.getPacmanLastMoveMade();
        DataTuple dataTuple = new DataTuple(game, dir);
        StateActionPair sap = createSAP(dataTuple);
        List<Double> outputs = neuralNet.test(Collections.singletonList(sap));

        return getPacManBestMove(game, outputs);
    }
}
