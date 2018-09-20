package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.pacman.BT.PacManBuilder.*;
import static pacman.entries.utils.Parameters.PILLS_THRESHOLD;
import static pacman.entries.utils.Utils.*;

public class GetClosestPill extends Node {


    @Override
    public void init() {
        activePillsIndices = game.getActivePillsIndices();
        safeJunctionsNDE = getSafeJunctionsNDE(game, true, false);
        safePills = getSafeIndicesFromSafePaths(game, safeJunctionsNDE,
                true, PILLS_THRESHOLD, activePillsIndices.length);
        safePathToPills = getShortestSafePath(game, game.getPacmanCurrentNodeIndex(), safePills, false);
    }

    @Override
    public boolean successConditions() {
        return safePathToPills != null;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }


    @Override
    public void doAction() {
        GameView.addPoints(game, Color.BLUE, safePills);
        closestTarget = targetIndexFromPath(safePathToPills);
    }
}
