public class LinkedStack<T> implements MyStack<T> {
    
    private static class ListNode<T>{
        private final T data;
        private ListNode<T> next;

        private ListNode(T data, ListNode<T> next){
            this.data = data;
            this.next = next;
        }
        
        private ListNode(T data){
            this.data = data;
        }
    }
    private ListNode<T> top; //top of the stack 
    
    private int size;

    public LinkedStack() {
        top = null;
        size = 0;

    }
    //adds an item to the top of the stack
    @Override
    public void push(T item){
        top = new ListNode<>(item,top);
        size++;
    }

    // This method takes out the most recently added thing (front) and returns it's value.
    // It does this by setting result to front's data, and then moving the value of front to the next value
    @Override
    public T pop() {
        if(isEmpty()){ // Throws an exception if there is nothing in the Stack to pop
            throw new IllegalStateException("There is nothing in the Stack");
        }
        T result = top.data;
        top = top.next;
        size--;
        return result;
    }


    // returns the frontmost data point, does not remove from the Stack, throws exception if empty 
    @Override
    public T peek(){
        if(isEmpty()) throw new IllegalStateException("Stack is empty");
        return top.data;
    }

    
    // Return the number of items currently in the Stack
    @Override
    public int size(){
        return size;
    }

    // Returns a boolean to indicate whether the Stack has items
    @Override
    public boolean isEmpty() {
        return size == 0;
    }




}
