package pacman.controllers;

import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

import static pacman.entries.pacman.Utils.getAllSafeJunctionPaths;

/*
 * Allows a human player to play the game using the arrow key of the keyboard.
 */
public class HumanController extends Controller<MOVE> {
    public KeyBoardInput input;

    public HumanController(KeyBoardInput input) {
        this.input = input;
    }

    public KeyBoardInput getKeyboardInput() {
        return input;
    }

    public MOVE getMove(Game game, long dueTime) {
        int[] junctions = game.getJunctionIndices();

        switch (input.getKey()) {
            case KeyEvent.VK_UP:
                return MOVE.UP;
            case KeyEvent.VK_RIGHT:
                return MOVE.RIGHT;
            case KeyEvent.VK_DOWN:
                return MOVE.DOWN;
            case KeyEvent.VK_LEFT:
                return MOVE.LEFT;
            default:
                return MOVE.NEUTRAL;
        }
    }
}