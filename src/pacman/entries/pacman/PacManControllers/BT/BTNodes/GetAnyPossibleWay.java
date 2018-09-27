package pacman.entries.pacman.PacManControllers.BT.BTNodes;

import pacman.entries.pacman.BehaviorTree.utils.Node;

import static pacman.entries.pacman.BehaviorTree.TreeBuilder.currentMove;
import static pacman.entries.pacman.utils.Utils.getPossibleMove;

public class GetAnyPossibleWay extends Node {


    @Override
    public void init() {
    }

    @Override
    public boolean successConditions() {
        return true;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }

    @Override
    public void doAction() {
        currentMove = getPossibleMove(game, game.getPacmanLastMoveMade(), true, true);
    }
}
