package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.game.GameView;

import java.awt.*;

import static pacman.entries.pacman.PacManControllers.BT.PMBTController.*;
import static pacman.entries.pacman.utils.Utils.*;

public class GetClosestPower extends Node {


    public void init() {
        activePowerPillsIndices = game.getActivePowerPillsIndices();
        //
        safePathToPower = getShortestSafePath(game, game.getPacmanCurrentNodeIndex(), activePowerPillsIndices, false);
        //if (!isPathSafe(safePathToPower, game, true)) safePathToPills = null;
    }

    @Override
    public boolean successConditions() {
        return safePathToPower != null;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {
        closestTarget = targetIndexFromPath(safePathToPower);
        GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestTarget));
        //System.out.println("Closest Power: " + closestTarget);
    }
}
