package pacman.entries.pacman.utils;

import java.util.stream.IntStream;

public class GPUBenchmark {
    public static void main(String[] args) {
        float[] a = new float[2000000];
        for (int i = 0; i < a.length; i++) {
            a[i] = (float) Math.random();
        }
        float[] b = new float[a.length];
        final float[] c = {0};

        System.out.println("GPU:");
        double startTime = System.currentTimeMillis();
        IntStream.range(0, a.length).parallel().forEach(i -> {
            b[i] = (float) Math.pow(a[i] * 2, 10);
            c[0] += Math.pow(b[i], 10);
            //System.out.println(b[i]);
        });
        double endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time:"+endTime);
        System.out.println(c[0]);

        c[0] = 0;
        System.out.println("CPU:");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < a.length; i++) {
            b[i] = (float) Math.pow(a[i] * 2, 10);
            c[0] += Math.pow(b[i], 10);
            //System.out.println(b[i]);
        }
        endTime = System.currentTimeMillis() - startTime;
        System.out.println("Time:"+endTime);
        System.out.println(c[0]);
    }
}
