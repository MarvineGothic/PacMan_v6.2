package pacman.entries.BT.utils;

import pacman.controllers.Controller;
import pacman.game.Constants;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.List;

import static pacman.entries.BT.utils.Status.*;
import static pacman.entries.pacman.Utils.*;
import static pacman.game.Constants.MOVE.DOWN;

public abstract class Task extends Controller<MOVE> {
    protected static final int MIN_DISTANCE = 30;    //if a ghost is this close, run away
    protected static int PILLS_THRESHOLD;
    protected static Game game;
    protected static int closestTarget = -1;
    protected static MOVE move;
    protected static int gameLevel = -1;
    protected static int[] junctions;
    protected static int[] totalPills;
    protected static List<int[]> allPathsList = new ArrayList<>();
    protected static List<int[]> safePathsList = new ArrayList<>();
    protected Status status = SUCCESS; // could be not static!
    protected Task runningLeaf;         // could be not static!

    public void init() {
        int currentLevel = game.getCurrentLevel();
        if (currentLevel != gameLevel) {
            gameLevel = currentLevel;
            junctions = game.getJunctionIndices();
            allPathsList = getAllJunctionPaths(game, junctions);
            totalPills = game.getPillIndices();
            // pills threshold
            PILLS_THRESHOLD = getPillsThreshold(game, allPathsList);
        }
    }

    public abstract Status execute();

    public MOVE getMove(Game game, long timeDue) {
        Task.game = game;
        //System.out.println(lastMove);


        // if previous or very first session finished/started with SUCCESS or FAILURE then run the tree again
        if (status == SUCCESS || status == FAILURE)
            status = execute();
        else if (status.equals(RUNNING))
            status = runningLeaf.execute();

        // check new session, if it's new of course ;)

        if (status.equals(RUNNING))
            return move;
        else if (status.equals(SUCCESS)) return move;
        else{
            System.out.println("final move" + lastMove);
            return lastMove;}
    }

    /**
     * Override to do a pre-conditions check to
     * see if the task can be updated.
     *
     * @return True if it can, false if it can't
     */
    public abstract boolean checkConditions();

    /**
     * Override to specify the logic the task
     * must update each cycle
     */
    public abstract void doAction();
}
