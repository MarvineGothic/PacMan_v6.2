package pacman.entries.utils;

import pacman.entries.genetic.GeneticGene;
import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.internal.Node;

import java.io.*;
import java.util.*;

import static pacman.entries.utils.Parameters.GHOST_EDIBLE_TIME;

public class Utils {

    //============================================== GETTERS ===============================================

    // ---------------------------------------- PILLS AND INDICES ----------------------------------------------

    /**
     * Get all indices from maze except the middle
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
     * Get all indices from list of pill paths
     * If we consider a list of safe paths(i.e. both junctions are in safe distance from ghosts)
     * then all indices within those paths are also safe.
     *
     * @param game
     * @param junctions
     * @return
     */
    public static int[] getSafeIndicesFromSafePaths(Game game, int[] junctions, boolean pill, int pillsThreshold, int activePills) {
        ArrayList<Integer> safePath = new ArrayList<>();
        // add junctions indices just in case:
        for (int i = 0; i < junctions.length; i++) {
            if (pill && isPill(game, junctions[i])) safePath.add(junctions[i]);
            if (!pill) safePath.add(junctions[i]);
        }

        List<int[]> safePathsList = getAllJunctionPaths(game, junctions);

        for (int i = 0; i < safePathsList.size(); i++) {
            int[] path = safePathsList.get(i);
            for (int j = 0; j < path.length; j++) {
                // if we're looking for a path with pills
                if (pill && isPill(game, path[j]) &&
                        (!isPowerInPath(game, path) ||
                                (isPowerInPath(game, path) && (pillsThreshold >= activePills) && allGhostsNonEdible(game)))) {
                    safePath.add(path[j]);
                }
                // if we're looking for any path
                if (!pill) {
                    safePath.add(path[j]);
                }
            }
        }
        Collections.sort(safePath);
        return convertListToArray(safePath);
    }

    /**
     * Returns amount of pills located at paths with Power
     * It's for the sake of keeping Power to the end of level.
     * So until there are more pills on the field those pills with Powers will not
     * be target for Pac-Man
     *
     * @param game
     * @param pathsList
     * @return
     */
    public static int getPillsThreshold(Game game, List<int[]> pathsList) {
        int totalWeight = 0;
        for (int i = 0; i < pathsList.size(); i++) {
            if (isPowerInPath(game, pathsList.get(i))) {
                totalWeight += getPathPills(game, pathsList.get(i));
            }
        }
        return totalWeight;
    }

    /**
     * How many pills are there in the path
     *
     * @param game
     * @param path
     * @return
     */
    public static int getPathPills(Game game, int[] path) {
        int weight = 0;
        for (int i = 0; i < path.length; i++) {
            if (isPill(game, path[i]))
                weight++;
        }
        return weight;
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
    public static int getPathJunctions(Game game, int[] path) {
        int count = 0;
        for (int i = 0; i < path.length; i++) {
            if (game.isJunction(path[i])) count++;
        }
        return count;
    }

    /**
     * Returns array of safe indices from another array of indices
     *
     * @param game
     * @param idxs
     * @return
     */
    public static int[] getSafeIndices(Game game, int[] idxs, boolean ghostLastMove) {
        /*int[] idxs = game.getJunctionIndices();*/
        List<Integer> junctionIdxs = new ArrayList<>();
        for (int i = 0; i < idxs.length; i++) {
            if (isIndexSafe(game, idxs[i], ghostLastMove))
                junctionIdxs.add(idxs[i]);
        }
        return convertListToArray(junctionIdxs);
    }

    /**
     * Returns array of safe junctions.
     * If boolean noDeadEnd is true, then for safety reasons,
     * all those junctions will have more than one connection to another junction. It will mean
     * that no junction will be a dead end.
     *
     * @param game
     * @return
     */
    public static int[] getSafeJunctionsNDE(Game game, boolean noDeadEnd, boolean ghostLastMove) {
        int[] safeJunctions = getSafeIndices(game, game.getJunctionIndices(), ghostLastMove);
        List<Integer> result = new ArrayList<>();

        for (int i = 0; i < safeJunctions.length; i++) {
            int initialJunction = safeJunctions[i];

            MOVE[] initialJunctionSides = MOVE.values();
            int countTurns = 0;
            for (int j = 0; j < safeJunctions.length; j++) {
                int nextJunction = safeJunctions[j];
                if (initialJunction != nextJunction) {

                    MOVE[] possibleMoves = game.getPossibleMoves(initialJunction);
                    int[] foundPaths = new int[0]; // it keeps track of existing paths
                    for (MOVE move : initialJunctionSides) {
                        int[] shortestPath = game.getShortestPath(initialJunction, nextJunction, move);
                        if (getPathJunctions(game, shortestPath) <= 1 && shortestPath.length != 0 && foundPaths.length != shortestPath.length) {
                            foundPaths = shortestPath;
                            countTurns++;
                        }
                        // early exit:
                        if (foundPaths.length == possibleMoves.length) break;
                    }
                }
            }
            if (noDeadEnd && countTurns > 1) result.add(initialJunction);
            else if (!noDeadEnd) result.add(initialJunction);
        }
        return convertListToArray(result);
    }

    /**
     * Returns a closest dangerous ghost less than some distance from Pac-Man
     *
     * @param game
     * @param toIndex
     * @param distance
     * @return
     */
    public static GHOST getClosestGhost(Game game, int toIndex, int distance) {
        for (GHOST ghost : GHOST.values())
            if (isGhostDangerous(game, ghost)) {

                int closestGhostIdx = game.getGhostCurrentNodeIndex(ghost);
                int closestGhostDist = game.getShortestPathDistance(toIndex, closestGhostIdx);

                if (closestGhostDist < distance)
                    return ghost;
            }
        return null;
    }

    /**
     * @param game
     * @return
     */
    public static GHOST getClosestEdibleGhost(Game game) {
        int minDistance = Integer.MAX_VALUE;
        Constants.GHOST minGhost = null;

        for (Constants.GHOST ghost : Constants.GHOST.values()) {
            int ghostCurrentNodeIndex = game.getGhostCurrentNodeIndex(ghost);
            if (game.isGhostEdible(ghost)) {
                int distanceToClosestGhost = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), ghostCurrentNodeIndex);

                if (distanceToClosestGhost < minDistance) {
                    minDistance = distanceToClosestGhost;
                    minGhost = ghost;
                }
            }
        }
        return minGhost;
    }

    public static int getClosestJunctionWithGap(Game game, int pacManIdx, int[] junctionsArray, int gap){
        int closestJunction = game.getClosestNodeIndexFromNodeIndex(pacManIdx, junctionsArray, DM.PATH);

        // ========= additionally check if PacMan is at least on some distance from junction
        // ========== (to avoid glitches when PacMan is bouncing between two closest junctions)
        double minDist = Integer.MAX_VALUE;
        for (int i = 0; i < junctionsArray.length; i++) {
            int distance = game.getShortestPathDistance(pacManIdx, junctionsArray[i], game.getPacmanLastMoveMade());
            if (distance < minDist && distance > gap) {
                minDist = distance;
                closestJunction = junctionsArray[i];
            }
        }
        return closestJunction;
    }

    // -------------------------------------------------- PATHS AND MOVES -----------------------------------------------------

    /**
     * Returns a list of unique paths from junction to junction
     *
     * @param game
     * @return
     */
    public static List<int[]> getAllJunctionPaths(Game game, int[] junctions) {

        List<int[]> pathsList = new ArrayList<>();

        for (int i = 0; i < junctions.length; i++) {
            int initialJunction = junctions[i];
            //if (isIndexSafe(game, initialJunction)) {

            MOVE[] junctionSides = MOVE.values();

            for (int j = i + 1; j < junctions.length; j++) {
                int nextJunction = junctions[j];
                // if (isIndexSafe(game, nextJunction)) {

                int[] path = new int[0]; // it keeps track of existing paths
                for (MOVE move : junctionSides) {
                    int[] path2 = game.getShortestPath(initialJunction, nextJunction, move);
                    if (getPathJunctions(game, path2) <= 1 && path2.length != 0 && path.length != path2.length) {
                        path = path2;
                        pathsList.add(path);
                    }
                }
                //}
            }
            //}
        }
        return pathsList;
    }

    /**
     * Returns a shortest safe path from one point to another from array of indices
     *
     * @param game
     * @param fromNodeIndex
     * @param toIndices
     * @return
     */
    public static int[] getShortestSafePath(Game game, int fromNodeIndex, int[] toIndices, boolean ghostLastMove) {
        // if no more indices left:
        if (toIndices.length == 0) return null;
        // else continue:
        int[] path = null;

        for (int i = 0; i < toIndices.length; i++) {
            int[] shortestPath = game.getShortestPath(fromNodeIndex, toIndices[i], game.getPacmanLastMoveMade());

            if (isPathSafe(shortestPath, game, ghostLastMove)) {
                if (path == null) path = shortestPath;

                if (shortestPath.length < path.length) {
                    path = shortestPath;
                }
            }
        }

        return path;
    }

    /**
     * Returns a possible currentMove towards a junction if the path is safe
     *
     * @param game
     * @param lastMove
     * @return
     */
    public static MOVE getPossibleMove(Game game, MOVE lastMove, boolean pacmanLastMove, boolean ghostLastMove) {
        MOVE move = lastMove;
        int currentIndex = game.getPacmanCurrentNodeIndex();
        int[] path;

        MOVE[] possibleMoves = pacmanLastMove ? game.getPossibleMoves(currentIndex, lastMove) :
                game.getPossibleMoves(currentIndex);

        /*if (possibleMoves.length == 1) return possibleMoves[0];
        else*/
        if (possibleMoves.length > 1) {

            for (int i = 0; i < possibleMoves.length; i++) {
                path = getPathToJunction(game, currentIndex, possibleMoves[i]);
                if (isPathSafe(path, game, ghostLastMove)) {
                    int target = targetIndexFromPath(path);
                    return game.getNextMoveTowardsTarget(currentIndex, target, Constants.DM.PATH);
                }
            }
        }

        return move;
    }

    /**
     * Helping method for MOVE getPossibleMove()
     * It returns a path to next Junction
     *
     * @param game
     * @param fromIndex
     * @param moveTo
     * @return
     */
    private static int[] getPathToJunction(Game game, int fromIndex, MOVE moveTo) {
        List<Integer> pathIdxs = new ArrayList<>();
        int nextIndex = fromIndex;

        while (true) {
            if ((nextIndex = game.getNeighbour(nextIndex, moveTo)) != -1) {
                pathIdxs.add(nextIndex);
                if (game.isJunction(nextIndex)) break;
            } else break;
        }
        return convertListToArray(pathIdxs);
    }

    /**
     * Find safe paths in some distance:
     *
     * @param safeDistance
     * @param game
     * @param fromNodeIndex
     * @return
     */
    @Deprecated
    public static List<int[]> getSafeDistancePaths(int safeDistance, Game game, int fromNodeIndex, boolean ghostLastMove) {
        int[] allIndices = getAllIndices(game);
        List<int[]> paths = new ArrayList<>();

        // now find all indices that have distance to PacMan as defined in parameters:
        for (int i = 0; i < allIndices.length; i++) {
            int[] shortestPath = game.getShortestPath(fromNodeIndex, allIndices[i]);

            if (shortestPath.length - 1 == safeDistance && isPathSafe(shortestPath, game, ghostLastMove)) {
                paths.add(shortestPath);
            }
        }
        return paths;
    }

    //========================================== BOOL =========================================================

    /**
     * Checks if the path has Power Pill
     *
     * @param game
     * @param path
     * @return
     */
    public static boolean isPowerInPath(Game game, int[] path) {
        for (int i = 0; i < path.length; i++) {
            if (isPower(game, path[i])) return true;
        }
        return false;
    }

    public static boolean isPacManInPath(Game game, int[] path) {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == game.getPacmanCurrentNodeIndex()) return true;
        }
        return false;
    }


    /**
     * Check if there are enemies on the way
     *
     * @param path
     * @param game
     * @return
     */
    public static boolean isPathSafe(int[] path, Game game, boolean ghostLastMove) {
        if (path == null) return false;
        for (int i = 0; i < path.length; i++) {
            int nodeIdx = path[i];
            if (!isIndexSafe(game, nodeIdx, ghostLastMove))
                return false;
        }
        return true;
    }

    public static boolean isGhostInPath(int ghostIndex, int[] path) {
        for (int i = 0; i < path.length; i++) {
            if (path[i] == ghostIndex) return true;
        }
        return false;
    }

    /**
     * Checking index for safety:
     *
     * @param game
     * @param index
     * @return
     */
    public static boolean isIndexSafe(Game game, int index, boolean ghostLastMove) {
        int pacManIndex = game.getPacmanCurrentNodeIndex();
        int ghostIndex;

        int[] pacManIndexPath;
        int[] ghostIndexPath;

        for (GHOST ghost : GHOST.values()) {
            if (isGhostDangerous(game, ghost)) {
                ghostIndex = game.getGhostCurrentNodeIndex(ghost);

                pacManIndexPath = game.getShortestPath(pacManIndex, index);
                ghostIndexPath = !ghostLastMove ? game.getShortestPath(ghostIndex, index) :
                        game.getShortestPath(ghostIndex, index, game.getGhostLastMoveMade(ghost));


                if ((ghostIndexPath.length - pacManIndexPath.length) < 2)
                    return false;
            }
        }
        return true;
    }

    public static boolean isPill(Game game, int nodeIndex) {
        int pillIndex = game.getPillIndex(nodeIndex);
        return pillIndex > -1 && game.isPillStillAvailable(pillIndex);
    }

    public static boolean isPower(Game game, int nodeIndex) {
        int powerIdx = game.getPowerPillIndex(nodeIndex);
        return powerIdx > -1 && game.isPowerPillStillAvailable(powerIdx);
    }

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
                game.getGhostEdibleTime(ghostType) <= GHOST_EDIBLE_TIME //&&
                /*!isEdibleGhostReachable(game, ghostType)*/;
    }


    public static boolean allGhostsEdible(Game game) {
        for (GHOST ghost : GHOST.values()) {
            if (isGhostDangerous(game, ghost)) return false;
        }
        return true;
    }

    public static boolean allGhostsNonEdible(Game game) {
        for (GHOST ghost : GHOST.values()) {
            if (!isGhostDangerous(game, ghost)) return false;
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

    //========================================== SERIALIZATION =======================================================

    /**
     * Saves a Genetic Population to file. This method performs Object Serialization
     * using the Serialization interface provided by Java.
     *
     * @param fileName save location
     * @throws IOException write error
     */
    public static void saveToFile(String fileName, Object object) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(new File(fileName)))) {
            oos.writeObject(object);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Loads a GeneticPopulation from file. This method performs Object Deserialization
     * using the Serialization interface provided by Java.
     *
     * @param filename
     * @return
     * @throws IOException            read error
     * @throws ClassNotFoundException object serializer error
     */
    public static Object loadFromFile(String filename) {
        Object gen_pop ;
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(filename)))) {
            gen_pop = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            //e.printStackTrace();
            return null;
        }
        return gen_pop;
    }

    //========================================== GAME =======================================================


    /**
     * Get the total number of bits required to store the chromosomes genes.
     * This is used to construct the chromosomes that make up the initial population.
     *
     * @return total bits required to store the chromosome
     */
    public static int getTotalBits(GeneticGene[] _genes) {
        int total = 0;
        for (int i = 0; i < _genes.length; i++)
            total += _genes[i].getBits();
        return total;
    }


}
