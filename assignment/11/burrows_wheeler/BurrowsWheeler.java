import edu.princeton.cs.algs4.BinaryStdIn;
import edu.princeton.cs.algs4.BinaryStdOut;

import java.util.LinkedList;

public class BurrowsWheeler {
    private static final int R = 256;

    /**
     * apply Burrows-Wheeler transform, reading from standard input and writing to standard output
     */
    public static void transform() {
        String s = BinaryStdIn.readString();
        CircularSuffixArray cArr = new CircularSuffixArray(s);
        int length = cArr.length();
        int first = -1;
        
        for (int i = 0; i < length; ++i) {
            if (cArr.index(i) == 0) {
                first = i;
                BinaryStdOut.write(first);
                break;
            }
        }

        for (int i = 0; i < first; ++i) {
            BinaryStdOut.write(s.charAt(cArr.index(i) - 1));
        }

        BinaryStdOut.write(s.charAt(length - 1));

        for (int i = first + 1; i < length; ++i) {
            BinaryStdOut.write(s.charAt(cArr.index(i) - 1)); 
        }

        BinaryStdOut.flush();
    }

    private static void putStringToMultiMap(String s, LinkedList<Integer>[] llist) {
        for (int i = 0; i < s.length(); ++i) {
            int index = s.charAt(i);

            if (llist[index] == null) {
                llist[index] = new LinkedList<Integer>();
            }
            
            llist[index].addLast(i);
        }
    }
    
    /**
     * apply Burrows-Wheeler inverse transform, reading from standard input and writing to standard output
     */
    public static void inverseTransform() {
        int first = BinaryStdIn.readInt();
        String s = BinaryStdIn.readString();
        
        LinkedList<Integer>[] lastCol = new LinkedList[R];
        putStringToMultiMap(s, lastCol);
        
        int[] firstCol = new int[R];
        for (int i = 0; i < s.length(); ++i) {
            ++firstCol[s.charAt(i)];
        }

        StringBuilder sorted = new StringBuilder(s.length());
        int[] next = new int[s.length()];
        for (int i = 0, j = 0; i < R;) {
            if (firstCol[i] > 0) {
                sorted.append((char) i);
                next[j++] = lastCol[i].removeFirst();
                --firstCol[i];
            }
            else {
                ++i;
            }
        }

//        debug next
//        System.out.printf("sorted: %s\n", sorted.toString());
//        for (int i = 0; i < s.length(); ++i) {
//            System.out.printf("t[%03d] = %c | next[%03d] = %d\n", i, s.charAt(i), i, next[i]);
//        }

        for (int i = 0; i < s.length(); ++i) {
            BinaryStdOut.write(sorted.charAt(first));
            first = next[first];
        }
        
        BinaryStdOut.flush();
    }

    /**
     * if args[0] is '-', apply Burrows-Wheeler transform
     * if args[0] is '+', apply Burrows-Wheeler inverse transform
     * @param args
     */
    public static void main(String[] args) {
        if (args[0].equals("-")) {
            transform();
        }
        else if (args[0].equals("+")) {
            inverseTransform();
        }
    }
}
