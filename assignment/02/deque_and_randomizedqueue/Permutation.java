import edu.princeton.cs.algs4.StdIn;

public class Permutation {

    /**
     * @param args
     */
    public static void main(String[] args) {
        RandomizedQueue<String> queue = new RandomizedQueue<String>();
        int nrUniformlyStrings = Integer.parseInt(args[0]);
        
        while (!StdIn.isEmpty()) {
            queue.enqueue(StdIn.readString());
        }

        for (int i = 0; i < nrUniformlyStrings; ++i) {
            System.out.println(queue.dequeue());
        }
    }

}
