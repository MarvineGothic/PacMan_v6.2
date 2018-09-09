package pacman.entries.BT.Decorator;

import pacman.entries.BT.Archetypes.Decorator;
import pacman.entries.BT.utils.Status;
import pacman.game.Constants;
import pacman.game.Game;

public class BTInvertor extends Decorator {
    @Override
    public void init() {

    }

    @Override
    public Status execute() {
        return this.child.execute();
    }

    @Override
    public boolean checkConditions() {
        return false;
    }

    @Override
    public void doAction() {

    }

    @Override
    public Constants.MOVE getMove(Game game, long timeDue) {
        return null;
    }
}
