import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuffmanTree {
    private TreeNode root;
    private HashMap<Integer, String> map;
    private int numInternalNodes;
    private int numLeafNodes;

    final private static String RIGHT = "1";
    final private static String LEFT = "0";
    final private static int INTERNAL_NODE = -1;

    // Constructor, creates empty HuffmanTree
    public HuffmanTree() {
        root = null;
        map = new HashMap<>();
        numLeafNodes = 0;
        numInternalNodes = 0;
    }

    // Constructor, creates HuffmanTree from given PriorityQueue
    public HuffmanTree(PriorityQueue<TreeNode> q){
        if (q == null){
            throw new IllegalArgumentException("PriorityQueue<TreeNode> q cannot be null");
        }
        // iterates through queue
        numLeafNodes = q.size();
        while(q.size() > 1) {
            // takes two TreeNodes from the front and combines into newTree, which is
            // then enqueued to the q
            TreeNode leftSubTree = q.dequeue();
            TreeNode rightSubTree = q.dequeue();
            TreeNode newTree = new TreeNode(leftSubTree, INTERNAL_NODE,rightSubTree);
            numInternalNodes++;
            q.enqueue(newTree);
        }
        root = q.dequeue();
        map = new HashMap<>();
        fillMap(root, "");

    }

    // given TreeNode root, fills instance variable map with paths to characters 
    // in HuffmanTree
    private void fillMap(TreeNode curr, String currChunk){
        if (curr.isLeaf()){
            map.put(curr.getValue(), currChunk);
        } else {
            fillMap(curr.getLeft(), currChunk + LEFT);
            fillMap(curr.getRight(), currChunk + RIGHT);
        }
    }

    // returns instance variable map, which contains paths to characters in HuffmanTree
    public HashMap<Integer, String> getMap(){
        return map;
    }

    // returns List of TreeNodes found in order in the HuffmanTree
    public List<TreeNode> getAllInOrder() {
        List<TreeNode> result = new ArrayList<>();
        getAllInOrderHelper(root, result);
        return result;
    }

    // helper method for getAll
    // adds all values of Huffman Tree into result in ascending order (in order)
    private void getAllInOrderHelper(TreeNode current, List<TreeNode> result) {
        if (current != null) {
            // adds left subtree
            getAllInOrderHelper(current.getLeft(), result);
            // adds current node
            result.add(current);
            // adds right subtree
            getAllInOrderHelper(current.getRight(), result);
        }

    }

    // returns List of TreeNodes found pre order in the HuffmanTree
    public List<TreeNode> getAllPreOrder() {
        List<TreeNode> result = new ArrayList<>();
        getAllPreOrderHelper(root, result);
        return result;
    }

    // helper method for getAll
    // adds all values of Huffman Tree into result in pre order
    private void getAllPreOrderHelper(TreeNode current, List<TreeNode> result) {
        if (current != null) {
            // adds current node
            result.add(current);
            // adds left subtree
            getAllPreOrderHelper(current.getLeft(), result);
            // adds right subtree
            getAllPreOrderHelper(current.getRight(), result);
        }

    }

    // Returns number of leaf nodes
    public int getNumLeafNodes(){
        return numLeafNodes;
    }

    // Returns number of internal nodes
    public int getNumInternalNodes() {
        return numInternalNodes;
    }

    // adds val to priority queue 
    public TreeNode getRoot() {
        return root;
    }

    // adds new TreeNode with value val to HuffmanTree, used when remaking tree with SCF
    // pre: none
    // post: none
    public void add(int val) {
        if (root == null) {
            root = new TreeNode(val, 0);
        } else {
            addHelper(root, val);
        }
    }

    // recursive helper method for add method, places new TreeNode at approriate place
    private boolean addHelper(TreeNode n, int val) {
        if (n.getValue() == INTERNAL_NODE) { // has not reached leaf node
            if (n.isLeaf()) { // internal node is leaf, add to left side
                n.setLeft(new TreeNode(val, 0));
                return true; // has added, ends recursion
            } else { // internal node has at least one child, go left, then go right
                boolean added = addHelper(n.getLeft(), val);
                // attempts to add to right if does not add to left
                if (!added) {
                    TreeNode right = n.getRight();
                    if (right == null) {
                        n.setRight(new TreeNode(val, 0));
                        added = true;
                    } else {
                        added = addHelper(right, val);
                    }
                }
                return added;
            }
        }
        return false; // default base case: does not add if not internal node
    }

    // helps SimpleHuggProcessor reach leaf node of character
    // pre: n != null, bit == 0 || bit == 1
    // post: returns TreeNode to the left, right of TreeNode n, or returns root
    public TreeNode getValue(TreeNode n, int bit) {
        if (bit != 0 && bit != 1) {
            throw new IllegalArgumentException("Int bit must be 0 or 1");
        }
        if (n == null) {
            return root;
        } else {
            if (LEFT.equals(Integer.toString(bit))) {
                return n.getLeft();
            } else {
                return n.getRight();
            }
        }
    }

}

