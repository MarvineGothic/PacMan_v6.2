package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;

import java.util.ArrayList;

import static pacman.game.Constants.*;

/*
 * Pac-Man controller as part of the starter package - simply upload this file as a zip called
 * MyPacMan.zip and you will be entered into the rankings - as simple as that! Feel free to modify
 * it or to start from scratch, using the classes supplied with the original software. Best of luck!
 *
 * This controller utilises 3 tactics, in order of importance:
 * 1. Get away from any non-edible ghost that is in close proximity
 * 2. Go after the nearest edible ghost
 * 3. Go to the nearest pill/power pill
 */
public class StarterPacMan extends Controller<MOVE> {
    private static final int MIN_DISTANCE = 20;    //if a ghost is this close, train away

    public MOVE getMove(Game game, long timeDue) {
        int pacmanCurrentNodeIndex = game.getPacmanCurrentNodeIndex();


        //Strategy 1: if any non-edible ghost is too close (less than P_MIN_DISTANCE), train away
        for (GHOST ghost : GHOST.values())
            if (!game.isGhostEdible(ghost) &&
                    game.getGhostLairTime(ghost) == 0 &&
                    game.getShortestPathDistance(pacmanCurrentNodeIndex, game.getGhostCurrentNodeIndex(ghost)) < MIN_DISTANCE)

                return game.getNextMoveAwayFromTarget(pacmanCurrentNodeIndex, game.getGhostCurrentNodeIndex(ghost), DM.PATH);

        //Strategy 2: find the nearest edible ghost and go after them
        int minDistance = Integer.MAX_VALUE;
        GHOST minGhost = null;

        for (GHOST ghost : GHOST.values())
            if (game.getGhostEdibleTime(ghost) > 0) {
                int distance = game.getShortestPathDistance(pacmanCurrentNodeIndex, game.getGhostCurrentNodeIndex(ghost));

                if (distance < minDistance) {
                    minDistance = distance;
                    minGhost = ghost;
                }
            }

        if (minGhost != null)    //we found an edible ghost
            return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(minGhost), DM.PATH);

        //Strategy 3: go after the pills and power pills
        int[] pills = game.getPillIndices();
        int[] powerPills = game.getPowerPillIndices();

        ArrayList<Integer> targets = new ArrayList<Integer>();

        for (int i = 0; i < pills.length; i++)                    //check which pills are available
            if (game.isPillStillAvailable(i))
                targets.add(pills[i]);

        for (int i = 0; i < powerPills.length; i++)            //check with power pills are available
            if (game.isPowerPillStillAvailable(i))
                targets.add(powerPills[i]);

        int[] targetsArray = new int[targets.size()];        //convert from ArrayList to array

        for (int i = 0; i < targetsArray.length; i++)
            targetsArray[i] = targets.get(i);

        //return the next direction once the closest target has been identified
        return game.getNextMoveTowardsTarget(pacmanCurrentNodeIndex, game.getClosestNodeIndexFromNodeIndex(pacmanCurrentNodeIndex, targetsArray, DM.PATH), DM.PATH);
    }
}























