package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import static pacman.entries.pacman.BT.PacManBuilder.*;
import static pacman.entries.utils.Parameters.PILLS_PERCENT;

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
