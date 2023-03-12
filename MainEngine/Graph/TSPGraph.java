package MainEngine.Graph;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import GraphicsEngine.GraphicsSystem;

import java.awt.*;

public class TSPGraph {

    public TSPGraph(String path) throws Exception{
    	
    	distances = new HashMap<UnorderedPair,Integer>();
    	nodes = new HashSet<String>();
    	
    	List<String> lines =  Files.readAllLines(Paths.get(path));
        for (String l : lines){
        	String[] tmp = l.split(" ");
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

	// Affichage des nodes : Principe des racines n-ième de l'unité
	public void Draw(Point center, float radius){
		Draw(center, radius, 0);
	}
	public void Draw(Point center, float radius, int priority){
		Set<Point> points = new HashSet<Point>();
		int index = 0;
		for (String node : nodes){
			// Affichgge d'un noeud
			int nodeRadius = (int)(radius * (1.f - nodes.size() * 0.01f) / 5.f) ;
			Point nodeLocation = new Point();
			nodeLocation.x = center.x + (int)(Math.sin(2 * Math.PI * index / nodes.size()) * radius) - nodeRadius;
			nodeLocation.y = center.y - (int)(Math.cos(2 * Math.PI * index / nodes.size()) * radius) - nodeRadius;
			points.add(nodeLocation);
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(new Rectangle(nodeLocation, new Dimension( nodeRadius + 2, nodeRadius + 1)), 
																	  new Dimension(nodeRadius + 2, nodeRadius + 2), Color.BLACK, true, priority);
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(new Rectangle(nodeLocation, new Dimension( nodeRadius, nodeRadius)), 
																	  new Dimension(nodeRadius, nodeRadius), Color.WHITE, true, priority + 2);
																	  
			
			nodeLocation.x += nodeRadius / 3;
			nodeLocation.y += nodeRadius / 1.5;
			GraphicsEngine.GraphicsSystem.GetInstance().DrawText(node, nodeLocation, Color.BLACK, priority + 3);
			index++;
		}
		// Affichage des arcs
		for (Point pointA : points) for (Point pointB : points) GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, Color.BLACK, priority);

	}
    
    // Verifie si tous les couples de noeuds sont reli�s et si les couts sont superieurs ou egaux a 0 
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
