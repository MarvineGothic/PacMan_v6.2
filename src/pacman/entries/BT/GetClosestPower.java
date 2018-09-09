package pacman.entries.BT;

import pacman.entries.BT.utils.Status;
import pacman.entries.BT.utils.Task;

import static pacman.entries.BT.utils.Status.FAILURE;
import static pacman.entries.BT.utils.Status.SUCCESS;
import static pacman.entries.pacman.Utils.getShortestSafePath;
import static pacman.entries.pacman.Utils.targetIndexFromPath;

public class GetClosestPower extends Task {
    private int[] safePathToPower;
    private int[] activePowerPillsIndices;
    private int[] activePillsIndices;

    @Override
    public void init() {
        activePowerPillsIndices = game.getActivePowerPillsIndices();
        activePillsIndices = game.getActivePillsIndices();
        safePathToPower = getShortestSafePath(game, game.getPacmanCurrentNodeIndex(), activePowerPillsIndices,false);
    }

    @Override
    public Status execute() {
        init();
        if (!checkConditions())
            return FAILURE;
        doAction();
        return SUCCESS;
    }

    @Override
    public boolean checkConditions() {
        return safePathToPower != null && activePillsIndices.length <= PILLS_THRESHOLD;
    }

    @Override
    public void doAction() {
        closestTarget = targetIndexFromPath(safePathToPower);
    }
}
