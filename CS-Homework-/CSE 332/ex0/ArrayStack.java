
public class ArrayStack<T> implements MyStack<T> {

    private T[] elements;   // array storing stack elements
    private int size;       // number of elements currently in the stack

   
    public ArrayStack() {
        elements = (T[]) new Object[1];
        size = 0;
    
    }

    // adds a new item to the stack
    @Override
    public void push(T item){
        if(size == elements.length){ //make new array if full
            reSize(elements.length * 2);
        }
        elements[size] = item; //the last element is set to the value of what you are trying to add, size increases by 1 because we added something 
        size++;
    }

    // returns and removes the last item in the stack
    @Override
    public T pop(){
        if(isEmpty()) throw new IllegalStateException("Stack is Empty!");

        size--; //decremement size so you can properly remove the last item due to the 0 based indexing 
        T result = elements[size]; //makes variable so we can return the value
        elements[size] = null; //gets rid of the pop'd value
        return result;
    }


    //returns but does not remove the last item in the stack
     @Override
    public T peek(){
        if(isEmpty()) throw new IllegalStateException("Stack is empty");
        return elements[size - 1];

    }

    @Override
    public int size(){ //returns the size of the array
        return size;
    }

    @Override
    public boolean isEmpty(){ //checks to see if the stack is empty
        return size == 0;
    }

    public void reSize(int newCapacity){ //method for resizing the array.Basically 
        T[] newArray = (T[]) new Object[newCapacity];
        for(int i = 0; i < size;i++){
            newArray[i] = elements[i];
        }
        elements = newArray;
    }
}
