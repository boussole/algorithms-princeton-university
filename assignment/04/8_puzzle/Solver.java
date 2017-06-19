import java.util.LinkedList;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.MinPQ;

public class Solver {
    private class SearchNode implements Comparable<SearchNode> {
        private int priority;
        
        private int nrMoves = 0;
        
        private Board board;
        
        private SearchNode previous;
        
        public SearchNode(int moves, Board board, SearchNode previous) {
            this.board = board;
            this.previous = previous;
            this.nrMoves = moves;
            priority = moves + board.manhattan();
        }
        
        public int priority() {
            return priority;
        }
        
        public int getMoves() {
            return nrMoves;
        }
        
        public Board board() {
            return board;
        }
        
        public SearchNode previous() {
            return previous;
        }
        
        public int compareTo(SearchNode rhs) {
            if (this == rhs || this.priority == rhs.priority) return 0;
            else if (priority < rhs.priority) return -1;
            else return 1;
        }
    }
    
    private LinkedList<Board> boards = new LinkedList<Board>();
    
    private int nrMoves = -1;
    
    private boolean solved = false;
    
    /**
     * find a solution to the initial board (using the A* algorithm)
     * @param initial
     */
    public Solver(Board initial) {
        if (initial == null) {
            throw new java.lang.NullPointerException();
        }
        
        MinPQ<SearchNode> pq = new MinPQ<SearchNode>();
        MinPQ<SearchNode> pq2 = new MinPQ<SearchNode>();
        
        SearchNode current = new SearchNode(0, initial, null);
        SearchNode currentTwin = new SearchNode(0, initial.twin(), null);
        
        while (!current.board.isGoal() && !currentTwin.board.isGoal()) {
            for (Board it : current.board.neighbors()) {
                if (current.previous() == null || !it.equals(current.previous().board())) {
                    pq.insert(new SearchNode(current.getMoves() + 1, it, current));
                }
            }
            
            for (Board it : currentTwin.board.neighbors()) {
                if (currentTwin.previous() == null || !it.equals(currentTwin.previous().board())) {
                    pq2.insert(new SearchNode(currentTwin.getMoves() + 1, it, currentTwin));
                }
            }
            
            current = pq.delMin();
            currentTwin = pq2.delMin();
        }

        if (current.board.isGoal()) {
            nrMoves = current.getMoves();
            while (current != null) {
                boards.addFirst(current.board);
                current = current.previous();
            }
            
            solved = true;
        }
    }
    
    /**
     * is the initial board solvable?
     * @return
     */
    public boolean isSolvable() {
        return solved;
    }
    
    /**
     * min number of moves to solve initial board; -1 if unsolvable
     * @return
     */
    public int moves() {
        return nrMoves;
    }
    
    /**
     * sequence of boards in a shortest solution; null if unsolvable
     * @return solution | null
     */
    public Iterable<Board> solution() {
        if (solved) {
            return boards;
        }
        
        return null;
    }
    
    /**
     * solve a slider puzzle (given below)
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

        // solve the puzzle
        Solver solver = new Solver(initial);
        
        // print solution to standard output
        if (!solver.isSolvable())
            StdOut.println("No solution possible");
        else {
            StdOut.println("Minimum number of moves = " + solver.moves());
            for (Board board : solver.solution())
                StdOut.println(board);
        }
    }

}
