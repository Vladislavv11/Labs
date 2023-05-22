import java.util.Arrays;
import java.util.Random;
import java.util.stream.IntStream;

public class Main {
    static final int N = 1000;
    public static void main(String[] args) {
        compareSleeps(0,0);
        compareSleeps(0,1);
        compareSleeps(1,0);
        compareSleeps(1,1);
    }

    private static void compareSleeps(int sleep1, int sleep2) {
        int[] input1 = new int[N], input2 = new int[N], result = new int[N];
        Random random = new Random();
        Arrays.setAll(input1, i -> (int) (Math.random() * 101));
        Arrays.setAll(input2, i -> (int) (Math.random() * 101));

        // Synchronous
        long time1 = System.currentTimeMillis();

        for (int i = 0; i < N; i++) {
            result[i] = input1[i] * input2[i];
                try {
                    Thread.sleep(sleep1);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
        }
        System.out.printf("Sync: %d\n", System.currentTimeMillis() - time1);

        // Parallel
        long time2 = System.currentTimeMillis();
        IntStream.range(0, N).parallel().forEach(i -> {
            result[i] = input1[i] * input2[i];
            try {
                Thread.sleep(sleep2);
            } catch (InterruptedException ex) {
                throw new RuntimeException(ex);
            }
        });

        System.out.printf("Parallel: %d\n", System.currentTimeMillis() - time2);
    }
}
