package pacman.entries.BT.Archetypes;

import pacman.entries.BT.utils.Task;

import java.util.ArrayList;
import java.util.List;

public abstract class Composite extends Task {
    public List<Task> children = new ArrayList<>();
}
