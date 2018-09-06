package pacman.controllers.examples;

import pacman.controllers.Controller;
import pacman.game.Game;

import static pacman.game.Constants.DM;
import static pacman.game.Constants.MOVE;

/*
 * The Class NearestPillPacMan.
 */
public class NearestPillPacMan extends Controller<MOVE> {

    /* (non-Javadoc)
     * @see pacman.controllers.Controller#getMove(pacman.game.Game, long)
     */
    public MOVE getMove(Game game, long timeDue) {
        int currentNodeIndex = game.getPacmanCurrentNodeIndex();
        //return the next direction once the closest target has been identified
        return game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getClosestNodeIndexFromNodeIndex(currentNodeIndex, nearestTargets(game), DM.PATH), DM.PATH);
    }

    // calculate nearest pills and power pills:
    public static int[] nearestTargets(Game game) {
        //get all active pills
        int[] activePills = game.getActivePillsIndices();

        //get all active power pills
        int[] activePowerPills = game.getActivePowerPillsIndices();

        //create a target array that includes all ACTIVE pills and power pills
        int[] targetNodeIndices = new int[activePills.length + activePowerPills.length];

        for (int i = 0; i < activePills.length; i++)
            targetNodeIndices[i] = activePills[i];

        for (int i = 0; i < activePowerPills.length; i++)
            targetNodeIndices[activePills.length + i] = activePowerPills[i];
        return targetNodeIndices;
    }
}