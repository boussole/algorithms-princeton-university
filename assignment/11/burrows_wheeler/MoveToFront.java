import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;
import java.util.LinkedList;

public class MoveToFront {
    private static final int R = 256;
    
    /**
     *  apply move-to-front encoding, reading from standard input and writing to standard output
     */
    public static void encode() {
        LinkedList<Integer> seq = new LinkedList<Integer>();
        for (int i = 0; i < R; ++i) {
            seq.addLast(i);
        }
        
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int index = seq.indexOf((int) c);
            if (index > 0) {
                seq.addFirst(seq.remove(index));
            }
            BinaryStdOut.write((char) index);
        }
        BinaryStdOut.flush();
    }

    /**
     *  apply move-to-front decoding, reading from standard input and writing to standard output
     */
    public static void decode() {
        LinkedList<Integer> seq = new LinkedList<Integer>();
        for (int i = 0; i < R; ++i) {
            seq.addLast(i);
        }
        
        while (!BinaryStdIn.isEmpty()) {
            char c = BinaryStdIn.readChar();
            int orig = (int) seq.remove((int) c);
            seq.addFirst(orig);
            BinaryStdOut.write((char) orig);
        }
        BinaryStdOut.flush();
    }

    /**
     *  if args[0] is '-', apply move-to-front encoding
     *  if args[0] is '+', apply move-to-front decoding
     * @param args
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            encode();
        }
        else if (args[0].equals("+")) {
            decode();
        }
    }
}