import java.util.LinkedList;
import java.util.Iterator;

public class PriorityQueue <E extends Comparable<? super E>>{

    private LinkedList<E> con;


    public PriorityQueue(){
        con = new LinkedList<>();        
    }

    public void enqueue(E other) {
        if (con.size() == 0 || con.getLast().compareTo(other) < 0 ){
            con.add(other);
        } else {
            Iterator<E> iter = con.iterator();
            int indx = 0;
            while(iter.hasNext()) {
                E currNode = iter.next();
                if  (currNode.compareTo(other) > 0){
                    con.add(indx, other);
                    return;
                } 
                indx++;
            }
        }
    }
    
    public E dequeue(){
        return con.removeFirst();
    }

    //TODO: REMOVE IF NECESSARY
    public E peek(){
        return con.getFirst();
    }

    public String toString(){
        return con.toString();
    }

    public int size(){
        return con.size();
    }
}
