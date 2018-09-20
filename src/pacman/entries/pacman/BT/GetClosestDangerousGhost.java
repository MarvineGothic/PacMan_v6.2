package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.pacman.BT.PacManBuilder.*;
import static pacman.entries.utils.Parameters.MIN_DISTANCE;
import static pacman.entries.utils.Utils.getClosestGhost;

public class GetClosestDangerousGhost extends Node {


    @Override
    public void init() {
        pacManIdx = game.getPacmanCurrentNodeIndex();
        closestGhost = getClosestGhost(game, pacManIdx, MIN_DISTANCE);
    }

    @Override
    public boolean successConditions() {
        return closestGhost != null;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {
        closestRunFromTargetIndex = game.getGhostCurrentNodeIndex(closestGhost);
        closestGhostDist = game.getShortestPathDistance(pacManIdx, closestRunFromTargetIndex);
    }
}
