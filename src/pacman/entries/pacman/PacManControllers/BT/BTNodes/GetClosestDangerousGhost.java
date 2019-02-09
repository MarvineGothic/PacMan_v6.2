package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.*;
import static pacman.entries.pacman.utils.Parameters.MIN_DISTANCE;
import static pacman.entries.pacman.utils.Utils.getClosestGhost;

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
