package pacman.entries.pacman;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Game;
import pacman.game.GameView;
import pacman.game.internal.PathsCache;

import java.awt.*;
import java.util.List;

import static pacman.entries.pacman.Utils.*;
import static pacman.game.Constants.DM;
import static pacman.game.Constants.MOVE;

/*
 * The Class StarterNearestPillPacMan.
 */
public class StarterNearestPillPacMan extends Controller<MOVE> {
    private static final int MIN_DISTANCE = 30;    //if a ghost is this close, run away
    private static int mazeIndex;
    private static PathsCache pathsCache;



    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {

        mazeIndex = game.getMazeIndex();
        pathsCache = Game.caches[mazeIndex];
        int[] junctions = game.getJunctionIndices();

        int pacManIdx = game.getPacmanCurrentNodeIndex();
        int[] powerIdxs = game.getActivePowerPillsIndices();
        int[] pillsIdxs = game.getActivePillsIndices();

        int closestTargetIndex;
        List<int[]> safePaths = getSafeDistancePaths(50, game, pacManIdx);

        //Strategy 1: if any non-edible ghost is too close (less than MIN_DISTANCE), run away
        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            if (isGhostDangerous(game, ghost)) {

                int[] safePathToPower = getShortestSafePath(game, pacManIdx, powerIdxs);
                int[] safePathToPill = getShortestSafePath(game, pacManIdx, pillsIdxs);

                int closestGhostIndex = game.getGhostCurrentNodeIndex(ghost);
                int nearestGhostDist = game.getShortestPathDistance(pacManIdx, closestGhostIndex);


                if (nearestGhostDist < MIN_DISTANCE) {
                    //closestTargetIndex = safePaths.get(0)[safePaths.get(0).length - 1];
                    // try to find safe PowerPill first:
                    if (safePathToPower != null) {
                        closestTargetIndex = targetIndexFromPath(safePathToPower);
                        GameView.addPoints(game, Color.PINK, game.getShortestPath(pacManIdx, closestTargetIndex));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestTargetIndex, DM.PATH);
                    }
                    // if no access to PowerPill yet, try just a Pill:
                /*else if (safePathToPill != null) {
                    closestTargetIndex = targetIndexFromPath(safePathToPill);
                    GameView.addPoints(game, Color.WHITE, game.getShortestPath(pacManIdx, closestTargetIndex));
                    return game.getNextMoveTowardsTarget(pacManIdx, closestTargetIndex, DM.PATH);
                }*/
                    // if no any Pills left try any safe escape
                    /*else if (!safePaths.isEmpty()) {
                        closestTargetIndex = safePaths.get(0)[safePaths.get(0).length - 1];
                        GameView.addPoints(game, Color.BLUE, game.getShortestPath(pacManIdx, closestTargetIndex));
                        return game.getNextMoveTowardsTarget(pacManIdx, closestTargetIndex, DM.PATH);
                    }*/
                    // the last case: just run!
                    /*else {
                        GameView.addPoints(game, Color.RED, game.getShortestPath(pacManIdx, closestGhostIndex));
                        return game.getNextMoveAwayFromTarget(pacManIdx, closestGhostIndex, DM.PATH);
                    }*/
                } /*else if (safePathToPower != null){
                    int closestPowerIndex = targetIndexFromPath(safePathToPower);
                    int nearestPowerDist = game.getShortestPathDistance(pacManIdx, closestPowerIndex);
                    if (nearestGhostDist > nearestPowerDist) {
                        return game.getNextMoveAwayFromTarget(pacManIdx, nearestPowerDist, DM.PATH);
                    }}*/
            }
        }


        //Strategy 2: find the nearest edible ghost and go after them
        int minDistance = Integer.MAX_VALUE;
        Constants.GHOST minGhost = null;

        /*for (Constants.GHOST ghost : Constants.GHOST.values()) {
            int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(ghost);
            if (game.isGhostEdible(ghost) && (ghost == Constants.GHOST.INKY || ghost == Constants.GHOST.SUE)) {
                int distanceToClosestGhost = game.getShortestPathDistance(pacManIdx, ghostCurrentNodeIndex);

                if (distanceToClosestGhost < minDistance) {
                    minDistance = distanceToClosestGhost;
                    minGhost = ghost;
                }
            }
        }*/

        // need to chose closest safest point:
        int[] nearestTargets = pillsIdxs;
        closestTargetIndex = game.getClosestNodeIndexFromNodeIndex(pacManIdx, nearestTargets, DM.PATH);
        /*int[] nearestTargetPath = getShortestSafePath(game, pacManIdx, nearestTargets);
        if (nearestTargetPath != null)
            closestTargetIndex = targetIndexFromPath(nearestTargetPath);*/

        if (minGhost != null) {    //we found an edible ghost
            int distToGhost = game.getShortestPath(pacManIdx, game.getGhostCurrentNodeIndex(minGhost)).length - 1;
            int distToTarget = game.getShortestPath(pacManIdx, closestTargetIndex).length - 1;
            int minGhostIndex = game.getGhostCurrentNodeIndex(minGhost);
            //if ((distToGhost - distToTarget) < 20) {
            GameView.addPoints(game, Color.BLACK, game.getShortestPath(pacManIdx, minGhostIndex));
            return game.getNextMoveTowardsTarget(pacManIdx, minGhostIndex, DM.PATH);
            // }
        }


        int[] shortestSafePath = getShortestSafePath(game, pacManIdx, nearestTargets);
        if (shortestSafePath != null) closestTargetIndex = targetIndexFromPath(shortestSafePath);

        GameView.addPoints(game, Color.GREEN, game.getShortestPath(pacManIdx, closestTargetIndex));
        //return the next direction once the closest target has been identified
        return game.getNextMoveTowardsTarget(pacManIdx, closestTargetIndex, DM.PATH);
    }
}