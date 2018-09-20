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

import static pacman.entries.utils.Parameters.*;
import static pacman.entries.utils.Utils.*;

@SuppressWarnings("all")
public class SuperPacMan extends Controller<MOVE> {

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
        double pillsPerCent = (double) activePillsIndices.length * 100 / totalPills.length;


        int[] safePills = getSafeIndicesFromSafePaths(game, safeJunctionsNDE, true, PILLS_THRESHOLD, activePillsIndices.length);
        int[] anySafeIdx = getSafeIndicesFromSafePaths(game, junctions, false, 0, 0);

        int[] safePathToPower = getShortestSafePath(game, pacManIdx, activePowerPillsIndices, false);
        int[] safePathToPills = getShortestSafePath(game, pacManIdx, safePills, false);

        GameView.addPoints(game, Color.BLUE, safePills);
        List<int[]> safePaths = getSafeDistancePaths(50, game, pacManIdx, false);


        //Strategy 1: if any non-edible ghost is too close (less than P_MIN_DISTANCE), run away
        for (GHOST ghost : GHOST.values())
            if (isGhostDangerous(game, ghost)) {

                int closestGhostIdx = game.getGhostCurrentNodeIndex(ghost);
                int closestGhostDist = game.getShortestPathDistance(pacManIdx, closestGhostIdx);

                if (closestGhostDist < MIN_DISTANCE) {

                    // ================================== 1.1 ==============================================
                    // if there're some pills on a safe way AND closest ghost is still on some safe distance
                    // then try to collect more pills
                    // TODO: 20.09.2018 include check if more than one or two ghosts are chasing pacman then take Power instead
                    if (safePathToPills != null && closestGhostDist > MIN_DISTANCE_2) {
                        closestPill = targetIndexFromPath(safePathToPills);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    }
                    // ================================== 1.2 ==============================================
                    // if there's a safe way to Power ///and pills are less than 60%
                    else if (safePathToPower != null) {
                        int closestPower = targetIndexFromPath(safePathToPower);
                        GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
                    }
                    // ================================== 1.3 ==============================================
                    // if there are still some pills around
                    else if (safePathToPills != null) {
                        closestPill = targetIndexFromPath(safePathToPills);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    }
                    // ================================== 1.4 ==============================================
                    // find closest safe junction
                    else if (safeJunctionsNDE.length > 0) {
                        game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);

                        int closestJunction = getClosestJunctionWithGap(game, pacManIdx, safeJunctionsNDE, JUNCTION_GAP);

                        GameView.addPoints(game, Color.PINK, safeJunctionsNDE);
                        return game.getNextMoveTowardsTarget(pacManIdx, closestJunction, DM.PATH);
                    }
                    // ================================== 1.5 ==============================================
                    else {
                        GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, closestGhostIdx));
                        return game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);
                    }
                }
            }

        List<Integer> targets = new ArrayList<>();
        //Strategy 2: find the nearest edible ghost and go after them
        GHOST minGhost = null;
        //we found an edible ghost
        // But there must be some Powers left, in case of difficult paths with pills left
        if ((minGhost = getClosestEdibleGhost(game)) != null &&
                activePowerPillsIndices.length >= POWERS_LEFT) {

            int minGhostIndex = game.getGhostCurrentNodeIndex(minGhost);

            if (game.getDistance(pacManIdx, minGhostIndex, DM.PATH) < EDIBLE_GHOST_DISTANCE) {
                GameView.addPoints(game, Color.GRAY, game.getShortestPath(pacManIdx, minGhostIndex));
                return game.getNextMoveTowardsTarget(pacManIdx, minGhostIndex, DM.PATH);
            }
            // give a PacMan chance to chase some ghosts before finishing

            else if (pillsPerCent < PILLS_PERCENT) {
                GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, minGhostIndex));
                return game.getNextMoveTowardsTarget(pacManIdx, minGhostIndex, DM.PATH);
            }
        }


        // ================================== Strategy 3: go after pills: =====================================
        // ========================================================================================================


        // ==================================== 3.1 -- if any safe pills on the field: ===========================
        if (safePathToPills != null) {
            closestPill = targetIndexFromPath(safePathToPills);
            // check if closest pill is a Power and don't take it while there are still a lot of pills on the field:
            int powerIdx = game.getPowerPillIndex(closestPill);
            if (powerIdx >= 0 && game.isPowerPillStillAvailable(powerIdx) && activePillsIndices.length > PILLS_THRESHOLD) {
                return game.getPacmanLastMoveMade().opposite();
            }
            GameView.addPoints(game, Color.GREEN, game.getShortestPath(pacManIdx, closestPill));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
        }
        // =================================  3.2 -- =================================================================
        // ============= if there is a safe path to Power pill and no more active pills outside ===========================
        // ============= paths with power pills (mainly to the end of game): ===============================================
        else if (safePathToPower != null && activePillsIndices.length <= PILLS_THRESHOLD) {
            int closestPower = targetIndexFromPath(safePathToPower);
            //GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
        }
        // ==================================  3.3 -- if there are some junctions =========================================
        // ==================================  that has no Dead End so just wander around: ===============================
        else if (safeJunctionsNDE.length > 0) {
            int closestJunction = getClosestJunctionWithGap(game, pacManIdx, safeJunctionsNDE, JUNCTION_GAP);
            //GameView.addPoints(game, Color.PINK, safeJunctionsNDE);
            return game.getNextMoveTowardsTarget(pacManIdx, closestJunction, DM.PATH);
        }
        //
        else return getPossibleMove(game, game.getPacmanLastMoveMade(), true, true);
        //return the next direction once the closest target has been identified
        //return MOVE.LEFT;
    }
}