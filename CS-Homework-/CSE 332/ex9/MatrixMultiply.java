import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveAction;
import java.util.concurrent.RecursiveTask;

public class MatrixMultiply {
    static ForkJoinPool POOL = new ForkJoinPool();
    static int CUTOFF;

    public static double[][] multiply(double[][] x, double[][] y, int cutoff) {
        MatrixMultiply.CUTOFF = cutoff;
        double[][] product = new double[x.length][y[0].length];
        POOL.invoke(new MatrixMultiplyAction(x, y, product, 0, x.length, 0, y[0].length));
        return product;
    }

    public static double dotProduct(double[][] x, double[][] y, int row, int col, int cutoff) {
        MatrixMultiply.CUTOFF = cutoff;
        return POOL.invoke(new DotProductTask(x, y, row, col, 0, x[0].length));
    }

    private static class MatrixMultiplyAction extends RecursiveAction {
        private final double[][] X, Y, Z;
        private final int rowMin, rowMax, colMin, colMax;

        public MatrixMultiplyAction(double[][] X, double[][] Y, double[][] Z,
                                    int rowMin, int rowMax, int colMin, int colMax) {
            this.X = X;
            this.Y = Y;
            this.Z = Z;
            this.rowMin = rowMin;
            this.rowMax = rowMax;
            this.colMin = colMin;
            this.colMax = colMax;
        }

        
        public void compute() {
            int rowLength = rowMax - rowMin;
            int colLength = colMax - colMin;

            if (rowLength <= CUTOFF || colLength <= CUTOFF) {
                for (int i = rowMin; i < rowMax; i++) {
                    for (int j = colMin; j < colMax; j++) {
                        DotProductTask task = new DotProductTask(X, Y, i, j, 0, X[0].length);
                        task.fork();
                        Z[i][j] = task.join();
                    }
                }
                return;
            }

            int rowMid = rowMin + rowLength / 2;
            int colMid = colMin + colLength / 2;

            MatrixMultiplyAction q1 = new MatrixMultiplyAction(X, Y, Z, rowMin, rowMid, colMin, colMid);
            MatrixMultiplyAction q2 = new MatrixMultiplyAction(X, Y, Z, rowMin, rowMid, colMid, colMax);
            MatrixMultiplyAction q3 = new MatrixMultiplyAction(X, Y, Z, rowMid, rowMax, colMin, colMid);
            MatrixMultiplyAction q4 = new MatrixMultiplyAction(X, Y, Z, rowMid, rowMax, colMid, colMax);

            q1.fork(); q2.fork(); q3.fork();
            q4.compute();
            q3.join(); q2.join(); q1.join();
        }
    }

    private static class DotProductTask extends RecursiveTask<Double> {
        private final double[][] X, Y;
        private final int row, col, lo, hi;

        public DotProductTask(double[][] X, double[][] Y, int row, int col, int lo, int hi) {
            this.X = X;
            this.Y = Y;
            this.row = row;
            this.col = col;
            this.lo = lo;
            this.hi = hi;
        }

        @Override
        public Double compute() {
            if (hi - lo <= CUTOFF) {
                double sum = 0;
                for (int k = lo; k < hi; k++) {
                    sum += X[row][k] * Y[k][col];
                }
                return sum;
            }

            int mid = lo + (hi - lo) / 2;
            DotProductTask left = new DotProductTask(X, Y, row, col, lo, mid);
            DotProductTask right = new DotProductTask(X, Y, row, col, mid, hi);

            left.fork();
            double rightResult = right.compute();
            return rightResult + left.join();
        }
    }
}