package MainEngine.Graph;

public class Arc {
    
    public Arc(Node _source, Node _destination, float _cost){
        source = _source;
        destination  = _destination;
        cost = _cost;

        source.AddNeighbor(destination);
        destination.AddNeighbor(source);
    }

    public float GetCost() {
        return cost;
    }

    private Node source;
    private Node destination;
    private float cost;
}
