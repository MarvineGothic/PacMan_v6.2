package pacman.entries.pacman.NeuralNetwork.Aparapi;

import pacman.entries.pacman.NeuralNetwork.SAPSet;
import pacman.entries.pacman.NeuralNetwork.StateActionPair;

import java.util.List;
import java.util.Properties;

import static pacman.entries.pacman.NeuralNetwork.Aparapi.MatrixMultiplication.flattenArray;
import static pacman.entries.pacman.PacManControllers.NN.PMNNTrainer.createDataTuple;
import static pacman.entries.pacman.utils.Utils.PACMAN_PROPERTIES;
import static pacman.entries.pacman.utils.Utils.getProperties;

public class ApaNN {
    public static int hiddenLayerSize = 10;
    public static double[][] dataSet;
    public static double[][] input;
    public static double[][] expectedOut;
    public static double[][] weights1;
    public static double[][] weights2;
    public static double[][] output;
    public static double[] hiddenLayer1;

    public static double[] flatInputs;
    public static double[] flatExpectedOut;
    public static double[] flatWeights1;
    public static double[] flatWeights2;

    public static double sigmoid(double sum, double SLOPE_PARAMETER) {
        return 1.0 / (1 + Math.pow(Math.E, -sum * SLOPE_PARAMETER));
    }

    public static double sigmoidDerivative(double p) {
        return p * (1 - p);
    }

    public static void main(String[] args) {
        Properties properties = getProperties(PACMAN_PROPERTIES);
        SAPSet SAPSet = createDataTuple(properties);
        List<StateActionPair> data = SAPSet.getData();

        int x = data.size();
        int y = data.get(0).getInputs().size();
        input = new double[x][y];
        for (int i = 0; i < input.length; i++)
            for (int j = 0; j < y; j++)
                input[i][j] = data.get(i).getInputs().get(j);

        y = data.get(0).getOutputs().size();
        expectedOut = new double[x][y];
        for (int i = 0; i < expectedOut.length; i++)
            for (int j = 0; j < y; j++)
                expectedOut[i][j] = data.get(i).getOutputs().get(j);

        weights1 = new double[input[0].length][hiddenLayerSize];
        weights2 = new double[hiddenLayerSize][expectedOut[0].length];

        MatrixMultiplication hiddenLayerMM = new MatrixMultiplication(input, weights1);
        flatInputs = flattenArray(input);
        flatExpectedOut = flattenArray(expectedOut);
        flatWeights1 = new double[input[0].length * hiddenLayerSize];
        flatWeights2 = new double[hiddenLayerSize * expectedOut[0].length];
        for (int i = 0; i < flatWeights1.length; i++) flatWeights1[i] = Math.random();
        for (int i = 0; i < flatWeights2.length; i++) flatWeights2[i] = Math.random();

        hiddenLayerMM.setFlatA(flatInputs);
        hiddenLayerMM.setFlatB(flatWeights1);

                    // feedforward
            hiddenLayer1 = hiddenLayerMM.dot();
        System.out.println("");
    }
}
