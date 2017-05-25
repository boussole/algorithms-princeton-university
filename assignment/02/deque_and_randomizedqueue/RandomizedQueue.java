import java.util.Arrays;

import java.util.Iterator;

import edu.princeton.cs.algs4.StdRandom;

public class RandomizedQueue<Item> implements Iterable<Item> {

    private int size = 0;
    
    private Item[] array;

    /**
     * construct an empty randomized queue
     */
    public RandomizedQueue() {
        array = (Item[]) new Object[1];
    }
    
    private void resize(int newsize) {
        Item[] copy = (Item[]) new Object[newsize];
        
        for (int i = 0; i < size; ++i)
            copy[i] = array[i];
        
        array = copy;
    }
    
    /**
     * Resize array if:
     * - full             => newsize = oldsize * 2;
     * - one-quarter full => newsize = oldsize / 2.
     */
    private void resizeIfNeed() {
        if (size == array.length) {
            resize(array.length * 2);
        }
        else if (size > 0 && size == array.length / 4) {
            resize(array.length / 2);
        }
    }
    
    /**
     * is the queue empty?
     * speed = O(1)
     * 
     * @return empty?
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * return the number of items on the queue
     * speed = O(1)
     * 
     * @return number of items on the queue
     */
    public int size() {
        return size;
    }
    
    /**
     * add the item
     * speed (amortized) = O(1)
     * 
     * @param item
     */
    public void enqueue(Item item) {
        if (item == null) {
            throw new java.lang.NullPointerException();
        }
        
        resizeIfNeed();
        
        array[size++] = item;
    }
    
    /**
     * remove and return a random item
     * speed (amortized) = O(1)
     * 
     * @return removed item
     */
    public Item dequeue() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        
        int randomIndex = StdRandom.uniform(size--); // now size points to the last (deleting) item !!!
        
        Item randomItem = array[randomIndex];
        
        array[randomIndex] = array[size];
        array[size] = null;
        
        resizeIfNeed();
        
        return randomItem;
    }
    
    /**
     * return (but do not remove) a random item
     * 
     * @return a random item
     */
    public Item sample() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        
        return array[StdRandom.uniform(size)];
    }
    
    private void debug() {
        System.out.println("---------- debug -----------");
        System.out.printf("Params: size = %d; capacity = %d\n", size, array.length);
        System.out.println("----------------------------");
        
        for (int i = 0; i < size; ++i)
            System.out.print(array[i] + " ");
        System.out.println("\n----------------------------");
    }
    
    private class RandomizedQueueIterator implements Iterator<Item> {
        private int counter = 0;
        
        private Item[] copyArray = Arrays.copyOf(array, size);
        
        public RandomizedQueueIterator() {
            for (int i = 1; i < copyArray.length; ++i) {
                int uniform = StdRandom.uniform(i + 1);
                Item temp = copyArray[i];
                copyArray[i] = copyArray[uniform];
                copyArray[uniform] = temp;
            }
        }
        
        public boolean hasNext() {
            return counter < copyArray.length;
        }
        
        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }

            return copyArray[counter++];
        }
        
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }
    
    /**
     * return an independent iterator over items in random order
     */
    public Iterator<Item> iterator() {
        return new RandomizedQueueIterator();
    }
    
    private static void printRandomizedQueueOfStrings(Iterator<String> it) {
        System.out.println("----------------------------");
        System.out.println("Print RandomizedQueue of strings");
        
        while (it.hasNext()) {
            String s = it.next();
            System.out.printf("'%s' ", s);
        }
        
        System.out.println("\n----------------------------\n");
    }
    
    /**
     * unit testing (optional)
     * 
     * @param args
     */
    public static void main(String[] args) {
        RandomizedQueue<String> rq = new RandomizedQueue<String>();
        
        rq.debug();
        
        rq.enqueue("1");
        rq.debug();
        printRandomizedQueueOfStrings(rq.iterator());
        
        rq.enqueue("2");
        rq.debug();
        printRandomizedQueueOfStrings(rq.iterator());
        printRandomizedQueueOfStrings(rq.iterator());
        
        rq.enqueue("3");
        rq.enqueue("4");
        rq.enqueue("5");
        rq.enqueue("6");
        rq.enqueue("7");
        rq.enqueue("8");
        rq.enqueue("9");
        rq.enqueue("10");
        rq.enqueue("11");
        rq.enqueue("12");
        rq.enqueue("13");
        rq.debug();
        printRandomizedQueueOfStrings(rq.iterator());
        printRandomizedQueueOfStrings(rq.iterator());
        printRandomizedQueueOfStrings(rq.iterator());
        printRandomizedQueueOfStrings(rq.iterator());
        
        while (rq.size() > 0) {
            System.out.printf("Removing random element: %s\n", rq.dequeue());
            rq.debug();
        }
    }
}
