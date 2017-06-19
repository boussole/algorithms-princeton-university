import java.util.LinkedList;
// import java.lang.AssertionError;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

public class Board {
    private int[][] blocks;
    private int dimension;
    
    private int rowEmpty;
    private int colEmpty;
    
    private int hamming;
    private int manhattan;
    
    /**
     * construct a board from an n-by-n array of blocks
     * (where blocks[i][j] = block in row i, column j)
     * 
     * @param blocks
     */
    public Board(int[][] blocks) {
        dimension = blocks.length;
        
        this.blocks = new int[dimension][dimension];
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                this.blocks[i][j] = blocks[i][j];
                
                if (blocks[i][j] == 0) {
                    rowEmpty = i;
                    colEmpty = j;
                }
                else {
                    hamming += calcHamming(blocks[i][j], i, j);
                    manhattan += calcManhattan(blocks[i][j], i, j);
                }
            }
        }
    }
    
    private int calcHamming(int blockId, int row, int col) {
        int actualPos = row * dimension + col + 1;
        
        if (actualPos == blockId)
            return 0;
        
        return 1;
    }
    
    private int calcManhattan(int blockId, int row, int col) {
        int actualBlockId = blockId - 1;
        int actualRow = actualBlockId / dimension;
        int actualCol = actualBlockId - actualRow * dimension;

        return Math.abs(row - actualRow) + Math.abs(col - actualCol);
    }
    
    /**
     *  // board dimension n
     * @return
     */
    public int dimension() {
        return dimension;
    }

    /**
     * number of blocks out of place
     * @return number of blocks out of place
     */

    public int hamming() {
        return hamming;
    }

    /**
     * sum of Manhattan distances between blocks and goal
     * @return sum of Manhattan distances between blocks and goal
     */
    public int manhattan() {
        return manhattan;
    }

    /**
     * is this board the goal board?
     * @return
     */
    public boolean isGoal() {
        return hamming() == 0;
    }

    private boolean hasRightNeighbour() {
        return (colEmpty != dimension - 1);
    }
    
    private boolean hasBottomNeighbour() {
        return (rowEmpty != dimension - 1);
    }
    
    private boolean hasLeftNeighbour() {
        return (colEmpty != 0);
    }
    
    private boolean hasAboveNeighbour() {
        return (rowEmpty != 0);
    }
    
    private void swap(int i0, int j0, int i1, int j1) {
        int val = blocks[i0][j0];
        blocks[i0][j0] = blocks[i1][j1];
        blocks[i1][j1] = val;
    }
    
    private Board getRightNeighbour() {
        Board right = null;
        
        swap(rowEmpty, colEmpty, rowEmpty, colEmpty + 1);
        right = new Board(blocks);
        swap(rowEmpty, colEmpty, rowEmpty, colEmpty + 1);
        
        return right;
    }
    
    private Board getBottomNeighbour() {
        Board bottom = null;
        
        swap(rowEmpty, colEmpty, rowEmpty + 1, colEmpty);
        bottom = new Board(blocks);
        swap(rowEmpty, colEmpty, rowEmpty + 1, colEmpty);
        
        return bottom;
    }
    
    private Board getLeftNeighbour() {
        Board left = null;
        
        swap(rowEmpty, colEmpty, rowEmpty, colEmpty - 1);
        left = new Board(blocks);
        swap(rowEmpty, colEmpty, rowEmpty, colEmpty - 1);
        
        return left;
    }
    
    private Board getAboveNeighbour() {
        Board above = null;
        
        swap(rowEmpty, colEmpty, rowEmpty - 1, colEmpty);
        above = new Board(blocks);
        swap(rowEmpty, colEmpty, rowEmpty - 1, colEmpty);
        
        return above;
    }
    
    /**
     * a board that is obtained by exchanging any pair of blocks
     * @return
     */
    public Board twin() {
        Board b = null;
        if (blocks[0][0] == 0 || blocks[0][1] == 0) {
            swap(1, 0, 1, 1);
            b = new Board(blocks);
            swap(1, 0, 1, 1);
        }
        else {
            swap(0, 0, 0, 1);
            b = new Board(blocks);
            swap(0, 0, 0, 1);
        }
        
        return b;
    }

    /**
     * does this board equal y?
     */
    public boolean equals(Object y) {
        if (y == null) {
            return false;
        }
        
        
        if (String.class == y.getClass()) {
            String s = (String) y;
            return this.toString().compareTo(s) == 0;
        }
        
        Board rhs = (Board) y;
        
        if (dimension != rhs.dimension()) {
            return false;
        }
        
        for (int i = 0; i < dimension; ++i) {
            for (int j = 0; j < dimension; ++j) {
                if (blocks[i][j] != rhs.blocks[i][j]) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * all neighboring boards
     * a little hack here 
     * @return
     */
    public Iterable<Board> neighbors() {
        LinkedList<Board> boards = new LinkedList<Board>();

        if (hasRightNeighbour()) {
            boards.add(getRightNeighbour());
        }

        if (hasBottomNeighbour()) {
            boards.add(getBottomNeighbour());
        }

        if (hasLeftNeighbour()) {
            boards.add(getLeftNeighbour());
        }

        if (hasAboveNeighbour()) {
            boards.add(getAboveNeighbour());
        }
        
        return boards;
    }

    /**
     * string representation of this board (in the output format specified below)
     */
    public String toString() {
        String str = Integer.toString(dimension) + "\n";
        for (int i = 0; i < dimension; ++i) {
            str = str.concat(" ");
            for (int j = 0; j < dimension - 1; ++j) {
                str = str.concat(blocks[i][j] + "  ");
            }
            str = str.concat(blocks[i][dimension - 1] + " \n");
        }
        
        return str;
    }

    /**
     * unit tests (not graded)
     * @param args
     */
    public static void main(String[] args) {
     // create initial board from file
        In in = new In(args[0]);
        int n = in.readInt();
        int[][] blocks = new int[n][n];
        for (int i = 0; i < n; i++)
            for (int j = 0; j < n; j++)
                blocks[i][j] = in.readInt();
        Board initial = new Board(blocks);

        StdOut.println(initial);
        
        for (Board it : initial.neighbors()) {
            System.out.println(it);
        }
        
        System.out.println(initial.twin());
    }
}
