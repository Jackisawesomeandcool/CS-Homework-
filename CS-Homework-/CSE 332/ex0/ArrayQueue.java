public class ArrayQueue<T> implements MyQueue<T>{
    // We've provided you several data fields to implement the circular array
    // Feel free to add some more if you need!
    // Please do not change the visibility of [data].
    public T[] data; // store the data
    private int first; // index of the head of the queue
    private int last; // index of the tail of the queue
    private int size; // size of the circular array

    // We've provided you a default implementation of constructor for the ArrayQueue
    public ArrayQueue(){
        this.data = (T[]) new Object[10]; // DO NOT CHANGE INITIAL SIZE HERE
        first = 0;
        last = 0;
    }

      // Adds an item into the queue.
    public void enqueue(T item){
        if(data.length == size){
            resize(data.length * 2);
        }
        data[last] = item;
        
        // makes sure that the first data point respects the wrap-around nature of a circular array by modding the lenght of the array
        last = (last + 1) % data.length; 
        size++;
    }


    // Removes and returns the least-recently added item from the queue
    // Throws an IllegalStateException if the queue is empty
    public T dequeue() {
        if(isEmpty()){ // Throws an exception if there is nothing in the queue to dequeue
            throw new IllegalStateException("There is nothing in the queue");
        }
        T result = data[first];

        data[first] = null;

        //makes sure to wrap around 
        first = (first+1) % data.length;

        size--;

        return result;
    }

    // Returns the least-recently added item from the queue
    // Throws an IllegalStateException if the queue is empty
    public T peek(){
        if(isEmpty()){ // Throws an exception if there is nothing in the queue to dequeue
            throw new IllegalStateException("There is nothing in the queue");
        }
        return data[first];
    }

    // Return the number of items currently in the queue
    public int size(){
        return size;
    }

    // Returns a boolean to indicate whether the queue has items
    public boolean isEmpty(){
        return size == 0;
    }

    public void resize(int newCapacity){
        // Create a new array of the new capacity
        T[] newArray = (T[]) new Object[newCapacity];

        // Copy elements in  order i from 0 to size:
        for (int i = 0; i < size; i++) {
            // Offset the first by 'i' to get the elements in order
            newArray[i] = data[(first + i) % data.length];
        }

        // Replace the old array with the new array
        data = newArray;

        //Reset indices to match new array
        first = 0;
        last = size;
    }

}
