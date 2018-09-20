package pacman.entries.BT;

import pacman.controllers.Controller;
import pacman.entries.BT.utils.Node;
import pacman.entries.utils.ParametricPhenotype;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import static pacman.entries.BT.utils.Node.Status;
import static pacman.entries.BT.utils.Node.Status.*;

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
    }

    public abstract TreeBuilder addPhenotype(ParametricPhenotype parametricPhenotype);

    public abstract void init();

    public abstract void constructTree();

    @Override
    public MOVE getMove(Game game, long timeDue) {
        TreeBuilder.game = game;
        Node.game = game;

        //System.out.println(lastMove);
        // if previous or very first session finished/started with SUCCESS or FAILURE then run the tree again
        if (status == SUCCESS || status == FAILURE)
            status = execute();
        else if (status.equals(RUNNING)) {
            System.out.println(status);
            status = runningNode.execute();
        }

        // check new session, if it's new of course ;)

        if (status.equals(RUNNING))
            return currentMove;
        else if (status.equals(SUCCESS)) return currentMove;
        else {
            //System.out.println("final currentMove" + lastMove);
            return lastMove;
        }
    }

    public Status execute() {
        init();
        return root.execute();
    }
}
