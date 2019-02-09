package pacman.entries.pacman.Montecarlo;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Random;


public class MCTSNode {

    public int gameLevel;
    public int maze;
    public int nodeIdx;
    public List<MCTSNode> children = new ArrayList<>();
    public MCTSNode parent;
    public float reward = 0;
    public int timesVisited = 0;
    public MOVE move;
    public List<Integer> neighbouringNodesIdxs = new ArrayList<>();
    private boolean fullyExpanded = false;


    public MCTSNode(Game game, int nodeIdx) {
        this.nodeIdx = nodeIdx;
        this.maze = game.getMazeIndex();
        this.gameLevel = game.getCurrentLevel();
        int[] nn = game.getNeighbouringNodes(nodeIdx);
        for (int aNn : nn)
            neighbouringNodesIdxs.add(aNn);

    }

    public MCTSNode(Game game, int nodeIdx, MCTSNode parent, MOVE move) {
        this(game, nodeIdx);
        this.parent = parent;
        this.move = move;
        neighbouringNodesIdxs.remove((Integer) parent.nodeIdx);
    }

    public boolean isFullyExpanded() {
        return fullyExpanded;
    }

    public MCTSNode expandNode(Game game) {
        if (this.isFullyExpanded()) throw new IllegalArgumentException("This node is already expanded!");
        MCTSNode next;

        int neighbourNodeIdx = neighbouringNodesIdxs.get(new Random().nextInt(neighbouringNodesIdxs.size()));
        next = new MCTSNode(game, neighbourNodeIdx, this, game.getMoveToMakeToReachDirectNeighbour(this.nodeIdx, neighbourNodeIdx));
        this.children.add(next);
        neighbouringNodesIdxs.remove((Integer) next.nodeIdx);
        if (neighbouringNodesIdxs.size() == 0) fullyExpanded = true;
        return next;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MCTSNode mctsNode = (MCTSNode) o;
        return nodeIdx == mctsNode.nodeIdx;
    }

    @Override
    public int hashCode() {
        return Objects.hash(nodeIdx);
    }
}
