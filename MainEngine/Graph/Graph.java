package MainEngine.Graph;

import java.util.HashSet;
import java.util.Set;

public class Graph {

    public Graph(){}

    public void AddArc(String labelA, String labelB, float cost){
        Node nodeA = new Node(labelA);
        Node nodeB = new Node(labelB);
        nodes.add(nodeA);
        nodes.add(nodeB);
        arcs.add(new Arc(nodeA, nodeB, cost));
    }

    public void AddArc(Node nodeA, Node nodeB, float cost){
        arcs.add(new Arc(nodeA, nodeB, cost));
    }
    
    private Set<Node> nodes = new HashSet<Node>();
    private Set<Arc> arcs = new HashSet<Arc>();
}
