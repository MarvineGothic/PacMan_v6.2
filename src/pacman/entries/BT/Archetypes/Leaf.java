package pacman.entries.BT.Archetypes;

import pacman.entries.BT.utils.Task;
import pacman.entries.BT.utils.TaskController;

public abstract class Leaf extends Task {


    /**
     * Task controler to keep track of the
     * Task state.
     */
    protected TaskController control;

    /**
     * Creates a new instance of the
     * LeafTask class
     *
     */
    public Leaf() {
        super();
        createController();
    }

    /**
     * Creates the controller for the class
     */
    private void createController()
    {
        this.control = new TaskController(this);
    }

}
