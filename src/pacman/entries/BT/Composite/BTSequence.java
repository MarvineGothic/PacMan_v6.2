package pacman.entries.BT.Composite;

import pacman.entries.BT.utils.Node;

import java.util.ArrayList;
import java.util.List;

import static pacman.entries.BT.TreeBuilder.runningNode;
import static pacman.entries.BT.utils.Node.Status.FAILURE;
import static pacman.entries.BT.utils.Node.Status.RUNNING;

public class BTSequence extends Node {
    public List<Node> children = new ArrayList<>();


    @Override
    public void init() {

    }

    @Override
    public Status execute() {
        //this.init();
        Status status = FAILURE;
        for (Node node : this.children) {
            status = node.execute();
            if (status == FAILURE)
                return FAILURE;
            if (status == RUNNING) {
                runningNode = node;
                return RUNNING;
            }
        }
        return status;
    }


    @Override
    public boolean successConditions() {
        return false;
    }

    @Override
    public boolean runningConditions() {
        return false;
    }


    @Override
    public void doAction() {

    }

    public BTSequence add(Node node) {
        children.add(node);
        return this;
    }

}
