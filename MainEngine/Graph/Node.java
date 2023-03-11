package MainEngine.Graph;

import java.util.HashSet;
import java.util.Set;

public class Node {

    public Node(String _label){
        label = _label;
    }

    public void AddNeighbor(Node neighbor){
        neighbors.add(neighbor);
    }

    public String GetLabel() {
        return label;
    }

    public Set<Node> GetNeighbors() {
        return neighbors;
    }

    private String label;
    private Set<Node> neighbors = new HashSet<Node>();
}
