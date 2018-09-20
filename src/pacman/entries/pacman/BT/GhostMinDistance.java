package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.pacman.BT.PacManBuilder.closestRunFromTargetIndex;
import static pacman.entries.utils.Parameters.MIN_DISTANCE_2;

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
