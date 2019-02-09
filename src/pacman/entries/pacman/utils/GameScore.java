package pacman.entries.pacman.utils;

import pacman.game.Game;

import java.io.Serializable;

/**
 * This class saves game score, totalTime and game level.
 * It also has a method to calculate fitness value.
 */
public class GameScore implements Serializable {
    private static final long serialVersionUID = -976451354879045000L;
    public int score;
    public int totalTime;
    public int level;

    public GameScore(Game game) {
        this.score = game.getScore();

        this.totalTime = game.getTotalTime();

        this.level = game.getCurrentLevel();
    }

    public double getFintess_01() {
        return 1.0 / score;
    }
}
