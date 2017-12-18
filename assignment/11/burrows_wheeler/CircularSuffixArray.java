import java.util.Arrays;
import java.util.Comparator;

public class CircularSuffixArray {
    private int length;
    private Integer[] suffix;
    
    /**
     * circular suffix array of s
     * @param s
     */
    public CircularSuffixArray(String s) {
        if (s == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        length = s.length();
        suffix = new Integer[length];
        for (int i = 0; i < length; ++i) {
            suffix[i] = i;
        }

        Arrays.sort(suffix, new Comparator<Integer>() {
            @Override
            public int compare(Integer left, Integer right) {
                for (int i = 0; i < length; ++i) {
                    char lChar = s.charAt((left + i) % length), rChar = s.charAt((right + i) % length);
                    if (lChar < rChar) {
                        return -1;
                    }
                    else if (lChar > rChar) {
                        return 1;
                    }
                }
                
                return 0;
            }
        });

//        debug all suffixes
//        for (int i = 0; i < length; ++i) {
//            for (int j = 0; j < length; ++j) {
//                char ch = s.charAt((i + j) % length);
//                System.out.printf("%c", ch);
//            }
//            System.out.println();
//        }
    }
    
    /**
     * length of s
     * @return
     */
    public int length() {
        return length;
    }
    
    /**
     * returns index of ith sorted suffix
     * @param i
     * @return
     */
    public int index(int i) {
        if (i < 0 || i >= length) {
            throw new java.lang.IllegalArgumentException();
        }
        
        return suffix[i];
    }
    
    /**
     * unit testing (required)
     * @param args
     */
    public static void main(String[] args) {
        CircularSuffixArray arr = new CircularSuffixArray("ABRACADABRA!");
        System.out.printf("length = %d\n", arr.length);
        for (int i = 0; i < arr.length; ++i) {
            System.out.printf("index[%d] = %d\n", i, arr.index(i));
        }
    }
}