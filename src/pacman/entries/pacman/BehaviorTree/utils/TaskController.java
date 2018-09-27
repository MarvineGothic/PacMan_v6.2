package pacman.entries.pacman.BehaviorTree.utils;

/**
 * Class added by composition to any root,
 * to keep track of the Node state
 * and logic flow.
 *
 * This state-control class is separated
 * from the Node class so the Decorators
 * have a chance at compile-time security.
 * @author Ying
 *
 */
public class TaskController
{
    /**
     * Indicates whether the root is finished
     * or not
     */
    private boolean done;
    /**
     * If finished, it indicates if it has
     * finished with success or not
     */
    private boolean sucess;
    /**
     * Indicates if the root has started
     * or not
     */
    private boolean started;
    /**
     * Reference to the root we monitor
     */
    private Node node;
    /**
     * Creates a new instance of the
     * TaskController class
     * @param node Node to controll.
     */
    public TaskController(Node node)
    {
        setNode(node);
        initialize();
    }
    /**
     * Initializes the class data
     */
    private void initialize()
    {
        this.done = false;
        this.sucess = true;
        this.started = false;
    }
    /**
     * Sets the root reference
     * @param node Node to monitor
     */
    public void setNode(Node node)
    {
        this.node = node;
    }
    /**
     * Starts the monitored class
     */
    public void safeStart()
    {
        this.started = true;
       // root.start();
    }
    /**
     * Ends the monitored root
     */
    public void safeEnd()
    {
        this.done = false;
        this.started = false;
      //  root.end();
    }
    /**
     * Ends the monitored class, with success
     */
    protected void finishWithSuccess()
    {
        this.sucess = true;
        this.done = true;
        //root.logTask("Finished with success");
    }
    /**
     * Ends the monitored class, with failure
     */
    protected void finishWithFailure()
    {
        this.sucess = false;
        this.done = true;
        //root.logTask("Finished with failure");
    }
    /**
     * Indicates whether the root
     * finished successfully
     * @return True if it did, false if it didn't
     */
    public boolean succeeded()
    {
        return this.sucess;
    }
    /**
     * Indicates whether the root
     * finished with failure
     * @return True if it did, false if it didn't
     */
    public boolean failed()
    {
        return !this.sucess;
    }
    /**
     * Indicates whether the root finished
     * @return True if it did, false if it didn't
     */
    public boolean finished()
    {
        return this.done;
    }
    /**
     * Indicates whether the class
     * has started or not
     * @return True if it has, false if it hasn't
     */
    public boolean started()
    {
        return this.started;
    }
    /**
     * Marks the class as just started.
     */
    public void reset()
    {
        this.done = false;
    }
}
