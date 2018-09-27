package pacman.entries.pacman.NN;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * A neuron models the neurons in our brains. It takes the weighted sum
 * of the products of its inputs and weights and adds it to a bias term.
 * It then runs this sum through a sigmoid function to calculate the
 * action potential of the neuron. This action potential is used as
 * inputs for neurons in the next layer of the NN net.
 *
 * @author ashwin
 */
public class Neuron implements Serializable {

    private static final long serialVersionUID = -7557746719637915288L;

    /**
     * The slope parameter is a property of the sigmoid function.
     * The greater the slope parameter the flatter the graph and the
     * small the slope parameter the steeper the graph is.
     */
    private static final double SLOPE_PARAMETER = 1.0;
    public double error, bias, output;
    public List<Double> _inputs, _weights;

    public Neuron(int inputs) {
        bias = Math.random();
        _weights = new ArrayList<>();
        for (int i = 0; i < inputs; i++)
            _weights.add(Math.random());
    }

    public Neuron(List<Double> weights) {
        _weights = weights;
    }


    /**
     * Returns the action potential of the neuron. This method first computes
     * the sum of the products of the weights and inputs and then runs the
     * sum through the sigmoid function to determine the output potential.
     *
     * @param inputs NN inputs
     * @return action potential
     */
    public double getActionPotential(List<Double> inputs) {
        _inputs = inputs;
        double sum = bias;
        for (int i = 0; i < inputs.size(); i++)
            sum += inputs.get(i) * _weights.get(i);
        output = 1.0 / (1 + Math.pow(Math.E, -sum * Neuron.SLOPE_PARAMETER));
        return output;
    }
}
