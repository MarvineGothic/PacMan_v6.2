package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import pacman.game.Constants;

import static pacman.entries.pacman.BT.PacManBuilder.closestTarget;
import static pacman.entries.pacman.BT.PacManBuilder.pacManIdx;
import static pacman.entries.utils.Parameters.EDIBLE_GHOST_DISTANCE;

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
