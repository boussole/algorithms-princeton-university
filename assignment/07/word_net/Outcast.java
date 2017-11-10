
public class Outcast {
    private final WordNet wordnet;

    /**
     * constructor takes a WordNet object
     * @param wordnet
     */
    public Outcast(WordNet wordnet) {
        this.wordnet = wordnet;
    }
    /**
     * given an array of WordNet nouns, return an outcast
     * @param nouns
     * @return
     */
    public String outcast(String[] nouns) {
        int max = -1;
        String result = null;

        for (String noun : nouns) {
            int d = 0;
            for (String other: nouns)
                d += wordnet.distance(noun, other);

            if (d > max) {
                max = d;
                result = noun;
            }
        }

        return result;
    }
    
    /**
     * see test client below
     * @param args
     */
    public static void main(String[] args) {
        WordNet wordnet = new WordNet(args[0], args[1]);
        Outcast outcast = new Outcast(wordnet);
        for (int t = 2; t < args.length; t++) {
            In in = new In(args[t]);
            String[] nouns = in.readAllStrings();
            StdOut.println(args[t] + ": " + outcast.outcast(nouns));
        }
    }
}
