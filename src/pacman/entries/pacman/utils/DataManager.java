package pacman.entries.pacman.utils;

import pacman.entries.pacman.NeuralNetwork.SAPSet;
import pacman.entries.pacman.NeuralNetwork.StateActionPair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import static pacman.entries.pacman.PacManControllers.NN.PMNNTrainer.createDataTuple;
import static pacman.entries.pacman.utils.Utils.PACMAN_PROPERTIES;
import static pacman.entries.pacman.utils.Utils.getProperties;

@SuppressWarnings("all")
public class DataManager {
    public static final String DIRECTORY = "./src/pacman/entries/pacman/Files/saves/collectedData/";
    public static final String CSV_DELIMETER = ";";

    /**
     * Save CSV File in the specified directory (It must exist in the project)
     *
     * @param filename Name of file to be created/written on.
     * @param content  Content of the csv filled with the generational statistics
     * @param append   If you wish to append to an existing file or override it
     * @return true if it could create and save the file, false otherwise
     */
    public static boolean SaveCSVFile(String filename, String content, boolean append) {
        try {
            FileOutputStream outS = new FileOutputStream(DIRECTORY + filename, append);
            PrintWriter pw = new PrintWriter(outS);

            pw.println(content);
            pw.flush();
            outS.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    /**
     * The read method reads in a csv file as a two dimensional string array.
     * This method is utilizes the string.split method for splitting each line of the data file.
     * String tokenizer bug fix provided by Martin Marcher.
     *
     * @param csvFile        File to load
     * @param seperationChar Character used to seperate entries
     * @param nullValue      What to insert in case of missing values
     * @return Data file content as a 2D string array
     * @throws IOException
     */
    public static String[][] readDataFile(String csvFile, String seperationChar, String nullValue, boolean skipHeaderRow) throws IOException {

        List<String[]> lines = new ArrayList<String[]>();
        BufferedReader bufRdr = new BufferedReader(new FileReader(new File(csvFile)));

        // read the header
        String line = bufRdr.readLine();

        while ((line = bufRdr.readLine()) != null) {
            String[] arr = line.split(seperationChar);

            for (int i = 0; i < arr.length; i++) {
                if (arr[i].equals("")) {
                    arr[i] = nullValue;
                }
            }
            if (!skipHeaderRow) {
                lines.add(arr);
            }
        }

        String[][] ret = new String[lines.size()][];
        bufRdr.close();
        return lines.toArray(ret);
    }


    public static void main(String[] args) {
        Properties properties = getProperties(PACMAN_PROPERTIES);
        SAPSet SAPSet = createDataTuple(properties);
        System.out.println(SAPSet);
        StringBuilder sb = new StringBuilder();
        sb.append("inputs" + CSV_DELIMETER + "outputs" + System.getProperty("line.separator"));

        List<StateActionPair> data = SAPSet.getData();
        for (int i = 0; i < data.size(); i++) {

            List<Double> inputList = data.get(i).getInputs();
            List<Double> outputList = data.get(i).getOutputs();

            Double[] inputs = inputList.toArray(new Double[inputList.size()]);
            Double[] outputs = outputList.toArray(new Double[outputList.size()]);

            sb.append(Arrays.toString(inputs).replaceAll("[]\\[]", "") + CSV_DELIMETER + Arrays.toString(outputs).replaceAll("[]\\[]", "") + System.getProperty("line.separator"));
        }

        SaveCSVFile("cd_000.csv", sb.toString(), false);
    }

    private void testCVSReader() {
        try {
            String[][] data = readDataFile("cd_000.csv", "\",\"", "-", false);

            //Print all the data
            for (String[] line : data) {
                System.out.println(Arrays.toString(line));
            }

            //Print a specific entry in the data
            //System.out.println(Arrays.toString(data[1]));
            System.out.println("Number of tuples loaded: " + data.length);
        } catch (IOException e) {
            System.err.println(e.getLocalizedMessage());
        }
    }
}