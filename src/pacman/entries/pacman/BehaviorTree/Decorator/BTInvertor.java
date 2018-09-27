package pacman.entries.pacman.BehaviorTree.Decorator;

import pacman.entries.pacman.BehaviorTree.Archetypes.Decorator;

public class BTInvertor extends Decorator {

    @Override
    public void init() {

    }

    @Override
    public Status execute() {
        return this.child.execute();
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
