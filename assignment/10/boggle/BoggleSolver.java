import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdOut;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BoggleSolver {
    /* Length Points */
    /* 0–2     0     */
    /* 3–4     1     */
    /* 5       2     */
    /* 6       3     */
    /* 7       5     */
    /* 8+     11     */
    private byte[] pointsByLength = { 0, 0, 0, 1, 1, 2, 3, 5, 11 };

    private class PatriciaTrie {
        private class TrieNode {
            private String prefix;
            private int points;
            private long epoch;
            private TrieNode parent;
            private TrieNode[] children;

            public TrieNode(String s, int p, TrieNode parentNode) {
                prefix = new String(s);
                points = p;
                parent = parentNode;
            }

            private void addChild(int at, String s, int p) {
                int hash = charToHash(s.charAt(at));
                if (children == null) {
                    children = new TrieNode[26];
                }

                if (children[hash] == null) {
                    children[hash] = new TrieNode(s.substring(at), p, this);
                }
                else {
                    children[hash].put(s.substring(at), p);
                }
            }

            private void moveToChild(int at) {
                TrieNode oldParent = parent;
                TrieNode newParent = new TrieNode(prefix.substring(0, at), -1, oldParent);
                newParent.children = new TrieNode[26];
                oldParent.children[charToHash(prefix.charAt(0))] = newParent;
                prefix = prefix.substring(at);
                newParent.children[charToHash(prefix.charAt(0))] = this;
                parent = newParent;
            }

            public void put(String s, int p) {
                int diff = 0;
                int length = Math.min(prefix.length(), s.length());
                for (diff = 0; diff < length; ++diff) {
                    if (prefix.charAt(diff) != s.charAt(diff)) {
                        break;
                    }
                }

                if (diff == length) {
                    if (prefix.length() < s.length()) {
                        addChild(diff, s, p);
                    }
                    else if (prefix.length() > s.length()) {
                        moveToChild(diff);
                        parent.points = p;
                    }
                    else {
                        points = p;
                    }
                }
                else {
                    moveToChild(diff);
                    parent.addChild(diff, s, p);
                }
            }

            private void printToScreen() {
                String parentName = "null";
                if (parent != null) {
                    parentName = parent.prefix;
                }

                System.out.printf("\'%s\' (%d) (parent = %s):", prefix, points, parentName);
                if (children != null) {
                    for (int i = 0; i < children.length; ++i) {
                        if (children[i] != null) {
                            System.out.printf(" [%c]", i + 'A');
                        }
                    }
                }
                else {
                    System.out.printf(" null");
                }
            }
        }

        private class Position {
            private TrieNode node;
            private int inPrefix;
            private char[] prefix;
            private int prefixLen;
            private long epoch;
        }

        private TrieNode root;
        private Position position;
        private int longestWordLen;

        PatriciaTrie() {
            root = new TrieNode("", -1, null);
            position = new Position();
            longestWordLen = 0;
        }

        void startPass() {
            position.node = root;
            position.inPrefix = 0;
            position.prefixLen = 0;
            ++position.epoch;
        }

        boolean moveNextIfEqual(char to) {
            TrieNode node = position.node;
            if (position.inPrefix < node.prefix.length()) {
                if (node.prefix.charAt(position.inPrefix) == to) {
                    position.prefix[position.prefixLen++] = to;
                    position.inPrefix++;
                    return true;
                }
                else {
                    return false;
                }
            }

            if (node.children != null) {
                TrieNode next = position.node.children[charToHash(to)];
                if (next != null) {
                    position.prefix[position.prefixLen++] = to;
                    position.inPrefix = 1;
                    position.node = next;
                    return true;
                }
            }

            return false;
        }

        boolean movePrev() {
            if (position.node.parent == null) {
                return false;
            }

            if (position.inPrefix > 0) {
                --position.inPrefix;
            }

            if (position.inPrefix == 0) {
                position.node = position.node.parent;
                position.inPrefix = position.node.prefix.length();
            }

            --position.prefixLen;

            return true;
        }

        int pointsAtPosition() {
            if (position.inPrefix == position.node.prefix.length() && position.node.points > -1 && position.node.epoch < position.epoch) {
                position.node.epoch = position.epoch;
                return position.node.points;
            }

            return -1;
        }

        public void put(String word) {
            if (word.length() > longestWordLen) {
                longestWordLen = word.length();
                position.prefix = new char[longestWordLen];
            }

            int points = wordLengthToPoint(word.length());
            root.put(word, points);
        }

        public int charToHash(char ch) {
            return (int) ch - 'A';
        }

        private String ident(int size) {
            char[] fill = new char[size];
            Arrays.fill(fill, ' ');
            return new String(fill);
        }

        private void printToScreen(TrieNode node, int level) {
            System.out.printf("\n%s%d level: ", ident(level), level);
            node.printToScreen();
            if (node.children != null) {
                for (int i = 0; i < node.children.length; ++i) {
                    if (node.children[i] != null) {
                        printToScreen(node.children[i], level + 1);
                    }
                }
            }
        }

        private void printToScreen() {
            printToScreen(root, 0);
        }

        private int scoreOf(String word) {
            trie.startPass();

            for (int i = 0; i < word.length(); ++i) {
                if (!trie.moveNextIfEqual(word.charAt(i))) {
                    return -1;
                }
            }

            return trie.pointsAtPosition();
        }

        private String prefixAtPosition() {
            return new String(position.prefix, 0, position.prefixLen);
        }
    }

    private PatriciaTrie trie;
    private boolean[][] helperBoard;
    /**
     * Initializes the data structure using the given array of strings as the dictionary.
     * @param dictionary
     * (You can assume each word in the dictionary contains only the uppercase letters A through Z.)
     */
    public BoggleSolver(String[] dictionary) {
        trie = new PatriciaTrie();
        for (String word : dictionary) {
            trie.put(word);
        }

//         trie.printToScreen();
    }

    private void findValidWords(BoggleBoard board, int i, int j, List<String> allValidWords) {
        char key = board.getLetter(i, j);

        if (trie.moveNextIfEqual(key)) {
            int helpI = i + 1, helpJ = j + 1;
            helperBoard[helpI][helpJ] = true;

            boolean qIsValid = true;
            if (key == 'Q') {
                qIsValid = trie.moveNextIfEqual('U');
            }

            if (qIsValid) {
                if (trie.pointsAtPosition() > 0) {
                    allValidWords.add(trie.prefixAtPosition());
                }

                // clockwise
                if (!helperBoard[helpI - 1][helpJ]) {
                    findValidWords(board, i - 1, j, allValidWords);
                }

                if (!helperBoard[helpI - 1][helpJ + 1]) {
                    findValidWords(board, i - 1, j + 1, allValidWords);
                }

                if (!helperBoard[helpI][helpJ + 1]) {
                    findValidWords(board, i, j + 1, allValidWords);
                }

                if (!helperBoard[helpI + 1][helpJ + 1]) {
                    findValidWords(board, i + 1, j + 1, allValidWords);
                }

                if (!helperBoard[helpI + 1][helpJ]) {
                    findValidWords(board, i + 1, j, allValidWords);
                }

                if (!helperBoard[helpI + 1][helpJ - 1]) {
                    findValidWords(board, i + 1, j - 1, allValidWords);
                }

                if (!helperBoard[helpI][helpJ - 1]) {
                    findValidWords(board, i, j - 1, allValidWords);
                }

                if (!helperBoard[helpI - 1][helpJ - 1]) {
                    findValidWords(board, i - 1, j - 1, allValidWords);
                }
            }

            helperBoard[helpI][helpJ] = false;
            trie.movePrev();

            if (key == 'Q' && qIsValid) {
                trie.movePrev();
            }
        }
    }

    /**
     * Returns the set of all valid words in the given Boggle board, as an Iterable.
     * @param board
     * @return
     */
    public Iterable<String> getAllValidWords(BoggleBoard board) {
        /* This board is a little bit bigger than original `board`.
         * It has extra fake rows and cols that are marked as busy.
         * This trick prevents `9 * IF` border checking.
         */
        helperBoard = new boolean[board.rows() + 2][board.cols() + 2];
        for (int i = 0; i < board.rows() + 2; ++i) {
            helperBoard[i][0] = true;
            helperBoard[i][board.cols() + 1] = true;
        }

        for (int j = 0; j < board.cols() + 2; ++j) {
            helperBoard[0][j] = true;
            helperBoard[board.rows() + 1][j] = true;
        }

        trie.startPass();
        List<String> allValidWords = new ArrayList<String>();
        for (int i = 0; i < board.rows(); ++i) {
            for (int j = 0; j < board.cols(); ++j) {
                findValidWords(board, i, j, allValidWords);
            }
        }

        return allValidWords;
    }

    /**
     * Returns the score of the given word if it is in the dictionary, zero otherwise.
     * @param word
     * @return
     * (You can assume the word contains only the uppercase letters A through Z.)
     */
    public int scoreOf(String word) {
        int score = trie.scoreOf(word);
        if (score > -1) {
            return score;
        }

        return 0;
    }

    /**
     *
     * @param len
     * @return
     */
    private byte wordLengthToPoint(int len) {
        if (len < pointsByLength.length) {
            return pointsByLength[len];
        }

        return 11;
    }

    /**
     *
     * @param args
     */
    public static void main(String[] args) {
        In in = new In(args[0]);
        String[] dictionary = in.readAllStrings();
        BoggleSolver solver = new BoggleSolver(dictionary);
        BoggleBoard board = new BoggleBoard(args[1]);
        int score = 0;
        for (String word : solver.getAllValidWords(board)) {
            StdOut.println(word);
            score += solver.scoreOf(word);
        }
        StdOut.println("Score = " + score);
    }
}
