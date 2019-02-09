package pacman.entries.pacman.Montecarlo;

import pacman.controllers.Controller;
import pacman.controllers.examples.StarterPacMan;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.EnumMap;
import java.util.Stack;

public class MCTSSimulator {

    public static final int MAX_SIMULATION_LENGTH = 1000;
    private static MCTSNode rootMCTSNode;
    private Controller<EnumMap<GHOST, MOVE>> ghostController;
    private Controller<MOVE> pacmanController = new StarterPacMan();
    private long stopTime, currTime;
    private int currentLives, currentLevel, currentMaze;
    private Stack<Game> gameStack = new Stack<>();
    private Game game;
    private Game prevGame;

    public MCTSSimulator(Game game, long timeDue, Controller<EnumMap<GHOST, MOVE>> ghostController) {
        this.ghostController = ghostController;
        if (timeDue <= 0)
            timeDue = System.currentTimeMillis() + 40;

        this.game = game;
        stopTime = timeDue;
        currentLives = game.getPacmanNumberOfLivesRemaining();
        currentLevel = game.getCurrentLevel();
        currentMaze = game.getMazeIndex();
        rootMCTSNode = new MCTSNode(game, game.getPacmanCurrentNodeIndex());
    }

    public MOVE MCTreeSearch() {

        while (running()) {
            pushToGameStack();
            MCTSNode expandedNode = treePolicy();
            float reward = defaultPolicy();
            backPropagation(expandedNode, reward);
            popFromGameStack();
        }

        MCTSNode best = bestNode(rootMCTSNode);

        if (best != null) {
           /*  System.out.println("Root: " + rootMCTSNode.nodeIdx);
            for (int i = 0; i < rootMCTSNode.children.size(); i++) {
                MCTSNode child = rootMCTSNode.children.get(i);
                System.out.printf("Child idx: %d, child reward: %s\n", child.nodeIdx, child.reward);
            }
            System.out.println("Best node: " + best.nodeIdx);
            System.out.println("Move: " + best.move.name());
            System.out.println();*/
            return best.move;
        }
        return game.getPacmanLastMoveMade();
    }

    /**
     * Tree Policy that consists of Selection Phase and Expansion Phase
     * It returns a node that haven't been expanded yet and then expands it
     *
     * @return
     */
    private MCTSNode treePolicy() {
        return expansionPhase(selectionPhase());
    }

    private MCTSNode selectionPhase() {
        MCTSNode current = rootMCTSNode;

        while (current.isFullyExpanded()) {
            current = bestNode(current);
        }

        return current;
    }

    private MCTSNode expansionPhase(MCTSNode current) {
        if (current.isFullyExpanded()) throw new IllegalArgumentException("node is already expanded");

        MCTSNode child = current;
        if (game.getMazeIndex() != currentMaze) {
            game = prevGame.copy();
        } else {
            child = current.expandNode(game);
            game.advanceGame(child.move, ghostController.getMove(game, 0));
        }
        return child;
    }

    private void playMove(MOVE move) {
        game.advanceGame(move, ghostController.getMove(game, 0));
    }


    /**
     * Returns the best child based on UCB1 formula. Part of Selection Phase
     * Also it plays advance game for a best move
     *
     * @param current
     * @return
     */
    private MCTSNode bestNode(MCTSNode current) {
        MCTSNode bestChild = null;
        float highestUCB1 = Integer.MIN_VALUE;

        for (MCTSNode child : current.children) {
            float ucb1 = UCB1(child);
            if (ucb1 > highestUCB1) {
                bestChild = child;
                highestUCB1 = ucb1;
            }
        }

        prevGame = game.copy();

        if (bestChild != null) game.advanceGame(bestChild.move, ghostController.getMove(game, 0));
        return bestChild;
    }

    /**
     * UCB1 formula from the book
     * Xj - average reward of all nodes beneath this node_j
     * Cp - exploration constant
     * n - the number of times the parent node has been visited
     * nj - the number of times the node_j has been visited
     *
     * @param node_j
     * @return
     */
    private float UCB1(MCTSNode node_j) {
        float Xj = node_j.reward / node_j.timesVisited;
        float Cp = (float) (1.0 / Math.sqrt(2));
        int n = node_j.parent.timesVisited;
        int nj = node_j.timesVisited;
        return (float) (Xj + 2 * Cp * Math.sqrt((2 * Math.log(n) / nj)));
    }


    /**
     * Simulation of game (playout) from expandedNode to the end using pacManController
     *
     * @return the outcome (reward) of the simulation
     */
    private float defaultPolicy() {
        //int i = 0;
        // int length = 4000 - game.getCurrentLevelTime();
        while (/*i++ < length &&*/ runningState()) {
            game.advanceGame(pacmanController.getMove(game, 0), ghostController.getMove(game, 0));
        }
        float score = game.getScore();
        /*if (game.getPacmanNumberOfLivesRemaining() < currentLives) {
            score = -5000;
        }*/
        return score;
    }

    /**
     * Updates the value of the simulation run on node vl
     * with reward delta. All ancestors of vl gets their
     * reward increased with delta and their visited count
     * increased by 1.
     *
     * @param expandedNode the node that had been simulated
     * @param delta        the value (reward) of the simulation
     */
    private void backPropagation(MCTSNode expandedNode, float delta) {
        MCTSNode prevNode = expandedNode;
        while (prevNode != null) {
            prevNode.timesVisited++;
            prevNode.reward += delta;
            prevNode = prevNode.parent;
        }
    }

    private boolean runningState() {
        return !game.gameOver() && game.getPacmanNumberOfLivesRemaining() == currentLives && game.getCurrentLevel() == currentLevel;
    }

    private boolean running() {
        currTime = System.currentTimeMillis();
        return currTime < stopTime - 20;
    }

    /**
     * Save a state of the game on top of the stack
     */
    private void pushToGameStack() {
        gameStack.push(game);
        game = game.copy();
    }

    /**
     * Get the top most game state from the stack
     *
     * @return the top most state
     */
    private void popFromGameStack() {
        game = gameStack.pop();
    }

    private void advanceGameToJunction() {
        MOVE move = game.getPacmanLastMoveMade();

        while (!isAtJunction(move)) {
            game.advanceGame(move, ghostController.getMove(game.copy(), -1));
        }
    }

    /**
     * Checks if pacman is at a junction or game is ended
     *
     * @return
     */
    private boolean isAtJunction(MOVE move) {
        //return true;
        return game.isJunction(game.getPacmanCurrentNodeIndex()) || isAtWall(move) || game.gameOver();
    }

    private boolean isAtWall(MOVE move) {
        MOVE[] possibles = game.getPossibleMoves(game.getPacmanCurrentNodeIndex());

        for (MOVE m : possibles) {
            if (m == move) {
                return false;
            }
        }
        return true;
    }

}
