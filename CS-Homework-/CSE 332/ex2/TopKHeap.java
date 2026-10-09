import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class TopKHeap<T extends Comparable<T>> {
    private BinaryMinHeap<T> topK; // Holds the top k items
    private BinaryMaxHeap<T> rest; // Holds all items other than the top k
    private int size; // Maintains the size of the data structure
    private final int k; // The value of k
    private Map<T, MyPriorityQueue<T>> itemToHeap; // Keeps track of which heap contains each item.
    
    // Creates a topKHeap for the given choice of k.
    // Uses a min-heap to store the top k items and a max-heap
    // to store all remaining items.
    public TopKHeap(int k){
        topK = new BinaryMinHeap<>();
        rest = new BinaryMaxHeap<>();
        size = 0;
        this.k = k;
        itemToHeap = new HashMap<>();
    }

    // Returns a list containing exactly the
    // largest k items. The list is not necessarily
    // sorted. If the size is less than or equal to
    // k then the list will contain all items.
    // The running time of this method should be O(k).
    public List<T> topK(){
        return topK.toList();
    }

    // Add the given item into the data structure.
    // The item is placed into the appropriate heap, and elements
    // may be moved between heaps to maintain exactly k items
    // The running time of this method should be O(log(n)+log(k)).
    public void insert(T item){
        if(topK.size() < k){
            topK.insert(item);
            itemToHeap.put(item,topK);
        }

        //moves the lowest value now in the top down to rest
        //Does this by extracting the lowest value, and then inserting it to the rest
        else if (k > 0 && item.compareTo(topK.peek()) > 0) {
            T old = topK.extract();
            rest.insert(old);
            itemToHeap.put(old, rest);

            topK.insert(item);
            itemToHeap.put(item, topK);
        }

        //case3: the new value just stays in the rest, isnt high enough to get added
        else{
            rest.insert(item);
            itemToHeap.put(item, rest);
        }

        size++;

    }

    // Indicates whether the given item is among the 
    // top k items. Should return false if the item
    // is not present in the data structure at all.
    // The running time of this method should be O(1).
    // We have provided a suggested implementation,
    // but you're welcome to do something different!
    public boolean isTopK(T item){
        return itemToHeap.containsKey(item) && itemToHeap.get(item) == topK;
    }

    // To be used whenever an item's priority has changed.
    // The input is a reference to the items whose priority
    // has changed. This operation will then rearrange
    // the items in the data structure to ensure it
    // operates correctly.
    // Throws an IllegalArgumentException if the item is
    // not a member of the heap.
    // The running time of this method should be O(log(n)+log(k)).
    public void updatePriority(T item){
        if (!itemToHeap.containsKey(item)) {
        throw new IllegalArgumentException("Item not found");
    }
    MyPriorityQueue<T> current = itemToHeap.get(item);
    current.updatePriority(item);

    // if the elo improves enough, player might be moved up into the topk players
    //does this by removing player from rest, and then replacing the lowest top player with the highest in rest
    if(current == rest){
        if ( !topK.isEmpty() && item.compareTo(topK.peek()) > 0){
            T lowest = topK.extract();
            rest.remove(item);
            topK.insert(item);
            rest.insert(lowest);

            itemToHeap.put(item, topK);
            itemToHeap.put(lowest, rest);

        }
    }
    //if a players score drops, they might be moved down/removed from list of top players 
    else if(current == topK) {
            if(!rest.isEmpty() && item.compareTo(rest.peek()) < 0){
                T highestInRest = rest.extract();
                topK.remove(item);

                rest.insert(item);
                topK.insert(highestInRest);

                itemToHeap.put(highestInRest, topK);
                itemToHeap.put(item, rest);

            }
        }

    }

    // Removes the given item from the data structure.
// If the item is removed from the top-k heap, a replacement
// is promoted from the rest heap to maintain k elements.
// Throws an exception if the item does not exist.
// Runs in O(log n + log k) time.
    public void remove(T item){
    MyPriorityQueue<T> currentHeap = itemToHeap.get(item);
    if (currentHeap == null) throw new IllegalArgumentException();

    currentHeap.remove(item);
    itemToHeap.remove(item);
    size--;

    // if we remove an item, we need a replacement item from the rest group
    if (currentHeap == topK && !rest.isEmpty()) {
        T replacement = rest.extract();
        topK.insert(replacement);
        itemToHeap.put(replacement, topK);
    }


    }
}
