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


public class SuperPacMan extends Controller<MOVE> {
    private static final int MIN_DISTANCE = 40;    //if a ghost is this close, run away
    private static final double PILLS_THRESHOLD = 20.0;
    private List<Integer[]> safePathsList = new ArrayList<>();


    public MOVE getMove(Game game, long timeDue) {

        int pacManIdx = game.getPacmanCurrentNodeIndex();
        // pills and Power:
        int[] powerIdxs = game.getActivePowerPillsIndices();
        int[] pillsIdxs = game.getActivePillsIndices();
        int[] totalPills = game.getPillIndices();

        // pills percentage
        double pillsPerCent = pillsIdxs.length * 100 / totalPills.length;
        // safe junctions with no dead end
        int[] safeJunctionsNDE = getSafeJunctionsNDE(game, true);

        // get list of safe paths
        safePathsList = getAllSafeJunctionPaths(game, safeJunctionsNDE);
        int[] junctions = game.getJunctionIndices();
        int[] safeJunctions = safeJunctions(game);
        int[] safePills = getSafePoints(game, safePathsList, true, pillsPerCent, PILLS_THRESHOLD);
        int[] anySafeWay = getSafePoints(game, safePathsList, false, 0, 0);
        int[] safePathToPower = shortestSafePath(game, pacManIdx, powerIdxs);

        GameView.addPoints(game, Color.BLUE, safePills);

        //System.out.println(Arrays.toString(junctions));
        // System.out.println(Arrays.toString(game.getNeighbouringNodes(153, MOVE.LEFT)));

        //System.out.println(pillsPerCent);
        int closestPill;


        List<int[]> safePaths = safePaths(50, game, pacManIdx);
        // GameView.addPoints(game, Color.BLUE, safePills);


        //Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
        for (GHOST ghost : GHOST.values())
            if (isGhostDangerous(game, ghost)) {

                int closestGhostIdx = game.getGhostCurrentNodeIndex(ghost);
                int closestGhostDist = game.getShortestPathDistance(pacManIdx, closestGhostIdx);

                if (closestGhostDist < MIN_DISTANCE)

                    // if there're some pills on a safe way
                    if (safePills.length > 0 && (closestGhostDist > 5 || safePathToPower == null)) {
                        closestPill = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safePills, DM.PATH);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    } else if (safePills.length > 0) {
                        closestPill = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safePills, DM.PATH);
                        GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestPill));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
                    }
                    // if there's a safe way to Power and pills are less than 60%
                    else if (safePathToPower != null /*&& pillsPerCent <= PILLS_THRESHOLD*/) {
                        int closestPower = targetIndexFromPath(safePathToPower);
                        GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
                    } else if (safeJunctions.length > 0) {
                        int closestJunction = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safeJunctions, DM.PATH);
                        double minDist = Integer.MAX_VALUE;
                        for (int i = 0; i < safeJunctions.length; i++) {
                            int distance = game.getShortestPathDistance(pacManIdx, safeJunctions[i]);
                            if (distance < minDist && distance > 2) {
                                minDist = distance;
                                closestJunction = safeJunctions[i];
                            }
                        }
                        GameView.addPoints(game, Color.RED, safeJunctions);
                        return game.getNextMoveTowardsTarget(pacManIdx, closestJunction, DM.PATH);
                    }
                // if there're some safe paths at some distance
                    /*else if (!safePaths.isEmpty()) {
                        int distance = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safePaths.get(0), DM.PATH);
                        GameView.addPoints(game, Color.GRAY, game.getShortestPath(pacManIdx, distance));
                        return game.getNextMoveTowardsTarget(pacManIdx, distance, DM.PATH);
                    }*/
                // if there's any safe path at all
                    /*else if (anySafeWay.length > 0) {
                        int closestWayOut = game.getFarthestNodeIndexFromNodeIndex(pacManIdx, anySafeWay, DM.PATH);
                        GameView.addPoints(game, Color.MAGENTA, game.getShortestPath(pacManIdx, closestWayOut));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestWayOut, DM.PATH);
                    } */
                // just run away in opposite direction
                    /*else {
                        GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, closestGhostIdx));
                        return game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIdx, DM.PATH);
                    }*/
            }


        //Strategy 3: go after junctions:


        if (safePills.length > 0) {
            closestPill = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safePills, DM.PATH);
            // check if closest pill is a Power and don't take it while there are still a lot of pills on the field:
            int powerIdx = game.getPowerPillIndex(closestPill);
            if (powerIdx >= 0 && game.isPowerPillStillAvailable(powerIdx) && pillsPerCent > PILLS_THRESHOLD) {
                return game.getPacmanLastMoveMade().opposite();
            }
            GameView.addPoints(game, Color.GREEN, game.getShortestPath(pacManIdx, closestPill));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
        }

        /* else if (anySafeWay.length > 0) {
            // give another safe way
            closestPill = game.getFarthestNodeIndexFromNodeIndex(pacManIdx, anySafeWay, DM.PATH);
            GameView.addPoints(game, Color.PINK, anySafeWay);
            return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
        }*/
        else if (!safePaths.isEmpty()) {
            closestPill = game.getClosestNodeIndexFromNodeIndex(pacManIdx, safePaths.get(0), DM.PATH);
            GameView.addPoints(game, Color.CYAN, game.getShortestPath(pacManIdx, closestPill));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPill, DM.PATH);
        } else if (safePathToPower != null) {
            int closestPower = targetIndexFromPath(safePathToPower);
            GameView.addPoints(game, Color.ORANGE, game.getShortestPath(pacManIdx, closestPower));
            return game.getNextMoveTowardsTarget(pacManIdx, closestPower, DM.PATH);
        }
        //return the next direction once the closest target has been identified
        return MOVE.LEFT;
    }
}