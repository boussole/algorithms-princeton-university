import java.util.Iterator;

public class Deque<Item> implements Iterable<Item> {
    
    private int size = 0;
    
    private Item[] array;
    
    private int head = 0, tail = 0;
    
    /**
     * construct an empty deque
     */
    public Deque() {
        array = (Item[]) new Object[1];
    }

    private void resize(int newsize) {
        Item[] copy = (Item[]) new Object[newsize];
        
        for (int i = 0; i < size; ++i, head = (head + 1) % array.length)
            copy[i] = array[head];
        
        head = 0;
        tail = size - 1;
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
     * is the deque empty?
     * speed = O(1)
     * 
     * @return empty or not
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * return the number of items on the deque
     * speed = O(1)
     * 
     * @return
     */
    public int size() {
        return size;
    }

    /**
     * add the item to the front
     * speed (amortized) = O(1)
     * 
     * @param item
     */
    public void addFirst(Item item) {
        if (item == null) {
            throw new java.lang.NullPointerException();
        }

        resizeIfNeed();
        
        --head;
        if (head < 0) {
            head = array.length - 1;
        }
        
        array[head] = item;
        ++size;
    }

    /**
     * add the item to the end
     * speed (amortized) = O(1)
     * 
     * @param item
     */
    public void addLast(Item item) {
        if (item == null) {
            throw new java.lang.NullPointerException();
        }
        
        resizeIfNeed();
        
        tail = (tail + 1) % array.length;
        array[tail] = item;
        ++size;
    }

    /**
     * remove and return the item from the front
     * speed (amortized) = O(1)
     * 
     * @return removed item
     */
    public Item removeFirst() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }

        Item item = array[head];
        
        array[head++] = null;
        
        head %= array.length;
        
        --size;
        
        resizeIfNeed();
        
        return item;
    }
    
    /**
     * remove and return the item from the end
     * speed (amortized) = O(1)
     * 
     * @return removed item
     */
    public Item removeLast() {
        if (isEmpty()) {
            throw new java.util.NoSuchElementException();
        }
        
        Item item = array[tail];

        array[tail--] = null;
        
        if (tail < 0) {
            tail = array.length - 1;
        }

        --size;

        resizeIfNeed();

        return item;
    }

    /**
     * print internals
     */
    private void debug() {
        System.out.println("---------- debug -----------");
        System.out.printf("Params: size = %d; capacity = %d, head = %d, tail = %d\n", size, array.length, head, tail);
        System.out.println("----------------------------");
        
        System.out.println("Internal view:");
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null)
                System.out.printf("[full] ");
            else
                System.out.printf("[empty] ");
        }
        
        System.out.println("\n----------------------------");
        
        System.out.printf("Deque view:\n");
        for (int i = head, counter = 0; counter < array.length; i = (i + 1) % array.length, ++counter) {
            if (array[i] != null)
                System.out.printf("[full] ");
            else
                System.out.printf("[empty] ");
        }
        
        System.out.println("\n----------------------------\n");
    }
    
    private class DequeIterator implements Iterator<Item> {
        private int current = head;
        private int counter = 0;
        
        public DequeIterator() {
            
        }
        
        public boolean hasNext() {
            return counter < size;
        }
        
        public Item next() {
            if (!hasNext()) {
                throw new java.util.NoSuchElementException();
            }
            
            ++counter;
            Item it = array[current++];
            current %= array.length;
            
            return it;
        }
        
        public void remove() {
            throw new java.lang.UnsupportedOperationException();
        }
    }
    
    /**
     * return an iterator over items in order from front to end
     * speed = O(1)
     * 
     * @return iterator
     */
    public Iterator<Item> iterator() {
        return new DequeIterator();
    }

    private static void printDequeOfStrings(Iterator<String> it) {
        System.out.println("----------------------------");
        System.out.println("Print Deque of strings");
        
        while (it.hasNext()) {
            String s = it.next();
            System.out.printf("'%s' ", s);
        }
        
        System.out.println("\n----------------------------\n");
    }
    
    /**
     * unit testing (optional)
     * @param args
     */
    public static void main(String[] args) {
        Deque<String> d = new Deque<String>();
        d.debug();
        printDequeOfStrings(d.iterator());

        d.addFirst("1");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("0");
        d.debug();
        printDequeOfStrings(d.iterator());

        d.addLast("2");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("-1");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("-2");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addLast("3");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        System.out.printf("removed last: %s\n", d.removeLast());
        d.debug();
        printDequeOfStrings(d.iterator());
        
        System.out.printf("removed last: %s\n", d.removeLast());
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("-3");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addLast("3");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("-4");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addLast("4");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addFirst("-5");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        d.addLast("5");
        d.debug();
        printDequeOfStrings(d.iterator());
        
        //-------------------
        // test 1
        Deque<Double> t1 = new Deque<Double>();
        t1.addLast(0.0);
//        t1.debug();
//        t1.addLast(0.8);
//        t1.addLast(0.0);
//        t1.removeLast();
//        t1.addLast(0.1);
    }
}
