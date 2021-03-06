package pacman.entries.pacman.BehaviorTree;

import pacman.controllers.Controller;
import pacman.entries.pacman.BehaviorTree.Archetypes.Node;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import static pacman.entries.pacman.BehaviorTree.Archetypes.Node.Status;
import static pacman.entries.pacman.BehaviorTree.Archetypes.Node.Status.*;

@SuppressWarnings("all")
public abstract class TreeBuilder extends Controller<MOVE> {
    public static Node root;
    public static Game game;
    public static MOVE currentMove;
    public static int gameLevel = -1;
    public static int[] junctions;
    public static int[] totalPills;

    public static Status status = SUCCESS;
    public static Node runningNode;

    public TreeBuilder(Node root) {
        TreeBuilder.root = root;
        root.setTreeBuilder(this);
    }

    public TreeBuilder() {
        constructTree();
        Node.setTreeBuilder(this);
    }

    public abstract void init();

    public abstract void constructTree();

    @Override
    public MOVE getMove(Game game, long timeDue) {
        TreeBuilder.game = game;
        Node.game = game;

        // if previous or very first session finished/started with SUCCESS or FAILURE then train the tree again
        if (status == SUCCESS || status == FAILURE)
            status = execute();
        else if (status.equals(RUNNING)) {
            System.out.println(status);
            status = runningNode.execute();
        }

        if (status.equals(RUNNING))
            return currentMove;
        else if (status.equals(SUCCESS)) return currentMove;
        else
            return lastMove;

    }

    public Status execute() {
        init();
        return root.execute();
    }
}
