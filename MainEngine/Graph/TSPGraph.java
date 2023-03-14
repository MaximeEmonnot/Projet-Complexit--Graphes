package MainEngine.Graph;


import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.awt.*;
import java.awt.event.KeyEvent;

public class TSPGraph {

    public TSPGraph(String path, Point _center, float _radius) throws Exception{
    	
    	distances = new HashMap<UnorderedPair,Integer>();
		points = new HashMap<String, Rectangle>();
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

		// Setup des points
		int index = 0;
		for (Map.Entry<String, Rectangle> entry : points.entrySet()){
			int nodeRadius = (int)(radius * (1.f - points.size() * 0.01f) / 5.f) ;
			Rectangle nodeLocation = new Rectangle();
			nodeLocation.x = center.x + (int)(Math.sin(2 * Math.PI * index / points.size()) * radius) - nodeRadius;
			nodeLocation.y = center.y - (int)(Math.cos(2 * Math.PI * index / points.size()) * radius) - nodeRadius;
			nodeLocation.width = nodeRadius;
			nodeLocation.height = nodeRadius;
			entry.setValue(nodeLocation);
			index++;
		}
    }
    
    private void addArc(String a, String b, int cost) {
    	distances.put(new UnorderedPair(a, b), cost);

		points.put(a, new Rectangle());
		points.put(b, new Rectangle());
    }
    
    public Integer getCost(String a, String b) {
    	return distances.get(new UnorderedPair(a, b));
    }

	public void Update() {
		// Hover
		hoveredNode = "";
		Point mousePos = CoreEngine.Mouse.GetInstance().GetMousePos();
		for (Map.Entry<String, Rectangle> entry : points.entrySet()){
			if (entry.getValue().contains(mousePos)){
				if (CoreEngine.Mouse.GetInstance().Read() == CoreEngine.Mouse.EEventType.LRelease){
					if (selectedNode.equals(entry.getKey())) selectedNode = "";
					else selectedNode = entry.getKey();
				}
				hoveredNode = entry.getKey();
				break;
			}
		}
		// Toggle affichage node
		CoreEngine.Keyboard.Event event = CoreEngine.Keyboard.GetInstance().ReadKey();
		if (event.keycode == KeyEvent.VK_CONTROL && event.event == CoreEngine.Keyboard.Event.EKeyEvent.Released) bIsShowingCost = !bIsShowingCost;
	}

	// Affichage des nodes : Principe des racines n-ième de l'unité
	public void Draw(){
		Draw(0);
	}
	public void Draw(int priority){

		for (Map.Entry<String, Rectangle> entryA : points.entrySet()){
			Rectangle nodeLocation = entryA.getValue();
			String node = entryA.getKey();
			Color nodeColor = Color.WHITE;
			if (node.equals(firstNode)) nodeColor = Color.GREEN;
			if (node.equals(selectedNode)) nodeColor = Color.BLUE;
			if (node.equals(hoveredNode)) nodeColor = nodeColor.darker().darker();
			// On affiche le contour
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(new Rectangle(nodeLocation.getLocation(), new Dimension(nodeLocation.width + 2, nodeLocation.height + 2)),
																	 nodeLocation.getSize(), Color.BLACK, true, priority + 1);
			// Puis l'intérieur
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(nodeLocation, nodeLocation.getSize(), nodeColor, true, priority + 2);
			
			// Puis on affiche le label du noeud
			Point textPosition = new Point((int)(nodeLocation.x + nodeLocation.width / 3), (int)(nodeLocation.y + nodeLocation.height / 1.5));
			GraphicsEngine.GraphicsSystem.GetInstance().DrawText(node, textPosition, Color.BLACK, priority + 3);

			// Affichage des arcs
			// Un noeud est survolé, alors on affiche seulement les arcs qui lui sont reliés,avec leur coût
			// Si un cycle est présent, alors on affiche seulement les arcs du cyle
			// Sinon, on affiche tous les arcs
			if (hoveredNode.isEmpty()){
				for (Map.Entry<String, Rectangle> entryB : points.entrySet()){
					UnorderedPair pair = new UnorderedPair(entryA.getKey(), entryB.getKey());
					if (!entryA.getKey().equals(entryB.getKey()) && (cycle.containsKey(pair) || cycle.isEmpty())){
						Point pointA = textPosition;
						Point pointB = new Point((int)(entryB.getValue().getLocation().x + entryB.getValue().width / 3), (int)(entryB.getValue().getLocation().y + entryB.getValue().height / 1.5));;
						Color arcColor = (cycle.isEmpty()) ? Color.BLUE : Color.GREEN;
						GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, arcColor, priority);
						if (bIsShowingCost){
							Point middle = new Point((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
							middle.translate(5, 5);
							GraphicsEngine.GraphicsSystem.GetInstance().DrawText(Integer.toString(distances.get(pair)), middle, Color.ORANGE, priority + 5);
						}
					}
				}
			}
			else if (!hoveredNode.equals(entryA.getKey())){
				UnorderedPair pair = new UnorderedPair(hoveredNode, entryA.getKey());
				if (cycle.containsKey(pair) || cycle.isEmpty()){
					Point pointA = new Point((int)(points.get(hoveredNode).getLocation().x + points.get(hoveredNode).width / 3), (int)(points.get(hoveredNode).getLocation().y + points.get(hoveredNode).height / 1.5));
					Point pointB = textPosition;
					Color arcColor = (cycle.isEmpty()) ? Color.BLUE : Color.GREEN;
					GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, arcColor, priority);
					// On récupère le milieu entre les deux points
					Point middle = new Point((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
					middle.translate(5, 5);
					GraphicsEngine.GraphicsSystem.GetInstance().DrawText(Integer.toString(distances.get(new UnorderedPair(hoveredNode, entryA.getKey()))), middle, Color.ORANGE, priority + 5);
				}
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
		return points.keySet();
	}
	
	public Map<UnorderedPair, Integer> GetDistances() {
		return distances;
	}

	public boolean IsShowingCost() {
		return bIsShowingCost;
	}
    
    // Verifie si tous les couples de noeuds sont reli�s et si les couts sont superieurs ou egaux a 0 
    private boolean isTSPValid() {
    	Set<String> unvisited = new HashSet<String>(points.keySet());
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
	private Map<String, Rectangle> points;
	private Map<UnorderedPair, Integer> cycle;
	private Point center;
	private float radius;
	private String selectedNode = "";
	private String hoveredNode = "";
	private String firstNode = "";
	private boolean bIsShowingCost = false;
}
