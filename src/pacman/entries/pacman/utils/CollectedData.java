package pacman.entries.pacman.utils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CollectedData implements Serializable {
    private static final long serialVersionUID = -6598756105406456487L;

    public List<DataTuple> dataTuples = new ArrayList<>();

    public void addTuple(DataTuple dataTuple) {
        dataTuples.add(dataTuple);
    }
}
