package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.activePillsIndices;
import static pacman.entries.pacman.utils.Parameters.PILLS_THRESHOLD;

public class CheckPillsThreshold extends Node {
    @Override
    public void init() {
        activePillsIndices = game.getActivePillsIndices();
    }

    @Override
    public boolean successConditions() {
        return activePillsIndices.length <= PILLS_THRESHOLD;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }
}
