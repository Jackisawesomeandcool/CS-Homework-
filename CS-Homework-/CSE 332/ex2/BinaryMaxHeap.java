import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

public class BinaryMaxHeap <T extends Comparable<T>> implements MyPriorityQueue<T> {    
    private int size; // Maintains the size of the data structure
    private T[] arr; // The array containing all items in the data structure
                     // index 0 must be utilized
    private Map<T, Integer> itemToIndex; // Keeps track of which index of arr holds each item.

    public BinaryMaxHeap(){
        // This line just creates an array of type T. We're doing it this way just
        // because of weird java generics stuff (that I frankly don't totally understand)
        // If you want to create a new array anywhere else (e.g. to resize) then
        // You should mimic this line. The second argument is the size of the new array.
        arr = (T[]) new Comparable[10];
        size = 0;
        itemToIndex = new HashMap<>();
    }

    //makes a new swap method, that takes two integer inputs and then swaps them
    //The inputs are the two indexes of the array whos values you want to swap. 
    //This works by utilizing a temporary variable to hold a value while setting the other index to the temporary value
    private void swap(int i, int j){
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;

        //syncronizes the map with the new array positions
        itemToIndex.put(arr[i], i);
        itemToIndex.put(arr[j],j );
    }



    // move the item at index i "rootward" until
    // the heap property holds
    private void percolateUp(int i){
        while(i>0){
            int parentIndex = (i - 1) / 2; //find the index of the parent of the inserted function
            if(arr[i].compareTo(arr[parentIndex]) > 0) {
                swap(i,parentIndex);
                i = parentIndex; //moves the pointer up to new position 
            }
            else{
                break;
            }
        }
    }

    // move the item at index i "leafward" until
    // the heap property holds
    private void percolateDown(int i){
        while(true){
            int leftbranch = 2*i+1;
            int rightbranch = 2*i+2;
            int largest = i;

            if(leftbranch < size && arr[leftbranch].compareTo(arr[largest]) > 0){
                largest = leftbranch;
            } // checks if left child exists, and is smaller than the current smallest values
            
            if(rightbranch < size && arr[rightbranch].compareTo(arr[largest]) > 0){
                largest = rightbranch;
            }// checks if right child exists, and is smaller than the current smallest values
            
            if(largest == i) break;


            swap(i, largest);
            i = largest; 
        }
    }

    // copy all items into a larger array to make more room.
    private void resize(){
        T[] larger = (T[]) new Comparable[arr.length * 2];
        for(int i = 0; i < arr.length; i++){
            larger[i] = arr[i];
        }
        arr = larger;
    }

    public void insert(T item){
        if(item == null) throw new IllegalArgumentException();
        if(size == arr.length) resize();

        arr[size] = item;
        itemToIndex.put(item,size);

        size++;
        percolateUp(size -1);

    }


    public T extract(){
        if(isEmpty()) throw new IllegalStateException();

        T root = arr[0];

        T lastbranch = arr[size - 1];
        arr[0] = lastbranch;

        itemToIndex.remove(root);
        if(size > 1){
            itemToIndex.put(lastbranch, 0);

        }

        size--;
        if(size > 0){
            percolateDown(0);
        }
        return root;
    }

    // Remove the item at the given index.
    // Make sure to maintain the heap property!
    private T remove(int index){
        if(index < 0 || index >= size){
            throw new IndexOutOfBoundsException();
        }

        T removedItem = arr[index];
        T lastItem = arr[size - 1];

        arr[index] = lastItem;
        itemToIndex.remove(removedItem);
        if(index < size - 1){
            itemToIndex.put(lastItem, index);

        }
        size--;

        if(index < size) {
            updatePriority(index);

        }
        return removedItem;
    }

    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    @Override
    public void remove(T item){
        if (!itemToIndex.containsKey(item)){
            throw new IllegalArgumentException("item not found");
        }
        remove(itemToIndex.get(item));
    }

    // Determine whether to percolate up/down
    // the item at the given index, then do it!
    private void updatePriority(int index){
        int rootIndex = (index - 1) / 2;

        //If smaller than the parent, move up, otherwise percolate down
        if (index > 0 && arr[index].compareTo(arr[rootIndex]) > 0){
            percolateUp(index);

        }else{
            percolateDown(index);
        }
    }

    // This method gets called after the client has 
    // changed an item in a way that may change its
    // priority. In this case, the client should call
    // updatePriority on that changed item so that 
    // the heap can restore the heap property.
    // Throws an IllegalArgumentException if the given
    // item is not an element of the priority queue.
    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    public void updatePriority(T item){
	    if(!itemToIndex.containsKey(item)){
            throw new IllegalArgumentException("Given item is not present in the priority queue!");
	    }
        updatePriority(itemToIndex.get(item));
    }

    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    public boolean isEmpty(){
        return size == 0;
    }

    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    public int size(){
        return size;
    }

    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    public T peek(){
        if(isEmpty()){
            throw new IllegalStateException();
        }
        return arr[0];
    }
    
    // We have provided a recommended implementation
    // You're welcome to do something different, though!
    public List<T> toList(){
        List<T> copy = new ArrayList<>();
        for(int i = 0; i < size; i++){
            copy.add(i, arr[i]);
        }
        return copy;
    }

    // For debugging
    public String toString(){
        if(size == 0){
            return "[]";
        }
        String str = "[(" + arr[0] + " " + itemToIndex.get(arr[0]) + ")";
        for(int i = 1; i < size; i++ ){
            str += ",(" + arr[i] + " " + itemToIndex.get(arr[i]) + ")";
        }
        return str + "]";
    }
    
}
