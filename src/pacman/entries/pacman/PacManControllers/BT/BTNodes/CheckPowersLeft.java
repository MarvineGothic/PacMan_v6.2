package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.utils.Node;

import static pacman.entries.pacman.PacManControllers.BT.PacManBTBuilder.activePowerPillsIndices;
import static pacman.entries.pacman.utils.Parameters.POWERS_LEFT;
public class CheckPowersLeft extends Node {
    @Override
    public void init() {
        activePowerPillsIndices = game.getActivePowerPillsIndices();
    }

    @Override
    public boolean successConditions() {
        return activePowerPillsIndices.length >= POWERS_LEFT;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }
}
