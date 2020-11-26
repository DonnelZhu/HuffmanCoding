import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HuffmanTree {
    private TreeNode root;
    private HashMap<Integer, String> map;
    final private static String RIGHT = "1";
    final private static String LEFT = "0";
    final private static int INTERNAL_NODE = -1;

    public HuffmanTree(PriorityQueue<TreeNode> q){
        while(q.size() > 1) {
            TreeNode leftSubTree = q.dequeue();
            TreeNode rightSubTree = q.dequeue();
            TreeNode newTree = new TreeNode(leftSubTree, INTERNAL_NODE,rightSubTree);
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
            System.out.println("added " + curr);
            map.put(curr.getValue(), currChunk);
        } else {
            fillMap(curr.getLeft(), currChunk + LEFT);
            fillMap(curr.getRight(), currChunk + RIGHT);
        }
    }

    public List<TreeNode> getAll() {
        List<TreeNode> result = new ArrayList<>();
        getAllHelper(root, result);
        return result;
    }

    // helper method for getAll
    // adds all values of BST into result in ascending order
    private void getAllHelper(TreeNode current, List<TreeNode> result) {
        if (current != null) {
            // adds left subtree
            getAllHelper(current.getLeft(), result);
            // adds current node
            result.add(current);
            // adds right subtree
            getAllHelper(current.getRight(), result);
        }

    }


}
