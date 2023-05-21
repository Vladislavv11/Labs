import java.util.Scanner;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class Main {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Enter n: ");
        int n = scanner.nextInt();
        
        CompletableFuture<Integer> completableFuture = CompletableFuture.supplyAsync(() -> fib(n));
            System.out.println("Waiting for result");
            int result = completableFuture.get();
            System.out.println("Fibonacci number "+n+" = "+result);
    }

    static int fib(int n)
    {
        if (n <= 1)
            return n;
        return fib(n - 1) + fib(n - 2);
    }
}
