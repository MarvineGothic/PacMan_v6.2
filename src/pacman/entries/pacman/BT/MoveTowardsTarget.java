package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import pacman.game.Constants.DM;

import static pacman.entries.BT.TreeBuilder.currentMove;
import static pacman.entries.pacman.BT.PacManBuilder.closestTarget;
import static pacman.entries.pacman.BT.PacManBuilder.pacManIdx;

public class MoveTowardsTarget extends Node {

    @Override
    public void init() {
        pacManIdx = game.getPacmanCurrentNodeIndex();
    }

    @Override
    public boolean successConditions() {

        if (game.wasPacManEaten()) return false;
        return closestTarget > -1;
    }

    @Override
    public boolean runningConditions() {
        return pacManIdx != closestTarget;
    }


    @Override
    public void doAction() {
        currentMove = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closestTarget, DM.PATH);
    }
}
