package pacman.entries.BT;

import pacman.entries.BT.utils.Status;
import pacman.entries.BT.utils.Task;
import pacman.game.Constants.DM;

import static pacman.entries.BT.utils.Status.*;

public class MoveToDestination extends Task {

    @Override
    public boolean checkConditions() {
        if (game.wasPacManEaten()) return false;
        return closestTarget > -1;
    }


    @Override
    public void doAction() {
        move = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), closestTarget, DM.PATH);
    }

    @Override
    public void init() {

    }

    @Override
    public Status execute() {
        if (checkConditions()) {
           /* if (!game.isPillStillAvailable(game.getPillIndex(closestTarget))) {
                System.out.println("success");
                return SUCCESS;
            }*/

            doAction();

                //return RUNNING;
            return SUCCESS;
        } else return FAILURE;
    }
}
