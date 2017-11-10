import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Digraph;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class WordNet {
    private class Nouns implements Iterable<String> {
        private class Synset {
            private final String[] nouns;
            private final String gloss;

            public Synset(String[] newNouns, String gloss) {
                this.nouns = new String[newNouns.length];
                for (int i = 0; i < newNouns.length; ++i) {
                    this.nouns[i] = newNouns[i];
                }

                this.gloss = gloss;
            }
            
            public int nounLength() {
                return nouns.length;
            }
            
            public String noun(int i) {
                return nouns[i];
            }
            
            public String nouns() {
                StringBuilder n = new StringBuilder(nouns[0]);
                for (int i = 1; i < nouns.length; ++i) {
                    n.append(" ");
                    n.append(nouns[i]);
                }
                
                return n.toString();
            }
            
            public String gloss() {
                return gloss;
            }
        }

        private final ArrayList<Synset> synsets;
        private int nrSynsets;
        private int nrNouns;
        
        public Nouns() {
            synsets = new ArrayList<Synset>();
            nrSynsets = 0;
            nrNouns = 0;
        }

        public void appendSynset(String[] newNouns, String gloss) {
            synsets.add(new Synset(newNouns, gloss));
            ++nrSynsets;
            nrNouns += newNouns.length;
        }

        public int synsetSize(int index) {
            return synsets.get(index).nounLength();
        }
        
        public String noun(int index, int indexInSet) {
            return synsets.get(index).noun(indexInSet);
        }
        
        public String synset(int index) {
            return synsets.get(index).nouns();
        }

        public String gloss(int index) {
            return synsets.get(index).gloss();
        }

        public int nrSynsets() {
            return nrSynsets;
        }

        public int nrNouns() {
            return nrNouns;
        }

        private class NounIterator implements Iterator<String> {
            private int index;
            private int indexInSet;
            private int nounIndex;

            public NounIterator() {
                index = 0;
                indexInSet = 0;
                nounIndex = 0;
            }

            public boolean hasNext() {
                return nounIndex < nrNouns();
            }

            public String next() {
                if (!hasNext()) {
                    throw new java.util.NoSuchElementException();
                }

                String n = noun(index, indexInSet++);
                
                if (indexInSet == synsetSize(index)) {
                    ++index;
                    indexInSet = 0;
                }
                
                ++nounIndex;

                return n;
            }

            public void remove() {
                throw new java.lang.UnsupportedOperationException();
            }
        }
        
        public Iterator<String> iterator() {
            return new NounIterator();
        }
    }

    private final Digraph digraph;
    private final Nouns nouns;
    private Map<String, Set<Integer>> nounsMap = new HashMap<>();
    private final SAP sap;
    
    /**
     * constructor takes the name of the two input files
     * @param synsets
     * @param hypernyms
     */
    public WordNet(String synsets, String hypernyms) {
        if (synsets == null || hypernyms == null) {
            throw new java.lang.IllegalArgumentException();
        }

        nouns = new Nouns();

        In synsetIn = new In(synsets);
        while (synsetIn.hasNextLine()) {
            String line = synsetIn.readLine();
            String[] lex = line.split(",");
            int index = Integer.parseUnsignedInt(lex[0]);
            if (index != nouns.nrSynsets()) {
                throw new java.lang.IllegalArgumentException();
            }
            
            String[] synset = lex[1].split(" ");
            nouns.appendSynset(synset, lex[2]);
            for (String it : synset) {
                if (nounsMap.get(it) == null) {
                    nounsMap.put(it, new HashSet<>());
                }
                
                nounsMap.get(it).add(index);
            }
        }
        synsetIn.close();

        /**
         * To check graph rooting check number of outgoing edges from each vertex in the digraph.
         * Only one vertex must have number of outgoing edges is equal 0.
         */
        int nrSynsets = nouns.nrSynsets();
        
        digraph = new Digraph(nouns.nrSynsets());
        In hypernymsIn = new In(hypernyms);
        while (hypernymsIn.hasNextLine()) {
            String line = hypernymsIn.readLine();
            String[] vertexes = line.split(",");
            int src = Integer.parseUnsignedInt(vertexes[0]);
            
            if (digraph.outdegree(src) == 0 && vertexes.length > 1) {
                --nrSynsets;
            }
            
            for (int i = 1; i < vertexes.length; ++i) {
                int dst = Integer.parseUnsignedInt(vertexes[i]);
                digraph.addEdge(src, dst);
            }
        }
        
        if (nrSynsets != 1) {
            throw new java.lang.IllegalArgumentException();
        }
        
        sap = new SAP(digraph);
    }
    
    /**
     * returns all WordNet nouns
     * @return
     */
    public Iterable<String> nouns() {
        return nounsMap.keySet();
    }
    
    /**
     * is the word a WordNet noun?
     * @param word
     * @return
     */
    public boolean isNoun(String word) {
        if (word == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        return nounsMap.containsKey(word);
    }
    
    /**
     * distance between nounA and nounB (defined below)
     * @param nounA
     * @param nounB
     * @return
     */
    public int distance(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }

        Set<Integer> v = nounsMap.get(nounA);
        Set<Integer> w = nounsMap.get(nounB);
        return sap.length(v, w);
    }
    
    /**
     * a synset (second field of synsets.txt) that is the common ancestor of nounA and nounB
     * in a shortest ancestral path (defined below) 
     * @param nounA
     * @param nounB
     * @return
     */
    public String sap(String nounA, String nounB) {
        if (nounA == null || nounB == null || !isNoun(nounA) || !isNoun(nounB)) {
            throw new java.lang.IllegalArgumentException();
        }
        
        Set<Integer> v = nounsMap.get(nounA);
        Set<Integer> w = nounsMap.get(nounB);
        int res = sap.ancestor(v, w);
        if (res != -1) {
            return nouns.synset(res);
        }
        
        return "";
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        WordNet wordNet = new WordNet(args[0], args[1]);
        for (String noun : wordNet.nouns()) {
            System.out.println("Noun: " + noun);
        }
        
        System.out.printf("isNoun('yaya') = %b\n", wordNet.isNoun("yaya"));
        System.out.printf("isNoun('Athena') = %b\n", wordNet.isNoun("Athena"));
    }

}
