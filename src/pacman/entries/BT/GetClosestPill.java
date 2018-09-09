package pacman.entries.BT;

import pacman.entries.BT.utils.Status;
import pacman.entries.BT.utils.Task;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.BT.utils.Status.FAILURE;
import static pacman.entries.BT.utils.Status.SUCCESS;
import static pacman.entries.pacman.Utils.*;

public class GetClosestPill extends Task {

    private int[] activePillsIndices;
    private int[] safeJunctionsNDE;
    private int[] safePills;
    private int[] safePathToPills;


    @Override
    public void init() {
        activePillsIndices = game.getActivePillsIndices();
        safeJunctionsNDE = getSafeJunctionsNDE(game, true,false);
        safePills = getSafeIndicesFromSafePaths(game, safeJunctionsNDE,
                true, PILLS_THRESHOLD, activePillsIndices.length);
        safePathToPills = getShortestSafePath(game, game.getPacmanCurrentNodeIndex(), safePills,false);
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
        return safePathToPills != null;
    }


    @Override
    public void doAction() {
        GameView.addPoints(game, Color.BLUE, safePills);
        closestTarget = targetIndexFromPath(safePathToPills);
    }
}
