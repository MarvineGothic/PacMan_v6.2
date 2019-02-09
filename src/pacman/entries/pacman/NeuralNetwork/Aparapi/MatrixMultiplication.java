package pacman.entries.pacman.NeuralNetwork.Aparapi;

import com.amd.aparapi.Kernel;
import com.amd.aparapi.Range;

public class MatrixMultiplication extends Kernel {

    private static long time;
    private final int ROWS, A_COLS, B_ROWS, COLS, A_COLS_B_ROWS;
    private double matA[][], matB[][], flatC[], flatD[], flatA[], flatB[];

    public MatrixMultiplication(double[][] matA, double[][] matB) {
        this.matA = matA;
        this.matB = matB;
        this.ROWS = matA.length;
        this.A_COLS = matA[0].length;
        this.B_ROWS = matB.length;
        this.COLS = matB[0].length;
        if (A_COLS != B_ROWS)
            throw new IllegalArgumentException("Arrays dimension are different!\n The multiplication of matrices is undefined");
        else this.A_COLS_B_ROWS = A_COLS;
        this.flatC = new double[ROWS * COLS];
        this.flatD = new double[ROWS * COLS];
    }

    public static void main(String[] args) {
        // Width of the matrix
        /*int[][] initA = new int[][]{{7, 3}, {2, 5}, {6, 8}, {9, 0}};// 4 x 2
        int[][] initB = new int[][]{{7, 4, 9}, {8, 1, 5}}; // 2 x 3*/

        /*initA = new int[][]{{1, 2}, {5, 2}};
        initB = new int[][]{{2, 4}, {3, 2}};*/

        MatrixMultiplication mm = new MatrixMultiplication(new double[100][20], new double[20][100]);
        mm.flattenM();
        mm.createRandomMatrices();

        mm.runCPU();

        /*mm.execute(1);
        // Array size for GPU to know
        Range range = Range.create(1_000_000);
        System.out.println("Starting GPU computation");
        time = System.currentTimeMillis();
        mm.execute(range); // Running the Kernel
        System.out.println("Task finished in " + (System.currentTimeMillis() - time) + "ms");*/
        mm.dot();

        mm.verifyResults();
    }

    public static double[] flattenArray(double[][] array) {
        int rows = array.length;
        int cols = array[0].length;
        double[] flatArray = new double[rows * cols];
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                flatArray[x * cols + y] = array[x][y];
            }
        }
        return flatArray;
    }

    private void flattenM() {
        this.flatA = flattenArray(matA);
        this.flatB = flattenArray(matB);
    }

    public double[] dot() {
        this.execute(1);
        Range range = Range.create(1_000_000);
        time = System.currentTimeMillis();
        this.execute(range); // Running the Kernel
        System.out.println("Task finished in " + (System.currentTimeMillis() - time) + "ms");
        return flatD;
    }

    private void verifyResults() {
        // Verifying the result
        for (int i = 0; i < COLS; i++) {
            for (int j = 0; j < ROWS; j++) {
                System.out.println("C: " + flatC[i * ROWS + j]);
                System.out.println("D: " + flatD[i * ROWS + j]);
                if (flatC[i * ROWS + j] != flatD[i * ROWS + j]) {
                    System.out.println("ERROR");
                    return;
                }
            }
        }
    }

    private void runCPU() {
        time = System.currentTimeMillis();
        System.out.println();
        System.out.println("Sequential Execution on CPU");
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int sum = 0;
                for (int k = 0; k < A_COLS_B_ROWS; k++) {
                    sum += flatA[i * A_COLS_B_ROWS + k] * flatB[k * COLS + j];
                }
                flatC[i * COLS + j] = sum;
            }
        }
        System.out.println("Task finished in " + (System.currentTimeMillis() - time) + "ms");
    }

    private void createRandomMatrices() {
        for (int i = 0; i < flatA.length; i++)
            flatA[i] = (int) (Math.random() * 100);
        for (int i = 0; i < flatB.length; i++)
            flatB[i] = (int) (Math.random() * 100);
    }

    @Override
    public void run() {
        int row = getGlobalId() / ROWS;
        int col = getGlobalId() % COLS;
        if (row > ROWS || col > COLS) return;

        flatD[row * COLS + col] = 0;

        for (int k = 0; k < A_COLS_B_ROWS; k++) {
            flatD[row * COLS + col] += flatA[row * A_COLS_B_ROWS + k] * flatB[k * COLS + col];
        }
    }

    public int getROWS() {
        return ROWS;
    }

    public int getA_COLS() {
        return A_COLS;
    }

    public int getB_ROWS() {
        return B_ROWS;
    }

    public int getCOLS() {
        return COLS;
    }

    public int getA_COLS_B_ROWS() {
        return A_COLS_B_ROWS;
    }

    public void setFlatA(double[] flatA) {
        this.flatA = flatA;
    }

    public void setFlatB(double[] flatB) {
        this.flatB = flatB;
    }
}