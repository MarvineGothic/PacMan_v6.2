package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.utils.Node;

import static pacman.entries.pacman.PacManControllers.BT.PacManBTBuilder.closestRunFromTargetIndex;
import static pacman.entries.pacman.utils.Parameters.MIN_DISTANCE_2;

public class GhostMinDistance extends Node {
    /*public int distance;

    public GhostMinDistance(int dist) {
        super();
        distance = dist;
    }*/

    @Override
    public void init() {

    }

    @Override
    public boolean successConditions() {
        return closestRunFromTargetIndex > MIN_DISTANCE_2;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }
}
