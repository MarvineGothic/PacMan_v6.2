package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import pacman.game.Constants;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.pacman.BT.PacManBuilder.*;
import static pacman.entries.utils.Parameters.JUNCTION_GAP;
import static pacman.entries.utils.Utils.getClosestJunctionWithGap;
import static pacman.entries.utils.Utils.getSafeJunctionsNDE;

public class CheckSafeJunctionsNDE extends Node {

    @Override
    public void init() {
        safeJunctionsNDE = getSafeJunctionsNDE(game, true, false);
    }

    @Override
    public boolean successConditions() {
        return safeJunctionsNDE.length > 0;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {
        game.getNextMoveAwayFromTarget(pacManIdx, closestRunFromTargetIndex, Constants.DM.PATH);

        closestTarget = getClosestJunctionWithGap(game, pacManIdx, safeJunctionsNDE, JUNCTION_GAP);
        GameView.addPoints(game, Color.PINK, safeJunctionsNDE);
    }
}
