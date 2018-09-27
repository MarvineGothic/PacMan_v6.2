package pacman.entries.pacman.NN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A layer represents a collection of neurons. A layer takes a set of
 * inputs, runs them through its neurons, and outputs a collection of
 * outputs ranging from 0 to 1.
 *
 * @author ashwin
 */
public class Layer implements Serializable {

    private static final long serialVersionUID = -152148321829417267L;
    public List<Double> _outputs = new ArrayList<>();
    public int _inputs;
    private List<Neuron> _neurons;

    /**
     * Creates a layer with the specified number of neurons that each
     * take the specified number of inputs. The neurons weights are
     * initialized to random values.
     * neurons.weightsSize == nodes
     */
    public Layer(int inputs, int nodes) {
        _inputs = inputs;
        _neurons = new ArrayList<>();
        for (int i = 0; i < nodes; i++)
            _neurons.add(new Neuron(inputs));
    }

    public Layer(List<Neuron> neurons) {
        _neurons = neurons;
    }


    public int size() {
        return _neurons.size();
    }

    public List<Neuron> getNeurons() {
        return _neurons;
    }

    /**
     * Returns the outputs of the layer given the set of inputs.
     * The output list contains the action potential of each neuron when
     * supplied with the given input values.
     *
     * @param inputs
     * @return
     */
    public List<Double> getOutputs(List<Double> inputs) {
        if (this._inputs == 0) {
            _outputs = inputs;
            return inputs;
        }
        List<Double> outputs = new ArrayList<>();
        for (int i = 0; i < _neurons.size(); i++)
            outputs.add(_neurons.get(i).getActionPotential(inputs));
        _outputs = outputs;
        return outputs;
    }
}
