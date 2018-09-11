package pacman.entries.pacman.BT;

import pacman.entries.BT.utils.Node;

import static pacman.entries.BT.TreeBuilder.currentMove;
import static pacman.entries.pacman.Utils.getPossibleMove;

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
