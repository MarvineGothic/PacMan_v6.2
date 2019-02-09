package pacman.entries.pacman.NeuralNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Layer implements Serializable {

    private static final long serialVersionUID = -152148321829417267L;
    public List<Double> outputs;
    public int inputs;
    private List<Neuron> neurons;

    /**
     * Creates a layer with the specified number of neurons that each
     * take the specified number of inputs. The neurons weights are
     * initialized to random values.
     * neurons.weightsSize == nodes
     *
     * @param inputs
     * @param nodes
     */
    Layer(int inputs, int nodes) {
        this.inputs = inputs;
        neurons = new ArrayList<>();
        for (int i = 0; i < nodes; i++)
            neurons.add(new Neuron(inputs));
    }

    public int size() {
        return neurons.size();
    }

    public List<Neuron> getNeurons() {
        return neurons;
    }

    public void setNeurons(List<Neuron> neurons) {
        this.neurons = neurons;
    }

    /**
     * Returns the outputs of the layer given the set of inputs.
     * The output list contains the action potential of each neuron when
     * supplied with the given input values.
     *
     * @param inputs
     * @return
     */
    List<Double> getOutputs(List<Double> inputs) {
        if (this.inputs == 0) {
            outputs = inputs;
            return inputs;
        }
        List<Double> outputs = new ArrayList<>();
        for (Neuron neuron : neurons) outputs.add(neuron.useSigmoid(inputs));
        this.outputs = outputs;
        return outputs;
    }
}
