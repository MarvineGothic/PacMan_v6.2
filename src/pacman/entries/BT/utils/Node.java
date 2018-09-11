package pacman.entries.BT.utils;

import pacman.entries.BT.TreeBuilder;
import pacman.game.Game;

import static pacman.entries.BT.utils.Node.Status.*;


public abstract class Node {
    public static TreeBuilder treeBuilder;
    public static Game game;


    public static void setTreeBuilder(TreeBuilder treeBuilder) {
        Node.treeBuilder = treeBuilder;
    }

    public abstract void init();

    public Status execute() {
        init();
        if (!successConditions())
            return FAILURE;
        doAction();
        /*if (runningConditions())
            return RUNNING;*/
        return SUCCESS;
    }


    public abstract boolean successConditions();

    public abstract boolean runningConditions();

    public abstract void doAction();

    public enum Status {
        SUCCESS, FAILURE, RUNNING
    }
}
