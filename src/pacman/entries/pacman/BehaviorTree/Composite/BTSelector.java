package pacman.entries.pacman.BehaviorTree.Composite;

import pacman.entries.pacman.BehaviorTree.Archetypes.Composite;
import pacman.entries.pacman.BehaviorTree.utils.Node;

import static pacman.entries.pacman.BehaviorTree.TreeBuilder.runningNode;
import static pacman.entries.pacman.BehaviorTree.utils.Node.Status.FAILURE;
import static pacman.entries.pacman.BehaviorTree.utils.Node.Status.RUNNING;

public class BTSelector extends Composite {
    //public List<Node> children = new ArrayList<>();


    @Override
    public void init() {

    }

    @Override
    public Status execute() {
        //this.init();
        Status status = FAILURE;
        for (Node node : this.children) {
            status = node.execute();
            if (status == Status.SUCCESS)
                return Status.SUCCESS;
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
