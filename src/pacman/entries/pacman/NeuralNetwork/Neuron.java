package pacman.entries.pacman.NeuralNetwork;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

class Neuron implements Serializable {

    private static final long serialVersionUID = -7557746719637915288L;

    double error, bias, output;
    List<Double> inputs, weights;

    /**
     * Neuron constructor.
     * Takes number of inputs and creates a list of random weights
     * corresponding to the inputs size.
     *
     * @param inputs
     */
    Neuron(int inputs) {
        bias = Math.random();
        weights = new ArrayList<>();
        for (int i = 0; i < inputs; i++)
            weights.add(Math.random());
    }

    /**
     * Method uses sigmoid function to calculate outputs for the neuron.
     *
     * @param inputs
     * @return
     */
    double useSigmoid(List<Double> inputs) {
        this.inputs = inputs;
        double sum = bias;
        for (int i = 0; i < inputs.size(); i++)
            sum += inputs.get(i) * weights.get(i);
        output = 1.0 / (1 + Math.pow(Math.E, -sum));
        return output;
    }
}
