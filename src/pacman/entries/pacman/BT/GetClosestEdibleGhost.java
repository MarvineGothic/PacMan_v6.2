package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.pacman.BT.PacManBuilder.closestGhost;
import static pacman.entries.pacman.BT.PacManBuilder.closestTarget;
import static pacman.entries.utils.Utils.getClosestEdibleGhost;

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
