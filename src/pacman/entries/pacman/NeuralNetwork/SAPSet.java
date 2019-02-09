package pacman.entries.pacman.NeuralNetwork;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for a set of State-Action Pairs (SAP)
 * Instance can be either created from existing list of SAP data
 * of added to instance later.
 * Data can also be normally distributed for labels and then split to trainSet and testSet
 */
public class SAPSet {
    private List<StateActionPair> data;
    private List<StateActionPair> homogeneousData;
    private List<StateActionPair> trainingData;
    private List<StateActionPair> testData;

    public SAPSet(List<StateActionPair> data) {
        this();
        this.data = data;
    }

    public SAPSet() {
        this.data = new ArrayList<>();
        this.homogeneousData = new ArrayList<>();
        this.trainingData = new ArrayList<>();
        this.testData = new ArrayList<>();
    }

    public List<StateActionPair> splitData(boolean homogen) {
        homogeneous(homogen);
        int training = (int) (homogeneousData.size() * 0.7);
        trainingData = homogeneousData.subList(0, training);
        testData = homogeneousData.subList(training, homogeneousData.size() - 1);
        return trainingData;
    }

    /*public List<StateActionPair> getTrainingData() {
        //splitData(homogen);
        return trainingData;
    }*/

    public List<StateActionPair> getTestData() {
        //splitData();
        return testData;
    }

    private void homogeneous(boolean val) {
        if (val){
        List<StateActionPair> up = new ArrayList<>();
        List<StateActionPair> right = new ArrayList<>();
        List<StateActionPair> down = new ArrayList<>();
        List<StateActionPair> left = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            StateActionPair sap = data.get(i);
            if (sap.getOutputs().get(0) == 1.0) up.add(sap);
            if (sap.getOutputs().get(1) == 1.0) right.add(sap);
            if (sap.getOutputs().get(2) == 1.0) down.add(sap);
            if (sap.getOutputs().get(3) == 1.0) left.add(sap);
        }
        int minSize = Math.min(Math.min(up.size(), right.size()), Math.min(down.size(), left.size()));
        for (int i = 0; i < minSize; i++) {

            homogeneousData.add(left.get(i));
            homogeneousData.add(up.get(i));
            homogeneousData.add(right.get(i));
            homogeneousData.add(down.get(i));
        }}
        else homogeneousData = data;
    }

    public void addTuple(List<Double> input, List<Double> output) {
        data.add(new StateActionPair(input, output));
    }

    public void addTuple(StateActionPair stateActionPair) {
        data.add(stateActionPair);
    }

    public List<StateActionPair> getData() {
        return data;
    }

    public List<StateActionPair> getHomogeneousData() {
        return homogeneousData;
    }

    public List<StateActionPair> getTrainingData() {
        return trainingData;
    }
}

