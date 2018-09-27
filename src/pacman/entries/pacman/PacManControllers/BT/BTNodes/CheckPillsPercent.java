package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.utils.Node;
import static pacman.entries.pacman.PacManControllers.BT.PacManBTBuilder.*;
import static pacman.entries.pacman.utils.Parameters.PILLS_PERCENT;

public class CheckPillsPercent extends Node {
    @Override
    public void init() {
        activePillsIndices = game.getActivePillsIndices();
        pillsPerCent = /*(double)*/ activePillsIndices.length * 100 / totalPills.length;
    }

    @Override
    public boolean successConditions() {
        return pillsPerCent < PILLS_PERCENT;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }
}
