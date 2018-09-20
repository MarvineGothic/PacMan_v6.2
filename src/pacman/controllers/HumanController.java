package pacman.controllers;

import pacman.game.Constants.MOVE;
import pacman.game.Game;
import pacman.game.GameView;

import java.awt.*;
import java.awt.event.KeyEvent;

import static pacman.entries.utils.Utils.getAllIndices;
import static pacman.entries.utils.Utils.getSafeIndices;
import static pacman.entries.utils.Utils.getShortestSafePath;
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
        int pacManIdx = game.getPacmanCurrentNodeIndex();

        int[] junctions = game.getJunctionIndices();

        int[] pillsIdxs = game.getActivePillsIndices();
        int[] allIdxs = getSafeIndices(game, getAllIndices(game),false);
        int[] safePills = getSafeIndices(game, pillsIdxs,false);
        int[] safePath = getShortestSafePath(game, pacManIdx, safePills,false);

        if (allIdxs.length > 0 && safePath != null && safePath.length > 0) {
            GameView.addPoints(game, Color.BLUE, allIdxs);
            GameView.addPoints(game, Color.GREEN, safePath);
        }

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