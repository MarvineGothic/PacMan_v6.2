package pacman.entries.BT;

import pacman.entries.BT.utils.Status;
import pacman.entries.BT.utils.Task;

import static pacman.entries.BT.utils.Status.SUCCESS;
import static pacman.entries.pacman.Utils.getPossibleMove;

public class GetAnyPossibleWay extends Task {
    @Override
    public Status execute() {
        doAction();
        //if (closestTarget == -1) return FAILURE;
        return SUCCESS;
    }

    @Override
    public boolean checkConditions() {
        return false;
    }

    @Override
    public void doAction() {
        move = getPossibleMove(game, game.getPacmanLastMoveMade(), true, true);
    }
}
