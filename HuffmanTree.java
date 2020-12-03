/*  Student information for assignment:
 *
 *  On <MY|OUR> honor, Donnel and Akif), this programming assignment is <MY|OUR> own work
 *  and <I|WE> have not provided this code to any other student.
 *
 *  Number of slip days used:
 *
 *  Student 1 (Student whose turnin account is being used)
 *  UTEID: dz5298
 *  email address: dzhuhaocheng2013@gmail.com
 *  Grader name: Noah Beal
 *
 *  Student 2
 *  UTEID: asa3676
 *  email address: akif.abidi@utexas.edu
 *
 */

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

// Stores TreeNodes in a binary tree for Huffman compression and decompression
public class HuffmanTree implements IHuffConstants{
    private TreeNode root;
    private HashMap<Integer, String> map;
    private int numInternalNodes;
    private int numLeafNodes;
    private TreeNode traversalNode;

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

    // creates Huffman Tree from given array by using a Priority Queue
    public HuffmanTree(int[] array){
        PriorityQueue<TreeNode> q = new PriorityQueue<>();
        // fills q with treeNodes made from freq
        createQueue(array, q);
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

    // fills up a PriorityQueue with TreeNodes made from uncompressed file
    // pre: q != null
    private void createQueue(int[] freq, PriorityQueue<TreeNode> q) {
        if (q == null) {
            throw new IllegalArgumentException("PriorityQueue q cannot be null");
        }
        for (int i = 0; i < freq.length; i++) {
            // if the frequency of a character is > 0, it is added to q as a TreeNode
            if (freq[i] != 0) {
                TreeNode treeNode = new TreeNode(i, freq[i]);
                q.enqueue(treeNode);
            }
        }
        // enqeue TreeNode with PSEUDO_EOF value last
        q.enqueue(new TreeNode(PSEUDO_EOF, 1));
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

    // helps SimpleHuffProcessor reach leaf node of character
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

    public void createTreeHeader(BitOutputStream out) {
        // add the data for the tree
        for (TreeNode node : getAllPreOrder()) {
            if (node.isLeaf()) {
                out.writeBits(1, 1);
                out.writeBits(BITS_PER_WORD + 1, node.getValue());
            } else {
                out.writeBits(1, 0);
            }
        }
    }

    // place in tree class
    // decompresses file using the uncompressingTree
    // pre: none
    // returns the updated decompressedSize
    public int decompressMainBody(BitInputStream bin, BitOutputStream bout, int decompressedSize, HuffmanTree uncompressingTree) throws IOException {
        int bit = bin.readBits(1);
        TreeNode currentNode = uncompressingTree.getValue(null, bit);

        while (currentNode != null) {
            // reached leaf node with value
            if (currentNode.isLeaf()) {
                int val = currentNode.getValue();
                // has not reached end of file, write out val and reset currentNode
                // to root
                if (val != PSEUDO_EOF) {
                    bout.write(val);
                    decompressedSize += BITS_PER_WORD;
                    currentNode = uncompressingTree.getValue(null, bit);
                } else {
                    // currentNode set to null ends while loop
                    currentNode = null;
                }
            } else {
                // internal node, keep going down tree depending on bit
                currentNode = uncompressingTree.getValue(currentNode, bit);
                bit = bin.readBits(1);
            }
        }
        return decompressedSize;
    }

}

