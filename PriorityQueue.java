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

import java.util.LinkedList;
import java.util.Iterator;

// Priority Queue of nodes, giving precedence to the order of insertion in tiebreakers
public class PriorityQueue <E extends Comparable<? super E>>{

    private LinkedList<E> con;

    public PriorityQueue(){
        con = new LinkedList<>();        
    }

    public void enqueue(E other) {
        if (con.size() == 0 || con.getLast().compareTo(other) <= 0 ){
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
    public int size(){
        return con.size();
    }
    
    public E dequeue(){
        return con.removeFirst();
    }

    public boolean isEmpty() {
        return con.isEmpty();
    }

    public E peek(){
        return con.peek();
    }

    public String toString() {
        return con.toString();
    }

}

