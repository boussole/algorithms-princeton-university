import edu.princeton.cs.algs4.StdIn;
import edu.princeton.cs.algs4.StdRandom;
import edu.princeton.cs.algs4.StdStats;

public class PercolationStats {

    private int n, size;

    private double[] threshold;
    
    private double mean;
    
    private double standardDeviation;

    private double confidenceLo, confidenceHi;

    /**
     * Perform trials independent experiments on an n-by-n grid
     * 
     * @param n
     * @param trials
     */
    public PercolationStats(int n, int trials) {
        if ((n <= 0) || (trials <= 0))
            throw new java.lang.IllegalArgumentException();
        
        this.n = n;
        this.size = n * n;
        
        threshold = new double[trials];
        
        for (int i = 0; i < trials; ++i) {
            threshold[i] = doTrial();
        }
        
        mean = StdStats.mean(threshold);
        standardDeviation = StdStats.stddev(threshold);
        
        confidenceLo = mean - (1.96 * standardDeviation / Math.sqrt(trials));
        confidenceHi = mean + (1.96 * standardDeviation / Math.sqrt(trials));
    }

    /**
     * Percolate system
     * 
     * @return percolation threshold
     */
    private double doTrial() {
        Percolation prc = new Percolation(n);
        
        do {
            int row = StdRandom.uniform(n) + 1;
            int col = StdRandom.uniform(n) + 1;
            
            // don't do any actions if it's chosen opened site 
            while (prc.isOpen(row, col)) {
                row = StdRandom.uniform(n) + 1;
                col = StdRandom.uniform(n) + 1;
            }
            
            prc.open(row, col);

        } while (!prc.percolates());
        
        return (double) prc.numberOfOpenSites() / size;
    }
    
    /**
     * Sample mean of percolation threshold
     * 
     * @return mean of percolation threshold
     */
    public double mean() {
        return mean;
    }

    /**
     * Sample standard deviation of percolation threshold
     * 
     * @return standard deviation of percolation threshold
     */
    public double stddev() {
        return standardDeviation;
    }

    /**
     * Low endpoint of 95% confidence interval
     * 
     * @return low endpoint of 95% confidence interval
     */
    public double confidenceLo() {
        return confidenceLo;
    }

    /**
     * High endpoint of 95% confidence interval
     * 
     * @return high endpoint of 95% confidence interval
     */
    public double confidenceHi() {
        return confidenceHi;
    }
    
    /**
     * Takes two command-line arguments n and T, performs T independent computational experiments on an n-by-n grid, 
     * and prints the sample mean, sample standard deviation, and the 95% confidence interval for the percolation threshold.
     * 
     * @param args command line params
     */
    public static void main(String[] args) {
        /* 1 argument: n; 2 argument: T. */
        PercolationStats stats = new PercolationStats(StdIn.readInt(), StdIn.readInt());

        System.out.printf("mean                    = %f\n" +
                          "stddev                  = %f\n" +
                          "95%% confidence interval = [%f, %f]",
                          stats.mean(), stats.stddev(),
                          stats.confidenceLo(), stats.confidenceHi());
    }

}
