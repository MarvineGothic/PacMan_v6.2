package pacman.entries.BT.Composite;

import pacman.entries.BT.utils.Status;
import pacman.entries.BT.utils.Task;

import java.util.ArrayList;
import java.util.List;

import static pacman.entries.BT.utils.Status.FAILURE;
import static pacman.entries.BT.utils.Status.RUNNING;

public class BTSelector extends Task {
    public List<Task> children = new ArrayList<>();

    @Override
    public Status execute() {
        this.init();
        Status status = FAILURE;
        for (Task task : this.children) {
            status = task.execute();
            if (status == Status.SUCCESS)
                return Status.SUCCESS;
            if (status == RUNNING) {
                runningLeaf = task;
                return RUNNING;
            }
        }
        return status;
    }

    @Override
    public boolean checkConditions() {
        return false;
    }


    @Override
    public void doAction() {

    }
}
