import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class DotProduct {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    // Behavior should match Sequential.dotProduct
    // Your implementation must have linear work and log(n) span
    public static double dotProduct(double[] a, double[]b, int cutoff){
        DotProduct.CUTOFF = cutoff;
        return POOL.invoke(new DotProductTask(a,b,0,a.length)); // TODO: add parameters to match your constructor
    }

    private static class DotProductTask extends RecursiveTask<Double>{
        // select fields
        double[] a;
        double[] b;
        int low;
        int high;


        public DotProductTask(double[] a, double[] b, int low, int high){
            // implement constructor
            this.a = a;
            this.b = b;
            this.high = high;
            this.low = low;
        }

        public Double compute(){
            //establish base case
            if(high - low <= CUTOFF){
                double x = 0;
                for (int i = low; i < high; i++) {
                    x += a[i] * b[i];
                }
                return x;
            }
            //recursive step : find midpoint, then fork to compute
            int middle = low + (high - low) / 2;
            DotProductTask left = new DotProductTask(a, b, low, middle);
            DotProductTask right = new DotProductTask(a, b, middle, high);
            left.fork();
            return right.compute() + left.join();
        }
    }
    
}
