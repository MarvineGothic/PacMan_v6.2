package pacman.controllers;

import pacman.game.Game;

/**
 * This class is the superclass of your controller. In contains the code required to train the
 * controller as a thread. In provides numerous methods that allow the Executor to use the
 * controller in various different execution modes. Your controller only needs to provide the
 * code for the getMove() method.
 *
 * @param <T> The generic type of the currentMove to be returned (either a single currentMove for Ms Pac-Man or an EnumMap for the ghosts).
 */
public abstract class Controller<T> implements Runnable {
    protected T lastMove;    //this is now protected. You can set this directly in your getMove() method to save an immediate response.
    private boolean alive, wasSignalled, hasComputed;
    private volatile boolean threadStillRunning;
    private long timeDue;
    private Game game;

    /**
     * Instantiates a new controller. The constructor initialises the class variables.
     */
    public Controller() {
        alive = true;
        wasSignalled = false;
        hasComputed = false;
        threadStillRunning = false;
    }

    /**
     * Terminates the controller: a signal is sent and the flag 'alive' is set to false. When
     * the thread wakes up, the outer loop will terminate and the thread finishes.
     */
    public final void terminate() {
        alive = false;
        wasSignalled = true;

        synchronized (this) {
            notify();
        }
    }

    /**
     * Updates the game state: a copy of the game is passed to this method and the class variable is
     * updated accordingly.
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next currentMove is due
     */
    public final void update(Game game, long timeDue) {
        synchronized (this) {
            this.game = game;
            this.timeDue = timeDue;
            wasSignalled = true;
            hasComputed = false;
            notify();
        }
    }

    /**
     * Retrieves the currentMove from the controller (whatever is stored in the class variable).
     *
     * @return The currentMove stored in the class variable 'lastMove'
     */
    public final T getMove() {
        return lastMove;
    }

    /* (non-Javadoc)
     * @see java.lang.Runnable#train()
     */
    public final void run() {
        while (alive) {
            synchronized (this) {
                while (!wasSignalled) {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                if (!threadStillRunning) {
                    new Thread() {
                        public void run() {
                            threadStillRunning = true;
                            lastMove = getMove(game, timeDue);
                            hasComputed = true;
                            threadStillRunning = false;
                        }
                    }.start();
                }

                wasSignalled = false;
            }
        }
    }

    /**
     * This method is used to check whether the controller computed a currentMove since the last
     * update of the game.
     *
     * @return Whether or not the controller computed a currentMove since the last update
     */
    public final boolean hasComputed() {
        return hasComputed;
    }

    /**
     * Compute the next currentMove given a copy of the current game and a time the currentMove has to be computed by.
     * This is the method contestants need to implement. Many examples are available in
     * pacman.controllers.examples
     * Your controllers must be in the files: pacman.entries.pacman.MyPacMan.java for Pac-Man controllers or
     * pacman.entries.pacman.ghosts.MyGhosts.java for ghosts controllers.
     *
     * @param game    A copy of the current game
     * @param timeDue The time the next currentMove is due
     * @return The currentMove to be played (i.e., the currentMove calculated by your controller)
     */
    public abstract T getMove(Game game, long timeDue);
}