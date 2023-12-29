import java.util.*;
import java.util.List;

public class Node{
    String name;
    String service;
    Node nextNode;
    Node prevNode;
    boolean safety_conecction = false;
    boolean status;  // represents the node status ex: broken or not
    Map<String,Double> EdgesMap = new HashMap<>(); // carries the relatives distances to the other nodes in the ring


    public Node(String name,String service, Boolean status){
        this.name = name;
        this.service = service;
        this.nextNode =  null;
        this.prevNode = null;
        this.status = status;
    }
    public void addEdge(String targetName,Double weigh){
        this.EdgesMap.put(targetName,weigh);
    }
}

