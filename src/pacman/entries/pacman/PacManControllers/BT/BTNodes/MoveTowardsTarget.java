package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.game.Constants.DM;

import static pacman.entries.pacman.BehaviorTree.TreeBuilder.currentMove;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestTarget;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.pacManIdx;

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
