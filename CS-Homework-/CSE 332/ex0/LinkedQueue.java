public class LinkedQueue<E> implements MyQueue<E>{
    
    private static class ListNode<E>{
        private final E data;
        private ListNode<E> next;


        private ListNode(E data, ListNode<E> next){
            this.data = data;
            this.next = next;
        }
        private ListNode(E data){
            this.data = data;
        }
    }
    private ListNode<E> front; //front of the list (head)
    private ListNode<E> back; // back of the list (tail)
    private int size;

    public LinkedQueue() {
        front = null;
        back = null;
        size = 0;

    }

    //adds an item to the queue
    @Override
    public void enqueue(E item){
        ListNode<E> newNode = new ListNode<>(item);
       
        //if this is the first item in the queue, it is naturally the front and the back value
        if(isEmpty()) {
            front = newNode;
            back = newNode;
        }
        else{ 
            back.next = newNode; 
            back = newNode;
        }
        size++;
    }

    // This method takes out the most recently added thing (front) and returns it's value.
    // It does this by setting result to front's data, and then moving the value of front to the next value
    @Override
    public E dequeue() {
        if(isEmpty()){ // Throws an exception if there is nothing in the queue to dequeue
            throw new IllegalStateException("There is nothing in the queue");
        }
        E result = front.data;
        front = front.next;
        size--;

        if(front == null) { // We removed the last item in the queue, we must make the entire queue == null{
            back = null;    
        }
    
        return result;
    }


    // returns the frontmost data point, does not remove from the array, throws exception if empty 
    @Override
    public E peek(){
        if(isEmpty()) throw new IllegalStateException("Queue is empty");
        return front.data;
    }

    
    // Return the number of items currently in the queue
    @Override
    public int size(){
        return size;
    }

    // Returns a boolean to indicate whether the queue has items
    @Override
    public boolean isEmpty() {
        return size == 0;
    }



}
