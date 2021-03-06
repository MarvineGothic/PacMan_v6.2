package pacman.entries.pacman.utils;

import pacman.game.Constants;
import pacman.game.Constants.DM;
import pacman.game.Constants.GHOST;
import pacman.game.Constants.MOVE;
import pacman.game.Game;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static pacman.game.Constants.LEVEL_LIMIT;
import static pacman.game.Constants.MAX_TIME;

public class DataTuple implements Serializable {

    private static final long serialVersionUID = -987498808765435435L;

    public MOVE DirectionChosen;

    //General game state this - not normalized!
    public int mazeIndex;
    public int currentLevel;
    public int pacmanPosition;
    public int pacmanLivesLeft;
    public int currentScore;
    public int totalGameTime;
    public int currentLevelTime;
    public int numOfPillsLeft;
    public int numOfPowerPillsLeft;
    public int closestPill;
    public int closestPower;
    public int closestPillDistance;
    public int closestPowerDistance;

    //Ghost this, dir, dist, edible - BLINKY, INKY, PINKY, SUE
    public boolean isBlinkyEdible = false;
    public boolean isInkyEdible = false;
    public boolean isPinkyEdible = false;
    public boolean isSueEdible = false;

    public int blinkyDist = -1;
    public int inkyDist = -1;
    public int pinkyDist = -1;
    public int sueDist = -1;

    public MOVE blinkyDir;
    public MOVE inkyDir;
    public MOVE pinkyDir;
    public MOVE sueDir;

    //Util data - useful for normalization
    public int numberOfNodesInLevel;
    public int numberOfTotalPillsInLevel;
    public int numberOfTotalPowerPillsInLevel;
    public int[] pillsIndices;
    public int[] powerIndices;
    public int maxPillIndex;

    public DataTuple(Game game, MOVE move) {
        if (move == MOVE.NEUTRAL) {
            move = game.getPacmanLastMoveMade();
        }

        this.DirectionChosen = move;

        this.mazeIndex = game.getMazeIndex();
        this.currentLevel = game.getCurrentLevel();
        this.pacmanPosition = game.getPacmanCurrentNodeIndex();
        this.pacmanLivesLeft = game.getPacmanNumberOfLivesRemaining();
        this.currentScore = game.getScore();
        this.totalGameTime = game.getTotalTime();
        this.currentLevelTime = game.getCurrentLevelTime();
        this.numOfPillsLeft = game.getNumberOfActivePills();
        this.numOfPowerPillsLeft = game.getNumberOfActivePowerPills();
        this.closestPill = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getActivePillsIndices(), DM.PATH);
        this.closestPower = game.getClosestNodeIndexFromNodeIndex(game.getPacmanCurrentNodeIndex(), game.getActivePowerPillsIndices(), DM.PATH);
        this.closestPillDistance = game.getShortestPathDistance(this.closestPill, game.getPacmanCurrentNodeIndex());
        this.closestPowerDistance = game.getShortestPathDistance(this.closestPower, game.getPacmanCurrentNodeIndex());

        if (game.getGhostLairTime(GHOST.BLINKY) == 0) {
            this.isBlinkyEdible = game.isGhostEdible(GHOST.BLINKY);
            this.blinkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.BLINKY));
        }

        if (game.getGhostLairTime(GHOST.INKY) == 0) {
            this.isInkyEdible = game.isGhostEdible(GHOST.INKY);
            this.inkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.INKY));
        }

        if (game.getGhostLairTime(GHOST.PINKY) == 0) {
            this.isPinkyEdible = game.isGhostEdible(GHOST.PINKY);
            this.pinkyDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.PINKY));
        }

        if (game.getGhostLairTime(GHOST.SUE) == 0) {
            this.isSueEdible = game.isGhostEdible(GHOST.SUE);
            this.sueDist = game.getShortestPathDistance(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.SUE));
        }

        this.blinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.BLINKY), DM.PATH);
        this.inkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.INKY), DM.PATH);
        this.pinkyDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.PINKY), DM.PATH);
        this.sueDir = game.getNextMoveTowardsTarget(game.getPacmanCurrentNodeIndex(), game.getGhostCurrentNodeIndex(GHOST.SUE), DM.PATH);

        this.numberOfNodesInLevel = game.getNumberOfNodes();
        this.numberOfTotalPillsInLevel = game.getNumberOfPills();
        this.numberOfTotalPowerPillsInLevel = game.getNumberOfPowerPills();
        this.pillsIndices = game.getPillIndices();
        this.powerIndices = game.getPowerPillIndices();
        this.maxPillIndex = this.pillsIndices[this.pillsIndices.length - 1];
    }

    public List<Double> getNormalizedData(int... index) {
        List<Double> inputs = new ArrayList<>();

        for (int i = 0; i < index.length; i++)
            switch (index[i]) {
                case 0:
                    inputs.add(normalizeLevel(mazeIndex));
                    break;
                case 1:
                    inputs.add(normalizeLevel(currentLevel));
                    break;
                case 2:
                    inputs.add(normalizePosition(pacmanPosition));
                    break;
                case 3:
                    inputs.add((double) pacmanLivesLeft / 4);
                    break;
                case 4:
                    inputs.add((double) closestPill / maxPillIndex);
                    break;
                case 5:
                    inputs.add((double) closestPower / maxPillIndex);
                    break;
                case 6:
                    inputs.add(normalizeDistance(closestPillDistance));
                    break;
                case 7:
                    inputs.add(normalizeDistance(closestPowerDistance));
                    break;
                case 8:
                    inputs.add(normalizeCurrentScore(currentScore));
                    break;
                case 9:
                    inputs.add(normalizeTotalGameTime(totalGameTime));
                    break;
                case 10:
                    inputs.add(normalizeCurrentLevelTime(currentLevelTime));
                    break;
                case 11:
                    inputs.add(normalizeNumberOfPills(numOfPillsLeft));
                    break;
                case 12:
                    inputs.add(normalizeNumberOfPowerPills(numOfPowerPillsLeft));
                    break;
                case 13:
                    inputs.add(normalizeBoolean(isBlinkyEdible));
                    break;
                case 14:
                    inputs.add(normalizeBoolean(isInkyEdible));
                    break;
                case 15:
                    inputs.add(normalizeBoolean(isPinkyEdible));
                    break;
                case 16:
                    inputs.add(normalizeBoolean(isSueEdible));
                    break;
                case 17:
                    inputs.add(normalizeDistance(blinkyDist));
                    break;
                case 18:
                    inputs.add(normalizeDistance(inkyDist));
                    break;
                case 19:
                    inputs.add(normalizeDistance(pinkyDist));
                    break;
                case 20:
                    inputs.add(normalizeDistance(sueDist));
                    break;
                case 21:
                    inputs.add(blinkyDir.ordinal() / 3.0);
                    break;
                case 22:
                    inputs.add(inkyDir.ordinal() / 3.0);
                    break;
                case 23:
                    inputs.add(pinkyDir.ordinal() / 3.0);
                    break;
                case 24:
                    inputs.add(sueDir.ordinal() / 3.0);
                    break;
            }
        return inputs;
    }

    /**
     * Used to normalize distances. Done via min-max normalization.
     * Supposes that minimum possible distance is 0. Supposes that
     * the maximum possible distance is the total number of nodes in
     * the current level.
     *
     * @param dist Distance to be normalized
     * @return Normalized distance
     */
    public double normalizeDistance(int dist) {
        return (double) dist / this.numberOfNodesInLevel;
    }

    public double normalizeLevel(int level) {
        return (double) level / Constants.NUM_MAZES;
    }

    public double normalizePosition(int position) {
        return (double) position / this.numberOfNodesInLevel;
    }

    public double normalizeBoolean(boolean bool) {
        if (bool) {
            return 1.0;
        } else {
            return 0.0;
        }
    }

    public double normalizeNumberOfPills(int numOfPills) {
        return (double) numOfPills / this.numberOfTotalPillsInLevel;
    }

    public double normalizeNumberOfPowerPills(int numOfPowerPills) {
        return (double) numOfPowerPills / this.numberOfTotalPowerPillsInLevel;
    }

    public double normalizeTotalGameTime(int time) {
        return (double) time / MAX_TIME;
    }

    public double normalizeCurrentLevelTime(int time) {
        return (double) time / LEVEL_LIMIT;
    }

    /**
     * Max score value lifted from highest ranking PacMan controller on PacMan vs Ghosts
     * website: http://pacman-vs-ghosts.net/controllers/1104
     *
     * @param score
     * @return
     */
    public double normalizeCurrentScore(int score) {
        return (double) score / 82180;
    }

}
/*
* inputs.add(normalizeLevel(mazeIndex));
        //inputs.add(normalizeLevel(currentLevel));
        inputs.add(normalizePosition(pacmanPosition));
        //inputs.add((double) pacmanLivesLeft / 4);
        inputs.add((double) closestPill / maxPillIndex);
        inputs.add((double) closestPower / maxPillIndex);
        inputs.add(normalizeDistance(closestPillDistance));
        inputs.add(normalizeDistance(closestPowerDistance));
        //inputs.add(normalizeCurrentScore(currentScore));
        //inputs.add(normalizeTotalGameTime(totalGameTime));
        //inputs.add(normalizeCurrentLevelTime(currentLevelTime));
        inputs.add(normalizeNumberOfPills(numOfPillsLeft));
        inputs.add(normalizeNumberOfPowerPills(numOfPowerPillsLeft));
        inputs.add(normalizeBoolean(isBlinkyEdible));
        inputs.add(normalizeBoolean(isInkyEdible));
        inputs.add(normalizeBoolean(isPinkyEdible));
        inputs.add(normalizeBoolean(isSueEdible));
        inputs.add(normalizeDistance(blinkyDist));
        inputs.add(normalizeDistance(inkyDist));
        inputs.add(normalizeDistance(pinkyDist));
        inputs.add(normalizeDistance(sueDist));
        inputs.add(blinkyDir.ordinal() / 3.0);
        inputs.add(inkyDir.ordinal() / 3.0);
        inputs.add(pinkyDir.ordinal() / 3.0);
        inputs.add(sueDir.ordinal() / 3.0);*/
/*for (int i = 0; i < index.length; i++)
            switch (index[i]) {
                case 0:
                    inputs.add(normalizeLevel(mazeIndex));
                case 1:
                    //inputs.add(normalizeLevel(currentLevel));
                case 2:
                    inputs.add(normalizePosition(pacmanPosition));
                case 3://inputs.add((double) pacmanLivesLeft / 4);
                case 4:
                    inputs.add((double) closestPill / maxPillIndex);
                case 5:
                    inputs.add((double) closestPower / maxPillIndex);
                case 6:
                    inputs.add(normalizeDistance(closestPillDistance));
                case 7:
                    inputs.add(normalizeDistance(closestPowerDistance));
                case 8://inputs.add(normalizeCurrentScore(currentScore));
                case 9://inputs.add(normalizeTotalGameTime(totalGameTime));
                case 10://inputs.add(normalizeCurrentLevelTime(currentLevelTime));
                case 11:
                    inputs.add(normalizeNumberOfPills(numOfPillsLeft));
                case 12:
                    inputs.add(normalizeNumberOfPowerPills(numOfPowerPillsLeft));
                case 13:
                    inputs.add(normalizeBoolean(isBlinkyEdible));
                case 14:
                    inputs.add(normalizeBoolean(isInkyEdible));
                case 15:
                    inputs.add(normalizeBoolean(isPinkyEdible));
                case 16:
                    inputs.add(normalizeBoolean(isSueEdible));
                case 17:
                    inputs.add(normalizeDistance(blinkyDist));
                case 18:
                    inputs.add(normalizeDistance(inkyDist));
                case 19:
                    inputs.add(normalizeDistance(pinkyDist));
                case 20:
                    inputs.add(normalizeDistance(sueDist));
                case 21:
                    inputs.add(blinkyDir.ordinal() / 3.0);
                case 22:
                    inputs.add(inkyDir.ordinal() / 3.0);
                case 23:
                    inputs.add(pinkyDir.ordinal() / 3.0);
                case 24:
                    inputs.add(sueDir.ordinal() / 3.0);
                default:
            }*/