package pacman.entries.pacman.PacManControllers;

import pacman.controllers.Controller;
import pacman.entries.pacman.Montecarlo.MCTSSimulator;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;

public class MCTSPMController extends Controller<MOVE> {
    private Controller<EnumMap<Constants.GHOST, MOVE>> ghostController;

    public MCTSPMController(Controller<EnumMap<Constants.GHOST, MOVE>> ghostController) {
        this.ghostController = ghostController;
    }

    @Override
    public MOVE getMove(Game game, long timeDue) {
        MOVE move = new MCTSSimulator(game.copy(), timeDue, ghostController).MCTreeSearch();
        return move;
    }

}
