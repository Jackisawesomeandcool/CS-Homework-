import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;

public class ChainingHashTable<K, V> implements DeletelessDictionary<K, V> {
    public List<Item<K, V>>[] table; // The table itself is an array of linked lists of items.
    private int size;
    private static int[] primes = {11, 23, 47, 97, 197, 397};

    public ChainingHashTable() {
        table = (LinkedList<Item<K, V>>[]) Array.newInstance(LinkedList.class, primes[0]);
        for (int i = 0; i < table.length; i++) {
            table[i] = new LinkedList<>();
        }
        size = 0;
    }

    private void rehash() {
        int newSize = 0;

        if(table.length <= 197) {

            while (newSize < primes.length && table.length >= primes[newSize]) {

                newSize++;

            }

            newSize = primes[newSize];

        } else if (table.length == 397){

            newSize = 513;

        } else {

            newSize = 2*table.length - 1;

        }

        List<Item<K, V>>[] prevTable = this.table;

        List<Item<K, V>>[] newTable = (LinkedList<Item<K, V>>[]) Array.newInstance(LinkedList.class, newSize);

        this.table = newTable;

        for (int i = 0; i < this.table.length; i++) {

            this.table[i] = new LinkedList<>();

        }

        int index;

        this.size = 0;

        for(List<Item<K, V>> list : prevTable) {

            for(Item<K, V> item : list) {

                index = item.key.hashCode() % this.table.length;

                newTable[index].add(item);

                this.size++;

            }

        }

    }

    

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public V insert(K key, V value) {
        if(key == null){
            throw new IllegalArgumentException("you cannot insert a null key");
        }
        
        if(size/table.length >= 2) {

            rehash();
        }

        int getIndex = key.hashCode() % table.length;
        
        List<Item<K, V>> thing = table[getIndex];
        for (Item<K, V> item : thing) {
            if (item.key.equals(key)) {
                V old = item.value;
                item.value = value; // Update the value in place
                return old;    // Return the old value per Dictionary contract
            }
        }

        thing.add(new Item<>(key, value));
        size++;

        

        return null;

    }

        
        
    

    public V find(K key) {
        if(key == null){
            return null;
        }

        int getIndex = Math.abs(key.hashCode() % table.length);
        for(Item<K,V> thing : table[getIndex]){
            if(thing.key.equals(key)){
                return thing.value;
            }
        }
        return null;
    }

    public boolean contains(K key) {
    
        return find(key) != null;
    }

    public List<K> getKeys() {
        List<K> key = new ArrayList<>();
        for(int i = 0; i < table.length;i++){
            for(Item<K,V> item : table[i]){
                key.add(item.key);
            }
        }
        return key;
    }
    
    public List<V> getValues() {
        List<V> value = new ArrayList<>();
        for(int i = 0; i < table.length;i++){
            for(Item<K,V> item : table[i]){
                value.add(item.value);
            }
        }
        return value;
    }

    public String toString() {
        String s = "{";
        s += table[0];
        for (int i = 1; i < table.length; i++) {
            s += "," + table[i];
        }
        return s + "}";
    }

}
