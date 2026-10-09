import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;

public class SliceFilter {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    // Behavior should match Sequential.takeSlice.
    // Ignoring the initialization of arrays, your implementation must have linear
    // work and log(n) span
   public static double[][] takeSlice(double[][] arr, double[] point, double[] normal, int cutoff) {
        SliceFilter.CUTOFF = cutoff;
        int n = arr.length;

        // Precompute once
        double planeDP = dotProduct(point, normal);

        // Step 1 & 2: Map to counts in parallel
        // prefix[i] = number of valid elements up to index i (inclusive)

        int[] countArray = new int[n];
        POOL.invoke(new MapToCounts(arr, normal, planeDP, countArray, 0, n));

         // Step 3: Allocate result array of correct size
        int[] prefixResult = ParallelPrefix.prefixSum(countArray, cutoff);

        // Step 4: Initialize result array
        double[][] result = new double[prefixResult[n - 1]][];

        // Step 5: Pack in parallel
        POOL.invoke(new PackResult(arr, countArray, prefixResult, result, 0, n));

        return result;
}

    // Parallel packing task: places valid points into result array

    private static class PackResult extends RecursiveAction {
        private final double[][] arr;
        private final int[] counts;
        private final int[] prefix;
        private final double[][] result;
        private final int lo, hi;

    public PackResult(double[][] arr, int[] counts, int[] prefix, double[][] result, int lo, int hi) {
        this.arr = arr; this.counts = counts; this.prefix = prefix;
        this.result = result; this.lo = lo; this.hi = hi;
    }

    protected void compute() {
        if (hi - lo <= CUTOFF) {
            for (int i = lo; i < hi; i++) {
                if (counts[i] == 1) {
                    // prefix[i] gives the 1-based index of where this element goes
                    result[prefix[i] - 1] = arr[i];
                }
            }
        } else {
              // Recursive case: split in half
            int mid = lo + (hi - lo) / 2;
            PackResult left = new PackResult(arr, counts, prefix, result, lo, mid);
            PackResult right = new PackResult(arr, counts, prefix, result, mid, hi);
            left.fork();
            right.compute();
            left.join();
        }
    }
}
    


    private static double dotProduct(double[] a, double[] b) {
        return DotProduct.dotProduct(a, b, SliceFilter.CUTOFF);

    }

    private static class MapToCounts extends RecursiveAction {
        private final double[][] arr;
        private final double[] normal;
        private final double planeDP;
        private final int[] counts;
        private final int lo, hi;

        // Parallel map task: determines whether each point lies on plane
        public MapToCounts(double[][] arr, double[] normal, double planeDP, int[] counts, int lo, int hi) {
            this.arr = arr; this.normal = normal; this.planeDP = planeDP;
            this.counts = counts; this.lo = lo; this.hi = hi;
        }

        protected void compute() {
            if (hi - lo <= CUTOFF) {
                for (int i = lo; i < hi; i++) {
                    // Point lies on plane if dot(point, normal) == planeDP
                    counts[i] = Sequential.approxEquals(dotProduct(arr[i], normal), planeDP) ? 1 : 0;
                }
            }  else {
                int mid = lo + (hi - lo) / 2;
                MapToCounts left = new MapToCounts(arr, normal, planeDP, counts, lo, mid);
                MapToCounts right = new MapToCounts(arr, normal, planeDP, counts, mid, hi);
                left.fork();
                right.compute();
                left.join();
            } 
        }
    }
}
