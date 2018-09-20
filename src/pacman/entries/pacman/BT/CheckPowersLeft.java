package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.pacman.BT.PacManBuilder.activePowerPillsIndices;
import static pacman.entries.utils.Parameters.POWERS_LEFT;
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
