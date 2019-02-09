package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.closestRunFromTargetIndex;
import static pacman.entries.pacman.utils.Parameters.MIN_DISTANCE_2;

public class CheckGhostMinDistance extends Node {

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
