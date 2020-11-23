import java.util.Queue;

public class TEST {
    public static void main(String[] args) {
        PriorityQueue<TreeNode> queue = new PriorityQueue<>();
        queue.enqueue(new TreeNode(1, 5));
        queue.enqueue(new TreeNode(2, 10));
        queue.enqueue(new TreeNode(3, 15));
        System.out.println("First test: " + queue.toString());

        queue.enqueue(new TreeNode(4, 0));
        queue.enqueue(new TreeNode(5, 10));
        queue.enqueue(new TreeNode(6, 20));
        System.out.println("Second test: " + queue.toString());

        queue.dequeue();
        queue.dequeue();
        System.out.println("Third test: " + queue.toString());

    }
}
