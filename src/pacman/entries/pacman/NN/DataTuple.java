package pacman.entries.pacman.NN;

import java.util.ArrayList;
import java.util.List;

public class DataTuple {
    private List<Tuple> data;
    private List<Tuple> trainingData;
    private List<Tuple> testData;

    public DataTuple(List<Tuple> data) {
        this();
        this.data = data;
    }

    public DataTuple() {
        this.data = new ArrayList<>();
        this.trainingData = new ArrayList<>();
        this.testData = new ArrayList<>();
    }

    public void splitData() {
        int training = (int) (data.size() * 0.7);
        trainingData = data.subList(0, training);
        testData = data.subList(training, data.size() - 1);
    }

    public List<Tuple> getTrainingData() {
        splitData();
        return trainingData;
    }

    public List<Tuple> getTestData() {
        splitData();
        return testData;
    }

    public void addTuple(List<Double> input, List<Double> output) {
        data.add(new Tuple(input, output));
    }

    public void addTuple(Tuple tuple) {
        data.add(tuple);
    }

}

class Tuple {
    private List<Double> inputs;
    private List<Double> outputs;

    public Tuple() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    public Tuple(List<Double> inputs, List<Double> output) {
        this.inputs = inputs;
        this.outputs = output;
    }

    public List<Double> getInputs() {
        return inputs;
    }

    public List<Double> getOutputs() {
        return outputs;
    }
}
