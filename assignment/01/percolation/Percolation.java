import edu.princeton.cs.algs4.WeightedQuickUnionUF;

/*
 * This class doesn't solve Backwash Issue.
 * If you prefer to solve it, read that:
 * 1) https://www.sigmainfy.com/blog/avoid-backwash-in-percolation.html
 * 2) https://www.cs.princeton.edu/courses/archive/fall10/cos226/precepts/15UnionFind-Tarjan.pdf
 *
 * @author boussole
 */
public class Percolation {
    
    private int size;
    
    private int [][] sites;
    
    private int totalSites;
    
    private int nrOpenedSites;
    
    private int topSiteId;
    
    private int bottomSiteId;
    
    private WeightedQuickUnionUF uf;
    
   /**
     * Create n-by-n grid, with all sites blocked
     * 
     * @param n Grid size
     */
    public Percolation(int n) {
        if (n <= 0)
            throw new java.lang.IllegalArgumentException();
        
        sites = new int[n][n];

        size = n;
        totalSites = n * n;
        nrOpenedSites = 0;

        bottomSiteId = totalSites;
        topSiteId = totalSites + 1;

        uf = new WeightedQuickUnionUF(totalSites + 1 /* one top common */ + 1 /* one bottom common */);
    }

    /**
     * Check coords and throw exception if it isn't valid
     * 
     * @param row
     * @param col
     */
    private void checkPosition(int row, int col) {
        if (row < 1 || row > size || col < 1 || col > size)
            throw new java.lang.IndexOutOfBoundsException();
    }

    private void unionLeft(int i, int j) {
        int id = i * size + j;
        
        if ((j > 0) && (sites[i][j - 1] == 1)) {
            uf.union(id, i * size + j - 1);
        }
    }
    
    private void unionRight(int i, int j) {
        int id = i * size + j;
        
        if ((j < size - 1) && (sites[i][j + 1] == 1)) {
            uf.union(id, i * size + j + 1);
        }
    }
   
    private void unionAbove(int i, int j) {
        int id = i * size + j;
        
        if ((i > 0) && (sites[i - 1][j] == 1)) {
            uf.union(id, (i - 1) * size + j);
        }
    }
    
    private void unionBellow(int i, int j) {
        int id = i * size + j;
        
        if ((i < size - 1) && (sites[i + 1][j] == 1)) {
            uf.union(id, (i + 1) * size + j);
        }
    }
    
    /**
     * Open site (row, col) if it is not open already
     * 
     * @param row
     * @param col
     */
    public void open(int row, int col) {
        checkPosition(row, col);
        
        // normalize to zero index
        int i = row - 1;
        int j = col - 1;
        int id = i * size + j;
        
        if (sites[i][j] == 0) {
            sites[i][j] = 1;
            ++nrOpenedSites;
            
            if (row == 1) {
                uf.union(topSiteId, id);
            }

            if (row == size) {
                uf.union(bottomSiteId, id);
            }
            
            unionAbove(i, j);
            unionBellow(i, j);
            unionLeft(i, j);
            unionRight(i, j);
        }
    }

    /**
     * Is site (row, col) open?
     * 
     * @param row
     * @param col
     * @return is site opened or not
     */
    public boolean isOpen(int row, int col) {
        checkPosition(row, col);
        
        return sites[row - 1][col - 1] == 1;
    }
    
    /**
     * Is site (row, col) full?
     * 
     * @param row
     * @param col
     * @return Full or not a site
     */
    public boolean isFull(int row, int col) {
        checkPosition(row, col);
        
        if (sites[row - 1][col - 1] == 1) {
            return uf.connected(topSiteId, (row - 1) * size + col - 1);
        }
        
        return false;
    }

    /**
     * Number of open sites
     * 
     * @return Number of opened sites
     */
    public int numberOfOpenSites() {
        return nrOpenedSites;
    }
    
    /**
     * Does the system percolate?
     * 
     * @return is system percoleted or not
     */
    public boolean percolates() {
        return uf.connected(topSiteId, bottomSiteId);
    }

    private void print() {
        for (int i = 0; i < size; ++i) {
            for (int j = 0; j < size; ++j) {
                if (isFull(i + 1, j + 1)) {
                    System.out.printf(" F");
                }
                else {
                    System.out.printf(" %d", sites[i][j]);
                }
            }
            System.out.println();
        }
    }
    
    /**
     * test client (optional)
     * 
     * @param args
     */
    public static void main(String[] args) {
        System.out.println("Percolates picture..."); 
        Percolation pic1 = new Percolation(8);
        pic1.open(2, 1);
        pic1.open(3, 1);
        pic1.open(3, 2);
        pic1.open(3, 3);
        pic1.open(4, 3);
        pic1.open(4, 4);
        pic1.open(5, 2);
        pic1.open(5, 3);
        pic1.open(5, 4);
        pic1.open(6, 2);
        pic1.open(7, 1);
        pic1.open(7, 3);
        pic1.open(8, 1);
        pic1.open(8, 2);
        pic1.open(8, 3);
        pic1.open(8, 4);
        pic1.open(1, 3);
        pic1.open(1, 4);
        pic1.open(1, 5);
        pic1.open(2, 4);
        pic1.open(2, 4);
        pic1.open(2, 5);
        pic1.open(2, 6);
        pic1.open(2, 7);
        pic1.open(2, 8);
        pic1.open(3, 6);
        pic1.open(3, 7);
        pic1.open(4, 6);
        pic1.open(4, 7);
        pic1.open(4, 8);
        pic1.open(5, 6);
        pic1.open(5, 7);
        pic1.open(6, 7);
        pic1.open(6, 8);
        pic1.open(7, 5);
        pic1.open(7, 6);
        pic1.open(7, 7);
        pic1.open(7, 8);
        pic1.open(8, 6);
        pic1.print();
        /* here some of sites is marked as full, it's a 'Backwash Issue' (see comment to class) */
        System.out.printf("Number opened sites: %d\nPercolates: %b\n", pic1.numberOfOpenSites(), pic1.percolates());
        
        System.out.println("\nDoes not percolates picture...");
        Percolation pic2 = new Percolation(8);
        pic2.open(1, 3);
        pic2.open(1, 4);
        pic2.open(1, 6);
        pic2.open(2, 1);
        pic2.open(2, 2);
        pic2.open(2, 3);
        pic2.open(2, 4);
        pic2.open(2, 5);
        pic2.open(3, 1);
        pic2.open(3, 2);
        pic2.open(3, 5);
        pic2.open(3, 6);
        pic2.open(4, 3);
        pic2.open(4, 4);
        pic2.open(4, 5);
        pic2.open(4, 6);
        pic2.open(4, 7);
        pic2.open(4, 3);
        pic2.open(5, 1);
        pic2.open(5, 7);
        pic2.open(5, 8);
        pic2.open(6, 2);
        pic2.open(6, 4);
        pic2.open(6, 5);
        pic2.open(6, 6);
        pic2.open(7, 2);
        pic2.open(7, 3);
        pic2.open(7, 5);
        pic2.open(7, 6);
        pic2.open(7, 8);
        pic2.open(8, 1);
        pic2.open(8, 3);
        pic2.open(8, 7);
        pic2.print();
        System.out.printf("Number opened sites: %d\nPercolates: %b\n", pic2.numberOfOpenSites(), pic2.percolates());
    }
}
