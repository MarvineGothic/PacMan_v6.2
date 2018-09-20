package pacman.entries.utils;

import pacman.game.Game;

import java.io.Serializable;

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

    public double getPointsPerTime() {
        return (double) score / totalTime;
    }

    public double getTimePerLevel() {
        return (double) totalTime / level;
    }

    public int getLevel() {
        return level + 1;
    }


    public double getFintess_01() {
        return 1.0 / score;
    }

    public double getFintess_02() {
        return 1.0 / (score + totalTime);
    }

    public double getFintess_03() {
        return score;
    }
}
