package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.game.Constants;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.pacman.BehaviorTree.TreeBuilder.currentMove;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestRunFromTargetIndex;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.pacManIdx;

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
