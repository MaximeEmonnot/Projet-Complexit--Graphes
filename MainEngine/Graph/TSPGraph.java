package MainEngine.Graph;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TSPGraph {

    public TSPGraph(String path) throws Exception{
    	
    	distances = new HashMap<UnorderedPair,Integer>();
    	nodes = new HashSet<String>();
    	
    	List<String> lines =  Files.readAllLines(Paths.get(path));
        for (String l : lines){
        	String[] tmp = l.split("\t");
        	addArc(tmp[0], tmp[1], Integer.parseInt(tmp[2]));
        }
        if(!isTSPValid())
        	throw new Exception("It is not a valid TSP graph");
    }
    
    private void addArc(String a, String b, int cost) {
    	distances.put(new UnorderedPair(a, b), cost);
    	nodes.add(a);
    	nodes.add(b);
    }
    
    public Integer getCost(String a, String b) {
    	return distances.get(new UnorderedPair(a, b));
    }
    
    // Verifie si tous les couples de noeuds sont reliés et si les couts sont superieurs ou egaux a 0 
    private boolean isTSPValid() {
    	Set<String> unvisited = new HashSet<String>(nodes);
    	while(unvisited.size() != 0) {
    		String node = (String) unvisited.toArray()[0];
    		unvisited.remove(node);
    		for(String other : unvisited) {
    			UnorderedPair pair = new UnorderedPair(node, other);
    			if(!distances.containsKey(pair) || distances.get(pair) < 0)
    				return false;
    		}	
    	}
    	return true;
    }

    private Map<UnorderedPair,Integer> distances;
    private Set<String> nodes;
}
