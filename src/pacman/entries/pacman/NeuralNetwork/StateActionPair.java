package pacman.entries.pacman.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

public class StateActionPair {
    private List<Double> inputs;
    private List<Double> outputs;

    public StateActionPair() {
        this.inputs = new ArrayList<>();
        this.outputs = new ArrayList<>();
    }

    StateActionPair(List<Double> inputs, List<Double> output) {
        this.inputs = inputs;
        this.outputs = output;
    }

    public void addInput(double input) {
        inputs.add(input);
    }

    public void addOutput(double output) {
        outputs.add(output);
    }

    public void setInputs(List<Double> inputs) {
        this.inputs = inputs;
    }

    public void setOutputs(List<Double> outputs) {
        this.outputs = outputs;
    }

    public List<Double> getInputs() {
        return inputs;
    }

    public List<Double> getOutputs() {
        return outputs;
    }
}
