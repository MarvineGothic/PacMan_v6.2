package pacman.entries.pacman.NN;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * NeuralNets are a collection of Layers. They can be executed on a series
 * of inputs to produce a series of outputs. While the NeuralNet itself is
 * abstract and generalized, it can be used in any number of situations.
 * The NeuralNet implements Serializable so that trained nets can be saved to
 * file and used in the future with the PMWGAController.
 *
 * @author ashwin
 */
@SuppressWarnings("all")
public class NeuralNet implements Serializable {

    private static final long serialVersionUID = -4747208554472473154L;

    public static double learningRate = 0.2;
    private static List<Layer> _layers;

    public NeuralNet(List<Layer> layers) {
        _layers = layers;
    }

    public NeuralNet(int... nodes) {
        _layers = new ArrayList<>();

        for (int i = 1; i < nodes.length; i++)
            _layers.add(new Layer(nodes[i - 1], nodes[i]));
    }

    public static void main(String[] args) {

        DataTuple dataTuple = new DataTuple();
        for (int j = 0; j < 10; j++) {
            List<Double> inputs = new ArrayList<>();
            List<Double> outputs = new ArrayList<>();
            for (int i = 0; i < 20; i++) {
                inputs.add(Math.random());
            }
            int index = new Random().nextInt(3);
            for (int i = 0; i < 4; i++)
                outputs.add(0.0);
            outputs.set(index, 1.0);

            dataTuple.addTuple(inputs, outputs);
        }
        List<Tuple> trainData = dataTuple.getTrainingData();

        NeuralNet n1 = new NeuralNet(trainData.get(0).getInputs().size(), 10, trainData.get(0).getOutputs().size());
        n1.run(trainData, 1_000, 0.001, 0.000_01);
        System.out.println(n1);
    }

    public void run(List<Tuple> tuple, int epochs, double w_threshold, double error_threshold) {
        int epoch = 0;
        double largest_delta_w = Double.MIN_VALUE;
        double minError = Double.MAX_VALUE;
        while (true) {
            for (Tuple t : tuple) {
                feedForward(t.getInputs());

                // ================= UPDATE ERRORS FOR OUTPUT LAYER ====================
                Layer ouputL = getOutputLayer();

                for (int i = 0; i < ouputL.size(); i++) {
                    Neuron n = ouputL.getNeurons().get(i);
                    n.error = n.output * (1 - n.output) * (t.getOutputs().get(i) - n.output);
                    if (Math.abs(n.error) < minError) minError = Math.abs(n.error);
                }

                // ================= UPDATE ERRORS FOR HIDDEN LAYERS ===================
                for (int i = _layers.size() - 2; i >= 0; i--) {
                    Layer hiddenL = _layers.get(i);
                    Layer prevL = _layers.get(i + 1);
                    List<Neuron> hiddenNeurons = hiddenL.getNeurons();
                    List<Neuron> prevNeurons = prevL.getNeurons();

                    for (int j = 0; j < hiddenL.size(); j++) {
                        double error = 0;
                        Neuron n = hiddenNeurons.get(j);
                        for (int p = 0; p < prevL.size(); p++) {
                            Neuron synapse = prevNeurons.get(p);
                            error += synapse.error * synapse._weights.get(j);
                        }
                        n.error = hiddenL._outputs.get(j) * (1 - hiddenL._outputs.get(j)) * error;
                    }
                }
                // =============== UPDATE WEIGHTS AND BIASES =================
                for (Layer _layer : _layers) {
                    for (Neuron n : _layer.getNeurons()) {
                        for (int w = 0; w < n._weights.size(); w++) {
                            double delta_w = learningRate * n.error * n._inputs.get(w);
                            double updWeight = n._weights.get(w) + delta_w;
                            n._weights.set(w, updWeight);
                            if (delta_w > largest_delta_w)
                                largest_delta_w = delta_w;

                        }
                        double delta_b = learningRate * n.error;
                        n.bias += delta_b;
                    }
                }
                System.out.println("Epoch: " + epoch);
                System.out.println("Actual output: " + t.getOutputs().toString());
                System.out.println("Got output: " + ouputL._outputs.toString());
                System.out.println("================================================================================\n\n\n");
            }
            // ============================== STOP =================================
            System.out.println(getOutputLayer()._outputs.get(0));
            //if (largest_delta_w < w_threshold) System.out.println("Largest delta:" + largest_delta_w);
            //if (minError < error_threshold) System.out.println("MIN error: " + minError);
            if (epoch >= epochs /*|| largest_delta_w < w_threshold || minError < error_threshold*/)
                break;
            epoch++;
        }
    }

    public Layer getOutputLayer() {
        return _layers.get(_layers.size() - 1);
    }

    /**
     * Executes the entire NN net and returns the output of the
     * top most layer in the net.
     *
     * @param inputs
     * @return
     */
    public List<Double> feedForward(List<Double> inputs) {
        return feedForward(_layers.size() - 1, inputs);
    }

    /**
     * Executes the NN net up to the specified layer and returns
     * the output of the top most layer.
     *
     * @param layer
     * @param inputs
     * @return
     */
    public List<Double> feedForward(int layer, List<Double> inputs) {
        if (layer == 0)
            return _layers.get(layer).getOutputs(inputs);
        else
            return _layers.get(layer).getOutputs(feedForward(layer - 1, inputs));
    }
}
