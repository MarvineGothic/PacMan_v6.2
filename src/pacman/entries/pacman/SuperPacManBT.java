package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import static pacman.entries.pacman.Utils.*;

@SuppressWarnings("all")
public class SuperPacManBT extends Controller<MOVE> {
    private static final int MIN_DISTANCE = 30;    //if a ghost is this close, run away
    private static int PILLS_THRESHOLD;

    private int gameLevel = -1;
    private int[] junctions;
    private int[] totalPills;

    private int closestPill;

    private List<int[]> allPathsList = new ArrayList<>();
    private List<int[]> safePathsList = new ArrayList<>();


    public MOVE getMove(Game game, long timeDue) {

        // ======================== INIT GAME LEVEL AND VARIABLES =============================
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel) {
            gameLevel = currentLevel;
            junctions = game.getJunctionIndices();
            allPathsList = getAllJunctionPaths(game, junctions);
            totalPills = game.getPillIndices();
            // pills threshold
            PILLS_THRESHOLD = getPillsThreshold(game, allPathsList);
        }

        // =====================================================================================
        int pacManIdx = game.getPacmanCurrentNodeIndex();

        // pills, Power and indices:
        int[] activePowerPillsIndices = game.getActivePowerPillsIndices();
        int[] activePillsIndices = game.getActivePillsIndices();

        // safe junctions with no dead end
        int[] safeJunctionsNDE = getSafeJunctionsNDE(game, true, false);
        int[] safeJunctionsWDE = getSafeJunctionsNDE(game, false, false);
        int[] safeJunctions = getSafeIndices(game, junctions, false);

        // get lists of paths
        safePathsList = getAllJunctionPaths(game, safeJunctionsNDE);


        // add a safe path if pac man already went in:
        for (int i = 0; i < allPathsList.size(); i++) {
            if (isPacManInPath(game, allPathsList.get(i)))
                safePathsList.add(allPathsList.get(i));
        }


        // pills percentage
        double pillsPerCent = activePillsIndices.length * 100 / totalPills.length;


        int[] safePills = getSafeIndicesFromSafePaths(game, safeJunctionsNDE, true, PILLS_THRESHOLD, activePillsIndices.length);
        int[] anySafeIdx = getSafeIndicesFromSafePaths(game, junctions, false, 0, 0);

        int[] safePathToPower = getShortestSafePath(game, pacManIdx, activePowerPillsIndices, false);
        int[] safePathToPills = getShortestSafePath(game, pacManIdx, safePills, false);

        GameView.addPoints(game, Color.BLUE, safePills);

        List<int[]> safePaths = getSafeDistancePaths(50, game, pacManIdx, false);



        //Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
        for (GHOST ghost : GHOST.values())
            if (isGhostDangerous(game, ghost)) {

                int closestGhostIdx = game.getGhostCurrentNodeIndex(ghost);
                int closestGhostDist = game.getShortestPathDistance(pacManIdx, closestGhostIdx);

                if (closestGhostDist < MIN_DISTANCE) {


                    // if there're some pills on a safe way
                    if (safePathToPills != null && closestGhostDist > 20) {
                        closestPill = targetIndexFromPath(safePathToPills);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));

                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    }
                    // if there's a safe way to Power and pills are less than 60%
                    else if (safePathToPower != null) {
                        int closestPower = targetIndexFromPath(safePathToPower);
                        GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
                    } else if (safePathToPills != null) {
                        closestPill = targetIndexFromPath(safePathToPills);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    }
                    // find closest safe junction
                    else if (safeJunctionsNDE.length > 0) {
                        game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);

                        int closestJunction = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safeJunctionsNDE, DM.PATH);
                        double minDist = Integer.MAX_VALUE;
                        for (int i = 0; i < safeJunctionsNDE.length; i++) {
                            int distance = game.getShortestPathDistance(pacManIdx, safeJunctionsNDE[i], game.getPacmanLastMoveMade());
                            if (distance < minDist && distance > 2) {
                                minDist = distance;
                                closestJunction = safeJunctionsNDE[i];
                            }
                        }
                        GameView.addPoints(game, Color.PINK, safeJunctionsNDE);
                        return game.getNextMoveTowardsTarget(pacManIdx, closestJunction, DM.PATH);
                    } /*else if (junctions != null) {
                        game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);
                        for (int i=0; i<junctions.length;i++){

                        }

                    }*/ else {
                        GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, closestGhostIdx));
                        return game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);
                    }
                }
            }

        List<Integer> targets = new ArrayList<>();
        //Strategy 2: find the nearest edible ghost and go after them
        GHOST minGhost = null;
        if ((minGhost = getClosestEdibleGhost(game)) != null) {    //we found an edible ghost
            int minGhostIndex = game.getGhostCurrentNodeIndex(minGhost);
            if (isPill(game, minGhostIndex)) {
                GameView.addPoints(game, Color.GRAY, game.getShortestPath(pacManIdx, minGhostIndex));
                return game.getNextMoveTowardsTarget(pacManIdx, minGhostIndex, DM.PATH);
            }
            if (pillsPerCent > 20)
                targets.add(minGhostIndex);
            else {
                GameView.addPoints(game, Color.GRAY, game.getShortestPath(pacManIdx, minGhostIndex));
                return game.getNextMoveTowardsTarget(pacManIdx, minGhostIndex, DM.PATH);
            }
        }


        //Strategy 3: go after junctions:


        if (safePathToPills != null) {
            closestPill = targetIndexFromPath(safePathToPills);
            // check if closest pill is a Power and don't take it while there are still a lot of pills on the field:
            int powerIdx = game.getPowerPillIndex(closestPill);
            if (powerIdx >= 0 && game.isPowerPillStillAvailable(powerIdx) && activePillsIndices.length > PILLS_THRESHOLD) {
                return game.getPacmanLastMoveMade().opposite();
            }
            //GameView.addPoints(game, Color.GREEN, game.getShortestPath(pacManIdx, closestTarget));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
        } else if (safePathToPower != null && activePillsIndices.length <= PILLS_THRESHOLD) {
            int closestPower = targetIndexFromPath(safePathToPower);
            //GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
        } else if (safeJunctionsNDE.length > 0) {
            int closestJunction = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safeJunctionsNDE, DM.PATH);
            double minDist = Integer.MAX_VALUE;
            for (int i = 0; i < safeJunctionsNDE.length; i++) {
                int distance = game.getShortestPathDistance(pacManIdx, safeJunctionsNDE[i], game.getPacmanLastMoveMade());
                if (distance < minDist && distance > 2) {
                    minDist = distance;
                    closestJunction = safeJunctionsNDE[i];
                }
            }
            //GameView.addPoints(game, Color.PINK, safeJunctionsNDE);
            return game.getNextMoveTowardsTarget(pacManIdx, closestJunction, DM.PATH);
        }
        //return the next direction once the closest target has been identified
        return MOVE.LEFT;
    }
}