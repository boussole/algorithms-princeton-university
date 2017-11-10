import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;
import edu.princeton.cs.algs4.StdIn;

import java.util.LinkedList;

public class SAP {
    private final Digraph digraph;
    
    // cache :-)
    private boolean [] pathSrc;
    private int [] distanceSrc;
    private int from = -1;
    private boolean [] pathDst;
    private int [] distanceDst;
    private int to = -1;
    private int ancestor = -1;
    private int length = -1;
    
    private int multiAncestor = -1;
    private int multiLength = -1;

    /**
     * constructor takes a digraph (not necessarily a DAG)
     * @param G
     */
    public SAP(Digraph G) {
        if (G == null) {
            throw new java.lang.IllegalArgumentException();
        }

        digraph = new Digraph(G);
    }

    private void bfs(int v, boolean [] path, int [] distance) {
        LinkedList<Integer> queue = new LinkedList<Integer>();
        queue.addLast(v);
        distance[v] = 0;
        path[v] = true; 
        while (!queue.isEmpty()) {
            v = queue.pollFirst();
            for (int neigh : digraph.adj(v)) {
                if (!path[neigh]) {
                    path[neigh] = true;
                    distance[neigh] = distance[v] + 1;
                    queue.addLast(neigh);
                }
            }
        }
    }
    
    private void bfs(Iterable<Integer> vs, boolean [] path, int [] distance) {
        LinkedList<Integer> queue = new LinkedList<Integer>();
        for (int i : vs) {
            queue.addLast(i);
            distance[i] = 0;
            path[i] = true; 
        }
        
        while (!queue.isEmpty()) {
            int v = queue.pollFirst();
            for (int neigh : digraph.adj(v)) {
                if (!path[neigh]) {
                    path[neigh] = true;
                    distance[neigh] = distance[v] + 1;
                    queue.addLast(neigh);
                }
            }
        }
    }
    
    private void ancestorAndLength(int v, int w) {
        if (v < 0 || v >= digraph.V() || w < 0 || w >= digraph.V()) {
            throw new java.lang.IllegalArgumentException();
        }
        
        if (v == from && w == to) {
            return;
        }

        if (from != v) {
            pathSrc = new boolean[digraph.V()];
            distanceSrc = new int[digraph.V()];
            bfs(v, pathSrc, distanceSrc);
            from = v;
        }
        
        if (to != w) {
            pathDst = new boolean[digraph.V()];
            distanceDst = new int[digraph.V()];
            bfs(w, pathDst, distanceDst);
            to = w;
        }
        
        length = -1;
        ancestor = -1;
        int currentLength = Integer.MAX_VALUE;
        for (int i = 0; i < digraph.V(); ++i) {
            if (pathSrc[i] && pathDst[i]) {
                int newLength = distanceSrc[i] + distanceDst[i];
                if (currentLength > newLength) {
                    currentLength = newLength;
                    length = currentLength;
                    ancestor = i;
                }
            }
        }
    }
    
    private void ancestorAndLength(Iterable<Integer> v, Iterable<Integer> w) {
        if (v == null || w == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        for (int i : v) {
            if (i < 0 || i >= digraph.V()) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        
        for (int i : w) {
            if (i < 0 || i >= digraph.V()) {
                throw new java.lang.IllegalArgumentException();
            }
        }
        
        multiAncestor = -1;
        multiLength = -1;
        int oldLength = Integer.MAX_VALUE;
        
        pathSrc = new boolean[digraph.V()];
        distanceSrc = new int[digraph.V()];
        bfs(v, pathSrc, distanceSrc);

        pathDst = new boolean[digraph.V()];
        distanceDst = new int[digraph.V()];
        bfs(w, pathDst, distanceDst);

        for (int k = 0; k < digraph.V(); ++k) {
            if (pathSrc[k] && pathDst[k]) {
                int newLength = distanceSrc[k] + distanceDst[k];
                if (newLength < oldLength) {
                    multiAncestor = k;
                    multiLength = newLength;
                    oldLength = newLength;
                }
            }
        }
    }
    
    /**
     * length of shortest ancestral path between v and w; -1 if no such path
     * @param v
     * @param w
     * @return
     */
    public int length(int v, int w) {
        ancestorAndLength(v, w);
        return length;
    }

    /**
     * a common ancestor of v and w that participates in a shortest ancestral path; -1 if no such path
     * @param v
     * @param w
     * @return
     */
    public int ancestor(int v, int w) {
        ancestorAndLength(v, w);
        return ancestor;
    }

    /**
     * length of shortest ancestral path between any vertex in v and any vertex in w; -1 if no such path
     * @param v
     * @param w
     * @return
     */
    public int length(Iterable<Integer> v, Iterable<Integer> w) {
        ancestorAndLength(v, w);
        return multiLength;
    }

    /**
     * a common ancestor that participates in shortest ancestral path; -1 if no such path
     * @param v
     * @param w
     * @return
     */
    public int ancestor(Iterable<Integer> v, Iterable<Integer> w) {
        ancestorAndLength(v, w);
        return multiAncestor;
    }

    /**
     *  do unit testing of this class
     * @param args
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        Digraph G = new Digraph(in);
        SAP sap = new SAP(G);
        while (!StdIn.isEmpty()) {
            int v = StdIn.readInt();
            int w = StdIn.readInt();
            int length   = sap.length(v, w);
            int ancestor = sap.ancestor(v, w);
            StdOut.printf("length = %d, ancestor = %d\n", length, ancestor);
        }
    }
}
