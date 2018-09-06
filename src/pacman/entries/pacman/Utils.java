package pacman.entries.pacman;

import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Utils {

    //============================================== GETTERS ===============================================

    /**
     * Get all indices from maze
     *
     * @param game
     * @return
     */
    public static int[] getAllIndices(Game game) {
        Node[] graphs = game.getCurrentMaze().graph;
        int[] result = new int[graphs.length];

        for (int i = 0; i < graphs.length - 1; i++) {
            result[i] = graphs[i].nodeIndex;
        }
        return result;
    }

    /**
     * Get all points from list of safe paths
     *
     * @param game
     * @param safePathsList
     * @return
     */
    public static int[] getSafePoints(Game game, List<Integer[]> safePathsList, boolean safe, double pillsPerCent, double threshold) {
        ArrayList<Integer> safePath = new ArrayList<>();
        for (int i = 0; i < safePathsList.size(); i++) {
            int[] path = IntegerTointArray(safePathsList.get(i));
            for (int j = 0; j < path.length; j++) {
                int pillIndex = game.getPillIndex(path[j]);
                // if we're looking for a safe path with pills
                if (safe &&
                        pillIndex >= 0 &&
                        game.isPillStillAvailable(pillIndex) &&
                        (!isPowerInPath(game, path) || (isPowerInPath(game, path) && pillsPerCent <= threshold))
                        ) {
                    safePath.add(path[j]);
                }
                // if we're looking for any safe path
                if (!safe) {
                    safePath.add(path[j]);
                }
            }
        }
        Collections.sort(safePath);
        return convertListToArray(safePath);
    }

    /**
     * How many pills are there in the path
     *
     * @param game
     * @param path
     * @return
     */
    public static int getPathsWeight(Game game, int[] path) {
        int weight = 0;
        for (int i = 0; i < path.length; i++) {
            int pillIndex = game.getPillIndex(path[i]);
            if (pillIndex >= 0 && game.isPillStillAvailable(pillIndex))
                weight++;
        }
        return weight;
    }

    /**
     * Checks if the path has Power Pill
     *
     * @param game
     * @param path
     * @return
     */
    public static boolean isPowerInPath(Game game, int[] path) {
        for (int i = 0; i < path.length; i++) {
            int powerIdx = game.getPowerPillIndex(path[i]);
            if (powerIdx >= 0 && game.isPowerPillStillAvailable(powerIdx)) return true;
        }
        return false;
    }

    /**
     * Calculates junction connections
     *
     * @param game
     * @param junctionIdx
     * @return
     */
    public static int getJunctionType(Game game, int junctionIdx) {
        return game.getNeighbouringNodes(junctionIdx).length;
    }

    /**
     * Counts all junctions in the path
     *
     * @param game
     * @param path
     * @return
     */
    public static int countJunctions(Game game, int[] path) {
        int count = 0;
        for (int i = 0; i < path.length; i++) {
            if (game.isJunction(path[i])) count++;
        }
        return count;
    }

    public static int[] safeJunctions(Game game) {
        int[] junctions = game.getJunctionIndices();
        List<Integer> junctionIdxs = new ArrayList<>();
        for (int i = 0; i < junctions.length; i++) {
            if (isJunctionSafe(game, junctions[i]))
                junctionIdxs.add(junctions[i]);
        }
        return convertListToArray(junctionIdxs);
    }

    /**
     * Get paths from junction to junction without repeating paths and junctions
     *
     * @param game
     * @return
     */
    public static List<Integer[]> getAllSafeJunctionPaths(Game game, int[] safeJunctions) {

        List<Integer[]> pathsList = new ArrayList<>();

        for (int i = 0; i < safeJunctions.length; i++) {
            int initialJunction = safeJunctions[i];
            if (isJunctionSafe(game, initialJunction)) {

                MOVE[] junctionSides = game.getPossibleMoves(initialJunction);

                for (int j = i + 1; j < safeJunctions.length; j++) {
                    int nextJunction = safeJunctions[j];
                    if (isJunctionSafe(game, nextJunction)) {

                        Integer[] path = new Integer[0]; // it keeps track of existing paths
                        for (MOVE move : junctionSides) {
                            int[] path2 = game.getShortestPath(initialJunction, nextJunction, move);
                            if (countJunctions(game, path2) <= 1 && path2.length != 0 && path.length != path2.length) {
                                path = intToIntegerArray(path2);
                                pathsList.add(path);
                            }
                        }
                    }
                }
            }
        }
        return pathsList;
    }

    /**
     *
     *
     * @param game
     * @return
     */
    public static int[] getSafeJunctionsNDE(Game game, boolean noDeadEnd) {
        int[] safeJunctions = safeJunctions(game);
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < safeJunctions.length; i++) {
            int initialJunction = safeJunctions[i];

            MOVE[] initialJunctionSides = game.getPossibleMoves(initialJunction);
            int countTurns = 0;
            for (int j = 0; j < safeJunctions.length; j++) {
                int nextJunction = safeJunctions[j];
                if (initialJunction != nextJunction) {

                    int[] existingPaths = new int[0]; // it keeps track of existing paths
                    for (MOVE move : initialJunctionSides) {
                        int[] shortestPath = game.getShortestPath(initialJunction, nextJunction, move);
                        if (countJunctions(game, shortestPath) <= 1 && shortestPath.length != 0 && existingPaths.length != shortestPath.length) {
                            existingPaths = shortestPath;
                            countTurns++;
                        }
                    }
                }
            }
            if (noDeadEnd && countTurns > 1) result.add(initialJunction);
            else if (!noDeadEnd) result.add(initialJunction);
        }
        return convertListToArray(result);
    }

    //========================================== SAFETY =========================================================

    /**
     * Evaluates if Edible Ghost is still Edible by the time when Pac-Man can reach it
     *
     * @param game
     * @param ghostType
     * @return
     */
    public static boolean isEdibleGhostReachable(Game game, GHOST ghostType) {
        double edibleTime = game.getGhostEdibleTime(ghostType);
        double pacManGhostDist = game.getDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(ghostType), DM.PATH);
        return !(edibleTime <= pacManGhostDist);
    }

    /**
     * Check if ghost is dangerous
     *
     * @param game
     * @param ghostType
     * @return
     */
    public static boolean isGhostDangerous(Game game, GHOST ghostType) {
        return !game.isGhostEdible(ghostType) &&
                game.getGhostLairTime(ghostType) == 0 &&
                game.getGhostEdibleTime(ghostType) < 2 &&
                !isEdibleGhostReachable(game, ghostType);
    }

    /**
     * Find safe paths in some distance:
     *
     * @param safeDistance
     * @param game
     * @param fromNodeIndex
     * @return
     */
    public static List<int[]> safePaths(int safeDistance, Game game, int fromNodeIndex) {
        int[] allIndices = getAllIndices(game);
        List<int[]> paths = new ArrayList<>();

        // now find all indices that have distance to PacMan as defined in parameters:
        for (int i = 0; i < allIndices.length; i++) {
            int[] shortestPath = game.getShortestPath(fromNodeIndex, allIndices[i]);

            if (shortestPath.length - 1 == safeDistance && isPathSafe(shortestPath, game)) {
                paths.add(shortestPath);
            }
        }
        return paths;
    }

    public static int[] shortestSafePath(Game game, int fromNodeIndex, int[] toIndices) {
        // if no more indices left:
        if (toIndices.length == 0) return null;
        // else continue:
        int[] path = null;

        for (int i = 0; i < toIndices.length; i++) {
            int[] shortestPath = game.getShortestPath(fromNodeIndex, toIndices[i]);

            if (isPathSafe(shortestPath, game)) {
                if (path == null) path = shortestPath;

                else if (shortestPath.length < path.length) {
                    path = shortestPath;
                }
            }
        }

        List<int[]> safePaths = safePaths(50, game, fromNodeIndex);
        if (path == null && !safePaths.isEmpty()) {
            path = safePaths.get(0);
        }
        return path;
    }


    /**
     * Check if there are enemies on the way
     *
     * @param path
     * @param game
     * @return
     */
    public static boolean isPathSafe(int[] path, Game game) {
        for (int i = 0; i < path.length; i++) {
            for (GHOST ghost : GHOST.values()) {
                if (isGhostDangerous(game, ghost)) {
                    int ghostIdx = game.getGhostCurrentNodeIndex(ghost);
                    int pacManIdx = game.getPacmanCurrentNodeIndex();
                    int nodeIdx = path[i];

                    // check if it's a junction and it's safe:
                    if (game.isJunction(nodeIdx)) {
                        if (!isJunctionSafe(game, nodeIdx))
                            return false;
                    }

                    int ghostToNodeDist = game.getShortestPath(ghostIdx, nodeIdx).length - 1;
                    int pacManToNodeDist = game.getShortestPath(pacManIdx, nodeIdx).length - 1;

                    if (nodeIdx == ghostIdx || ghostToNodeDist <= pacManToNodeDist) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Checking junction for safety:
     *
     * @param game
     * @param junctionIndex
     * @return
     */
    public static boolean isJunctionSafe(Game game, int junctionIndex) {
        int pacManIndex = game.getPacmanCurrentNodeIndex();
        int ghostIndex;

        int pacManJunctionDistance;
        int ghostJunctionDistance;

        for (GHOST ghost : GHOST.values()) {
            if (isGhostDangerous(game, ghost)) {
                ghostIndex = game.getGhostCurrentNodeIndex(ghost);
                pacManJunctionDistance = game.getShortestPathDistance(pacManIndex, junctionIndex);
                ghostJunctionDistance = game.getShortestPathDistance(ghostIndex, junctionIndex);
                if ((ghostJunctionDistance - pacManJunctionDistance) < 3) return false;
            }
        }
        return true;
    }


    //========================================== CONVERTERS =======================================================

    /**
     * Just a converter from List to Array
     *
     * @param list
     * @return
     */
    public static int[] convertListToArray(List<Integer> list) {
        int[] result = new int[list.size()];
        for (int i = 0; i < list.size(); i++) {
            result[i] = list.get(i);
        }
        return result;
    }

    public static int targetIndexFromPath(int[] path) {
        if (path == null) return -1;
        return path[path.length - 1];
    }

    public static Integer[] intToIntegerArray(int[] array) {
        Integer[] integers = new Integer[array.length];
        for (int i = 0; i < integers.length; i++)
            integers[i] = array[i];
        return integers;
    }

    public static int[] IntegerTointArray(Integer[] array) {
        int[] integers = new int[array.length];
        for (int i = 0; i < integers.length; i++)
            integers[i] = array[i];
        return integers;
    }
}
