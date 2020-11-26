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

    public HuffmanTree(PriorityQueue<TreeNode> q){
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

    public HashMap<Integer, String> getMap(){
        return map;
    }

    private void fillMap(TreeNode curr, String currChunk){
        if (curr.isLeaf()){
            map.put(curr.getValue(), currChunk);
        } else {
            fillMap(curr.getLeft(), currChunk + LEFT);
            fillMap(curr.getRight(), currChunk + RIGHT);
        }
    }

    public List<TreeNode> getAllInOrder() {
        List<TreeNode> result = new ArrayList<>();
        getAllInOrderHelper(root, result);
        return result;
    }

    // helper method for getAll
    // adds all values of BST into result in ascending order
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

    public List<TreeNode> getAllPreOrder() {
        List<TreeNode> result = new ArrayList<>();
        getAllPreOrderHelper(root, result);
        return result;
    }

    // helper method for getAll
    // adds all values of BST into result in ascending order
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

    public int getNumLeafNodes(){
        return numLeafNodes;
    }

    public int getNumInternalNodes() {
        return numInternalNodes;
    }


    /*public String makeHeader(){
        String header = "";
        makeHeaderHelper(root, header);
        header = Integer.toBinaryString(header.length()) + header;
        return header;
    }
    // helper method for makeHeader(), iterates through tree in pre-order and creates rest of
    // header string
    private void makeHeaderHelper(TreeNode node, String header){
        if (node.isLeaf()){
            header = header.concat("1" + Integer.toBinaryString(node.getValue()));
        } else {
            header += "0";
            makeHeaderHelper(node.getLeft(), header);
            makeHeaderHelper(node.getRight(), header);
        }
    }*/
}
