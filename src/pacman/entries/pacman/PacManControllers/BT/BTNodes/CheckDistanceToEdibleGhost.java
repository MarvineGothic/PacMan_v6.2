package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.game.Constants;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestTarget;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.pacManIdx;
import static pacman.entries.pacman.utils.Parameters.EDIBLE_GHOST_DISTANCE;

public class CheckDistanceToEdibleGhost extends Node {
    @Override
    public void init() {

    }

    @Override
    public boolean successConditions() {
        return game.getDistance(pacManIdx, closestTarget, Constants.DM.PATH) < EDIBLE_GHOST_DISTANCE;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }
}
