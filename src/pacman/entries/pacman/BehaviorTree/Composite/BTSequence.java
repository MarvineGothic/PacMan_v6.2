package pacman.entries.pacman.BehaviorTree.Composite;

import pacman.entries.pacman.BehaviorTree.Archetypes.Composite;
import pacman.entries.pacman.BehaviorTree.Archetypes.Node;

import static pacman.entries.pacman.BehaviorTree.TreeBuilder.runningNode;
import static pacman.entries.pacman.BehaviorTree.Archetypes.Node.Status.FAILURE;
import static pacman.entries.pacman.BehaviorTree.Archetypes.Node.Status.RUNNING;

public class BTSequence extends Composite {

    @Override
    public void init() {

    }

    @Override
    public Status execute() {
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

}
