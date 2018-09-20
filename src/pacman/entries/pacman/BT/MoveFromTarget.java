package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import pacman.game.Constants;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.BT.TreeBuilder.currentMove;
import static pacman.entries.pacman.BT.PacManBuilder.closestRunFromTargetIndex;
import static pacman.entries.pacman.BT.PacManBuilder.pacManIdx;

public class MoveFromTarget extends Node {
    @Override
    public void init() {
        pacManIdx = game.getPacmanCurrentNodeIndex();
    }

    @Override
    public boolean successConditions() {
        if (game.wasPacManEaten()) return false;
        return closestRunFromTargetIndex > 0;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {
        GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, closestRunFromTargetIndex));
        currentMove = game.getNextMoveAwayFromTarget(pacManIdx, closestRunFromTargetIndex, Constants.DM.PATH);
    }
}
