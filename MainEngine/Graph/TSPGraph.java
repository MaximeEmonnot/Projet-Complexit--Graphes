package MainEngine.Graph;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.awt.*;

public class TSPGraph {

    public TSPGraph(String path, Point _center, float _radius) throws Exception{
    	
    	distances = new HashMap<UnorderedPair,Integer>();
    	nodes = new HashSet<String>();
		cycle = new HashMap<UnorderedPair, Integer>();
		center = _center;
		radius = _radius;
    	
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

	public void Update() {
		CoreEngine.Mouse.EEventType event = CoreEngine.Mouse.GetInstance().Read();
		if (event == CoreEngine.Mouse.EEventType.LRelease){
			Point mousePos = CoreEngine.Mouse.GetInstance().GetMousePos();
			int index = 0;
			for (String node : nodes ){
				int nodeRadius = (int)(radius * (1.f - nodes.size() * 0.01f) / 5.f) ;
				Point nodeLocation = new Point();
				nodeLocation.x = center.x + (int)(Math.sin(2 * Math.PI * index / nodes.size()) * radius) - nodeRadius;
				nodeLocation.y = center.y - (int)(Math.cos(2 * Math.PI * index / nodes.size()) * radius) - nodeRadius;
				Rectangle rect = new Rectangle(nodeLocation, new Dimension(nodeRadius, nodeRadius));
				if (rect.contains(mousePos)){
					if (selectedNode.equals(node)) selectedNode = "";
					else selectedNode = node;
					break;
				}
				index++;
			}
		}
	}

	// Affichage des nodes : Principe des racines n-ième de l'unité
	public void Draw(){
		Draw(0);
	}
	public void Draw(int priority){
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
			
			Color nodeColor = Color.WHITE;
			if (node.equals(selectedNode)) nodeColor = Color.BLUE;
			if (node.equals(firstNode)) nodeColor = Color.GREEN;
												  
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(new Rectangle(nodeLocation, new Dimension( nodeRadius, nodeRadius)), 
																	  new Dimension(nodeRadius, nodeRadius), nodeColor, true, priority + 2);
																	  
			
			nodeLocation.x += nodeRadius / 3;
			nodeLocation.y += nodeRadius / 1.5;
			GraphicsEngine.GraphicsSystem.GetInstance().DrawText(node, nodeLocation, Color.BLACK, priority + 3);
			index++;
		}
		// Affichage des arcs
		if (cycle.isEmpty()) for (Point pointA : points) for (Point pointB : points) 
			GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, Color.BLUE, priority);
		else{
			int nodeRadius = (int)(radius * (1.f - nodes.size() * 0.01f) / 5.f) ;
			int indexA = 0;
			for (String nodeA : nodes) {
				Point nodeALocation = new Point();
				nodeALocation.x = center.x + (int)(Math.sin(2 * Math.PI * indexA / nodes.size()) * radius) - 2 * nodeRadius / 3;
				nodeALocation.y = center.y - (int)(Math.cos(2 * Math.PI * indexA / nodes.size()) * radius) - nodeRadius / 3;
				int indexB = 0;
				for (String nodeB : nodes){
					if (!nodeB.equals(nodeA)) {
						Point nodeBLocation = new Point();
						nodeBLocation.x = center.x + (int)(Math.sin(2 * Math.PI * indexB / nodes.size()) * radius) - 2 * nodeRadius / 3;
						nodeBLocation.y = center.y - (int)(Math.cos(2 * Math.PI * indexB / nodes.size()) * radius) - nodeRadius / 3;
						UnorderedPair pair = new UnorderedPair(nodeA, nodeB);
						if (cycle.containsKey(pair)) GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(nodeALocation, nodeBLocation, Color.GREEN, priority);
					}
					indexB++;
				}
				indexA++;
			}
		}
	}

	public void SetCycle(Map<UnorderedPair, Integer> newCycle){
		cycle = newCycle;
	}
	
	public void SetCycleFirstNode(String newFirstNode){
		firstNode = newFirstNode;
	}

	public String GetSelectedNode() {
		return selectedNode;
	}

	public void ResetCycle() {
		cycle = new HashMap<UnorderedPair, Integer>();
		firstNode = "";
	}

	public int GetCycleCost(){
		int output = 0;
		for (Map.Entry<UnorderedPair, Integer> entry : cycle.entrySet()) output += entry.getValue();
		return output;
	}

	public Set<String> GetNodes(){
		return nodes;
	}
	
	public Map<UnorderedPair, Integer> GetDistances() {
		return distances;
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
	private Map<UnorderedPair, Integer> cycle;
	private Point center;
	private float radius;
	private String selectedNode = "";
	private String firstNode = "";
}
