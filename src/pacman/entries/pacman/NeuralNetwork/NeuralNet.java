package pacman.entries.pacman.NeuralNetwork;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Random;

import static pacman.entries.pacman.utils.Utils.saveToFile;


@SuppressWarnings("all")
public class NeuralNet implements Serializable {

    private static final long serialVersionUID = -4747208554472473154L;

    private double learningRate = 2;
    private List<Layer> layers;
    private double error;
    private Properties properties;

    /**
     * Constructor for Neural Net.
     * It takes an array of nodes as parameters for layers.
     * First and last elements in the array will be correspondently
     * the input and output layers. Values in between are hidden layers.
     *
     * @param properties
     * @param nodes
     */
    public NeuralNet(Properties properties, int... nodes) {
        this.layers = new ArrayList<>();
        this.properties = properties;
        this.error = Double.MAX_VALUE;

        for (int i = 1; i < nodes.length; i++)
            layers.add(new Layer(nodes[i - 1], nodes[i]));
    }

    /**
     * Loads a NeuralNet from file. This method performs Object Deserialization
     * using the Serialization interface provided by Java.
     *
     * @param file
     * @return
     * @throws IOException            read error
     * @throws ClassNotFoundException object serializer error
     */
    public static NeuralNet load(File file) throws IOException, ClassNotFoundException {
        ObjectInputStream in = new ObjectInputStream(new FileInputStream(file));
        NeuralNet net = (NeuralNet) in.readObject();
        in.close();
        return net;
    }

    public static void main(String[] args) {

        SAPSet SAPSet = new SAPSet();
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

            SAPSet.addTuple(inputs, outputs);
        }
        List<StateActionPair> trainData = SAPSet.splitData(true);

        NeuralNet n1 = new NeuralNet(new Properties(), trainData.get(0).getInputs().size(), 10, trainData.get(0).getOutputs().size());
        n1.train(trainData, 1_000, 0.7);
        System.out.println(n1);
    }

    public List<Double> test(List<StateActionPair> stateActionPair) {
        List<Double> outputs = new ArrayList<>();
        int errorCount = 0;
        for (StateActionPair saPair : stateActionPair) {
            feedForward(saPair.getInputs());
            Layer ouputL = getOutputLayer();

            double maxOutput = Double.MIN_VALUE;
            int outIndex = -1;
            for (int i = 0; i < ouputL.size(); i++) {
                Neuron n = ouputL.getNeurons().get(i);
                if (n.output > maxOutput) {
                    maxOutput = n.output;
                    outIndex = i;
                }
                outputs.add(n.output);
            }
            if (outIndex > -1 && saPair.getOutputs().get(outIndex) == 0.0) errorCount++;
        }
        double err = errorCount * 100 / stateActionPair.size();
        //System.out.println("Test result. Error: " + err + "%");
        return outputs;
    }

    public void train(List<StateActionPair> stateActionPair, int epochs, double value) {
        //learningRate = 2;
        double initialLR = 1;
        int epoch = 0;
        int errorCount = 0;
        double largest_delta_w = Double.MIN_VALUE;
        double minError = Double.MAX_VALUE;
        while (true) {
            double SAPairErrCount = 0;
            for (StateActionPair saPair : stateActionPair) {

                feedForward(saPair.getInputs());

                // ================= UPDATE ERRORS FOR OUTPUT LAYER ====================
                Layer ouputL = getOutputLayer();

                double bestOutput = Double.MIN_VALUE;
                int bestIndex = -1;
                for (int i = 0; i < ouputL.size(); i++) {
                    Neuron n = ouputL.getNeurons().get(i);
                    n.error = n.output * (1 - n.output) * (saPair.getOutputs().get(i) - n.output);
                    // == for Error calculation ==
                    if (n.output > bestOutput) {
                        bestOutput = n.output;
                        bestIndex = i;
                    }
                    if (Math.abs(n.error) < minError) minError = Math.abs(n.error);
                }

                // ================= UPDATE ERRORS FOR HIDDEN LAYERS ===================
                for (int i = layers.size() - 2; i >= 0; i--) {
                    Layer hiddenL = layers.get(i);
                    Layer prevL = layers.get(i + 1);
                    List<Neuron> hiddenNeurons = hiddenL.getNeurons();
                    List<Neuron> prevNeurons = prevL.getNeurons();

                    for (int j = 0; j < hiddenL.size(); j++) {
                        double error = 0;
                        Neuron n = hiddenNeurons.get(j);
                        for (int p = 0; p < prevL.size(); p++) {
                            Neuron synapse = prevNeurons.get(p);
                            error += synapse.error * synapse.weights.get(j);
                        }
                        n.error = hiddenL.outputs.get(j) * (1 - hiddenL.outputs.get(j)) * error;
                    }
                }
                // update only weights for mispredicted pairs
                if (saPair.getOutputs().get(bestIndex) == 0.0 || ouputL.outputs.get(bestIndex) < value) {
                    SAPairErrCount++;
                    // =============== UPDATE WEIGHTS AND BIASES =================
                    for (Layer _layer : layers) {
                        for (Neuron n : _layer.getNeurons()) {
                            for (int w = 0; w < n.weights.size(); w++) {
                                double delta_w = learningRate * n.error * n.inputs.get(w);
                                double updWeight = n.weights.get(w) + delta_w;
                                n.weights.set(w, updWeight);
                                if (delta_w > largest_delta_w)
                                    largest_delta_w = delta_w;

                            }
                            double delta_b = learningRate * n.error;
                            n.bias += delta_b;
                        }
                    }
                }
                /*if (epoch > epochs * 0.8) {
                System.out.println("Epoch: " + epoch);
                System.out.println("Actual output: " + t.getOutputs().toString());
                System.out.println("Got output: " + ouputL.outputs.toString());
                System.out.println("================================================================================\n");
                 }*/
            }
            double midErr = SAPairErrCount * 100 / stateActionPair.size();

            if (epoch % 1000 == 0)
                System.out.printf("Epoch: %d, Error: %s, LR: %s\n", epoch, midErr, learningRate);

            double newRate = initialLR * midErr / 100;
            if (epoch > 1)
                learningRate = newRate < learningRate ? newRate : learningRate;
            errorCount += SAPairErrCount;

            // ============================== SAVE =================================
            if (midErr < error) {
                error = midErr;
                saveToFile(properties.getProperty("game.neural"), this);
            }
            // ============================== STOP =================================
            if (epoch >= epochs)
                break;
            epoch++;
        }
        double newError = (double) errorCount * 100 / (stateActionPair.size() * (epoch + 1));
        error = newError < error ? newError : error;
        System.out.println("Error of training: " + error + "%");
    }

    public Layer getOutputLayer() {
        return layers.get(layers.size() - 1);
    }

    /**
     * Executes the entire NN net and returns the output of the
     * top most layer in the net.
     *
     * @param inputs
     * @return
     */
    public List<Double> feedForward(List<Double> inputs) {
        return feedForward(layers.size() - 1, inputs);
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
            return layers.get(layer).getOutputs(inputs);
        else
            return layers.get(layer).getOutputs(feedForward(layer - 1, inputs));
    }

    public double getError() {
        return error;
    }

    public double getLearningRate() {
        return learningRate;
    }

    public List<Layer> getLayers() {
        return layers;
    }

    public void setLayers(List<Layer> layers) {
        this.layers = layers;
    }
}
