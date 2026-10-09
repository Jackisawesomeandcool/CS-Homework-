
public class AVLTree<K extends Comparable<K>, V> extends BinarySearchTree<K, V> {

    public AVLTree() {
        super();
    }

    public V insert(K key, V value) {
        V prevValue = find(key);
        this.root = insertHelper(key, value, root);
        return prevValue;
        
    }
    public TreeNode<K,V> insertHelper(K key, V value, TreeNode <K,V> node){
        if(node == null){  
            size++;
            return new TreeNode<>(key, value);
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0){
            node.left = insertHelper(key, value, node.left);

        }
        else if(cmp > 0){
            node.right = insertHelper(key, value, node.right);
        }
        else{
            node.value = value;
            return node;
        }
        node.updateHeight();
        int leftHeight = (node.left == null) ? -1 : node.left.height;  // left and right heights can be null, in which case they should be set to -1
        int rightHeight = (node.right == null) ? -1 : node.right.height;
        int balance = leftHeight - rightHeight;

        // rotate left and left 
        if (balance > 1 && key.compareTo(node.left.key) < 0) {
            return rotateRight(node);
        }
        
        // Right Right
        if (balance < -1 && key.compareTo(node.right.key) > 0) {
            return rotateLeft(node);
        }

       // Left Right 
        if (balance > 1 && key.compareTo(node.left.key) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

    // rotate right and then left
        if (balance < -1 && key.compareTo(node.right.key) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node; 

    }


    

    private TreeNode<K,V> rotateRight(TreeNode<K,V> inputNode){
        TreeNode<K,V> temp = inputNode.left;

        inputNode.left = temp.right;

        temp.right = inputNode;


        inputNode.updateHeight();
        temp.updateHeight();
        return temp;

    }
//correct logic for rotateRight is different
// you assign the input node.left to a temp node
// then on the input node.left make it the temp node.right
// then the temp.right should just be the input node
// update heights
// return temp
    private TreeNode<K,V> rotateLeft(TreeNode<K,V> inputNode){
        TreeNode<K,V> temp = inputNode.right;

        inputNode.right = temp.left;

        temp.left = inputNode;


        inputNode.updateHeight();
        temp.updateHeight();
        return temp;

    }

}
