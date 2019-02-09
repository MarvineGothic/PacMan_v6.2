package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestGhost;
import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestTarget;
import static pacman.entries.pacman.utils.Utils.getClosestEdibleGhost;

public class GetClosestEdibleGhost extends Node {
    @Override
    public void init() {
        closestGhost = getClosestEdibleGhost(game);
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
        closestTarget = game.getGhostCurrentNodeIndex(closestGhost);
    }
}
