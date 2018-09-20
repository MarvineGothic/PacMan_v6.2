package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.pacman.BT.PacManBuilder.activePillsIndices;
import static pacman.entries.utils.Parameters.PILLS_THRESHOLD;

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
