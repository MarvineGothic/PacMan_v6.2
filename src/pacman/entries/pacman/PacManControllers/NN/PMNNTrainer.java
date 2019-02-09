package pacman.entries.pacman.PacManControllers.NN;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;
import pacman.entries.pacman.NeuralNetwork.NeuralNet;
import pacman.entries.pacman.NeuralNetwork.SAPSet;
import pacman.entries.pacman.NeuralNetwork.StateActionPair;
import pacman.entries.pacman.utils.CollectedData;
import pacman.entries.pacman.utils.DataTuple;

import java.util.List;
import java.util.Properties;

import static pacman.entries.pacman.utils.Utils.*;

public class PMNNTrainer {

    public static void main(String[] args) {

        Properties properties = getProperties(PACMAN_PROPERTIES);

        SAPSet SAPSet = createDataTuple(properties);
        List<StateActionPair> trainData = SAPSet.splitData(false);
        List<StateActionPair> testData = SAPSet.getTestData();
        System.out.println("Train data size: " + trainData.size());
        double minError;
        for (int i = 0; i < 1000_000; i++) {
            NeuralNet nn;
            nn = (NeuralNet) loadFromFile(properties.getProperty("game.neural"));
            if (nn == null)
                nn = new NeuralNet( properties, trainData.get(0).getInputs().size(), 20, 10, trainData.get(0).getOutputs().size());


            minError = nn.getError();
            System.out.printf("NN Error: %s LR: %s\n\n", nn.getError(), nn.getLearningRate());
            NeuralNet finalNn = nn;
            //nn.train(trainData, 1_000_000, 0.7);
            Kernel kernel = new Kernel() {
                @Override
                public void run() {
                    finalNn.train(trainData, 100_000_000,0.7);
                }
            };

            kernel.execute(Range.create(1));
            kernel.dispose();

            if (nn.getError() < minError) {
                saveToFile(properties.getProperty("game.neural"), nn);
                nn.test(testData);
            }
        }

    }

    public static SAPSet createDataTuple(Properties properties) {
        SAPSet SAPSet = new SAPSet();
        CollectedData cd = (CollectedData) loadFromFile(properties.getProperty("game.cd"));

        if (cd != null)
            for (DataTuple tuple : cd.dataTuples)
                SAPSet.addTuple(createSAP(tuple));

        return SAPSet;
    }

    static StateActionPair createSAP(DataTuple tuple) {
        StateActionPair tt = new StateActionPair();
        for (int i = 0; i < 4; i++) {
            if (i == tuple.DirectionChosen.ordinal()) {
                tt.addOutput(1.0);
            } else {
                tt.addOutput(0.0);
            }
        }
        tt.setInputs(tuple.getNormalizedData(0, 2, 4, 5, 6, 7, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24));
        return tt;
    }
}
