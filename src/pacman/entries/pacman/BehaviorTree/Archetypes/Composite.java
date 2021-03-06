package pacman.entries.pacman.BehaviorTree.Archetypes;

import java.util.ArrayList;
import java.util.List;

public abstract class Composite extends Node {
    public List<Node> children = new ArrayList<>();

    @Override
    public Node add(Node node) {
        children.add(node);
        return this;
    }
}
