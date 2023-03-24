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

	public TSPGraph(String path, Point _center, float _radius) throws Exception {

		name = path.substring(path.lastIndexOf("/") + 1);
		distances = new HashMap<UnorderedPair, Integer>();
		points = new HashMap<String, Rectangle>();
		cycle = new HashMap<UnorderedPair, Integer>();
		center = _center;
		radius = _radius;

		if (path.endsWith(".graphe"))
			ReadGrapheFile(path);
		else if (path.endsWith(".tsp"))
			ReadTSPFile(path);

		// Setup des points
		int index = 0;
		for (Map.Entry<String, Rectangle> entry : points.entrySet()) {
			int nodeRadius = (points.size() <= 100) ? (int) (radius * (1.f - points.size() * 0.01f) / 5.f) : 0;
			Rectangle nodeLocation = new Rectangle();
			nodeLocation.x = center.x + (int) (Math.sin(2 * Math.PI * index / points.size()) * radius) - nodeRadius;
			nodeLocation.y = center.y - (int) (Math.cos(2 * Math.PI * index / points.size()) * radius) - nodeRadius;
			nodeLocation.width = nodeRadius;
			nodeLocation.height = nodeRadius;
			entry.setValue(nodeLocation);
			index++;
		}
	}

	private void ReadGrapheFile(String path) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(path));
		for (String l : lines) {
			String[] tmp = l.split(" ");
			addArc(tmp[0], tmp[1], Integer.parseInt(tmp[2]));
		}
		if (!isTSPValid())
			throw new Exception("It is not a valid TSP graph");
	}

	private void ReadTSPFile(String path) throws Exception {
		List<String> lines = Files.readAllLines(Paths.get(path));
		int lineIndex = 0;
		// Lecture des headers
		String type = "";
		String edgeWeightType = "";
		while (!lines.get(lineIndex).contains("_SECTION")) {
			String[] header = lines.get(lineIndex).split(":");
			switch (header[0].replace(" ", "")) {
				case "TYPE":
					type = header[1];
					break;
				case "EDGE_WEIGHT_TYPE":
					edgeWeightType = header[1];
					break;
				default:
					break;
			}
			lineIndex++;
		}

		// Vérification du bon type de problème (TSP)
		if (!type.contains("TSP"))
			throw new Exception("It is not a valid TSP graph");

		// Vérification du bon type de données (Tout sauf EXPLICIT)
		if (edgeWeightType.contains("EXPLICIT"))
			throw new Exception("This edge type is not supported yet");

		// Lecture de l'ensemble des points
		Map<String, Map.Entry<Double, Double>> data = new HashMap<String, Map.Entry<Double, Double>>(); // Nom du point -
																																																		// Position X / LAT
																																																		// - Position Y /
																																																		// LONG
		lineIndex++;

		while (!lines.get(lineIndex).contains("EOF")) {

			String[] point = lines.get(lineIndex).split(" ");

			String number = "";
			String coordX = "";
			String coordY = "";

			for (String info : point) {
				if (!info.isEmpty()) {
					if (number.isEmpty())
						number = info;
					else if (coordX.isEmpty())
						coordX = info;
					else if (coordY.isEmpty())
						coordY = info;
				}
			}

			data.put(number, Map.entry(Double.parseDouble(coordX), Double.parseDouble(coordY)));
			lineIndex++;
		}

		// Initialisation du graphe
		for (Map.Entry<String, Map.Entry<Double, Double>> entryA : data.entrySet()) {
			for (Map.Entry<String, Map.Entry<Double, Double>> entryB : data.entrySet()) {
				if (!entryA.getKey().equals(entryB.getKey())) {
					switch (edgeWeightType.replace(" ", "")) {
						case "EUC_2D": {
							double xDist = entryA.getValue().getKey() - entryB.getValue().getKey();
							double yDist = entryA.getValue().getValue() - entryB.getValue().getValue();
							int distance = (int) (Math.round(Math.sqrt(xDist * xDist + yDist * yDist)));
							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						case "MAX_2D": {
							double xDist = Math.abs(entryA.getValue().getKey() - entryB.getValue().getKey());
							double yDist = Math.abs(entryA.getValue().getValue() - entryB.getValue().getValue());
							int distance = Math.max((int) Math.round(xDist), (int) Math.round(yDist));
							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						case "MAN_2D": {
							double xDist = Math.abs(entryA.getValue().getKey() - entryB.getValue().getKey());
							double yDist = Math.abs(entryA.getValue().getValue() - entryB.getValue().getValue());
							int distance = (int) Math.round(xDist + yDist);
							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						case "CEIL_2D": {
							double xDist = entryA.getValue().getKey() - entryB.getValue().getKey();
							double yDist = entryA.getValue().getValue() - entryB.getValue().getValue();
							int distance = (int) (Math.ceil(Math.sqrt(xDist * xDist + yDist * yDist)));
							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						case "GEO": {
							// Latitude Longitude A
							double degreeLatitudeA = Math.round(entryA.getValue().getKey());
							double minuteLatitudeA = entryA.getValue().getKey() - degreeLatitudeA;
							double latitudeA = Math.PI * (degreeLatitudeA + 5.0 * minuteLatitudeA / 3.0) / 180.0;
							double degreeLongitudeA = Math.round(entryA.getValue().getValue());
							double minuteLongitudeA = entryA.getValue().getValue() - degreeLongitudeA;
							double longitudeA = Math.PI * (degreeLongitudeA + 5.0 * minuteLongitudeA / 3.0) / 180.0;

							// Latitude Longitude B
							double degreeLatitudeB = Math.round(entryB.getValue().getKey());
							double minuteLatitudeB = entryB.getValue().getKey() - degreeLatitudeB;
							double latitudeB = Math.PI * (degreeLatitudeB + 5.0 * minuteLatitudeB / 3.0) / 180.0;
							double degreeLongitudeB = Math.round(entryB.getValue().getValue());
							double minuteLongitudeB = entryB.getValue().getValue() - degreeLongitudeB;
							double longitudeB = Math.PI * (degreeLongitudeB + 5.0 * minuteLongitudeB / 3.0) / 180.0;

							// Distance
							double RRR = 6378.388;
							double q1 = Math.cos(longitudeA - longitudeB);
							double q2 = Math.cos(latitudeA - latitudeB);
							double q3 = Math.cos(latitudeA + latitudeB);
							int distance = (int) (RRR * Math.acos(0.5 * ((1.0 + q1) * q2 - (1.0 - q1) * q3)) + 1.0);

							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						case "ATT": {
							double xDist = entryA.getValue().getKey() - entryB.getValue().getKey();
							double yDist = entryA.getValue().getValue() - entryB.getValue().getValue();
							double rij = Math.sqrt((xDist * xDist + yDist * yDist) / 10.0);
							double tij = (double) Math.round(rij);
							int distance = (int) tij + ((tij < rij) ? 0 : 1);
							addArc(entryA.getKey(), entryB.getKey(), distance);
						}
							break;
						default:
							break;
					}
				}
			}
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
		for (Map.Entry<String, Rectangle> entry : points.entrySet()) {
			if (entry.getValue().contains(mousePos)) {
				if (CoreEngine.Mouse.GetInstance().Read() == CoreEngine.Mouse.EEventType.LRelease) {
					if (selectedNode.equals(entry.getKey()))
						selectedNode = "";
					else
						selectedNode = entry.getKey();
				}
				hoveredNode = entry.getKey();
				break;
			}
		}
		// Toggle affichage node
		CoreEngine.Keyboard.Event event = CoreEngine.Keyboard.GetInstance().ReadKey();
		if (event.keycode == KeyEvent.VK_CONTROL && event.event == CoreEngine.Keyboard.Event.EKeyEvent.Released)
			bIsShowingCost = !bIsShowingCost;
	}

	// Affichage des nodes : Principe des racines n-ième de l'unité
	public void Draw() {
		Draw(0);
	}

	public void Draw(int priority) {

		for (Map.Entry<String, Rectangle> entryA : points.entrySet()) {
			Rectangle nodeLocation = entryA.getValue();
			String node = entryA.getKey();
			Color nodeColor = Color.WHITE;
			if (node.equals(firstNode))
				nodeColor = Color.GREEN;
			if (node.equals(selectedNode))
				nodeColor = Color.BLUE;
			if (node.equals(hoveredNode))
				nodeColor = nodeColor.darker().darker();
			// On affiche le contour
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(
					new Rectangle(nodeLocation.getLocation(), new Dimension(nodeLocation.width + 2, nodeLocation.height + 2)),
					nodeLocation.getSize(), Color.BLACK, true, priority + 1);
			// Puis l'intérieur
			GraphicsEngine.GraphicsSystem.GetInstance().DrawRoundRect(nodeLocation, nodeLocation.getSize(), nodeColor, true,
					priority + 2);

			// Puis on affiche le label du noeud (si le nombre total de noeud est inférieur
			// à 50)
			Point textPosition = new Point((int) (nodeLocation.x + nodeLocation.width / 3),
					(int) (nodeLocation.y + nodeLocation.height / 1.5));
			if (points.size() < 50) {
				GraphicsEngine.GraphicsSystem.GetInstance().DrawText(node, textPosition, Color.BLACK, priority + 3);
			}

			// Affichage des arcs
			// Un noeud est survolé, alors on affiche seulement les arcs qui lui sont
			// reliés,avec leur coût
			// Si un cycle est présent, alors on affiche seulement les arcs du cyle
			// Sinon, on affiche tous les arcs
			if (hoveredNode.isEmpty()) {
				for (Map.Entry<String, Rectangle> entryB : points.entrySet()) {
					UnorderedPair pair = new UnorderedPair(entryA.getKey(), entryB.getKey());
					if (!entryA.getKey().equals(entryB.getKey()) && (cycle.containsKey(pair) || cycle.isEmpty())) {
						Point pointA = textPosition;
						Point pointB = new Point((int) (entryB.getValue().getLocation().x + entryB.getValue().width / 3),
								(int) (entryB.getValue().getLocation().y + entryB.getValue().height / 1.5));
						;
						Color arcColor = (cycle.isEmpty()) ? Color.BLUE : Color.GREEN;
						GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, arcColor, priority);
						if (bIsShowingCost) {
							Point middle = new Point((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
							middle.translate(5, 5);
							GraphicsEngine.GraphicsSystem.GetInstance().DrawText(Integer.toString(distances.get(pair)), middle,
									Color.ORANGE, priority + 5);
						}
					}
				}
			} else if (!hoveredNode.equals(entryA.getKey())) {
				UnorderedPair pair = new UnorderedPair(hoveredNode, entryA.getKey());
				if (cycle.containsKey(pair) || cycle.isEmpty()) {
					Point pointA = new Point((int) (points.get(hoveredNode).getLocation().x + points.get(hoveredNode).width / 3),
							(int) (points.get(hoveredNode).getLocation().y + points.get(hoveredNode).height / 1.5));
					Point pointB = textPosition;
					Color arcColor = (cycle.isEmpty()) ? Color.BLUE : Color.GREEN;
					GraphicsEngine.GraphicsSystem.GetInstance().DrawLine(pointA, pointB, arcColor, priority);
					// On récupère le milieu entre les deux points
					Point middle = new Point((pointA.x + pointB.x) / 2, (pointA.y + pointB.y) / 2);
					middle.translate(5, 5);
					GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
							Integer.toString(distances.get(new UnorderedPair(hoveredNode, entryA.getKey()))), middle, Color.ORANGE,
							priority + 5);
				}
			}
		}
	}

	public void SetCycle(Map<UnorderedPair, Integer> newCycle) {
		cycle = newCycle;
	}

	public void SetCycleFirstNode(String newFirstNode) {
		firstNode = newFirstNode;
	}

	public String GetSelectedNode() {
		return selectedNode;
	}

	public void ResetCycle() {
		cycle = new HashMap<UnorderedPair, Integer>();
		firstNode = "";
		selectedNode = "";
	}

	public int GetCycleCost() {
		int output = 0;
		for (Map.Entry<UnorderedPair, Integer> entry : cycle.entrySet())
			output += entry.getValue();
		return output;
	}

	public Set<String> GetNodes() {
		return points.keySet();
	}

	public Map<UnorderedPair, Integer> GetDistances() {
		return distances;
	}

	public boolean IsShowingCost() {
		return bIsShowingCost;
	}

	// Verifie si tous les couples de noeuds sont reli�s et si les couts sont
	// superieurs ou egaux a 0
	private boolean isTSPValid() {
		Set<String> unvisited = new HashSet<String>(points.keySet());
		while (unvisited.size() != 0) {
			String node = (String) unvisited.toArray()[0];
			unvisited.remove(node);
			for (String other : unvisited) {
				UnorderedPair pair = new UnorderedPair(node, other);
				if (!distances.containsKey(pair) || distances.get(pair) < 0)
					return false;
			}
		}
		return true;
	}

	public String getName() {
		return name;
	}

	private Map<UnorderedPair, Integer> distances;
	private Map<String, Rectangle> points;
	private Map<UnorderedPair, Integer> cycle;
	private Point center;
	private float radius;
	private String selectedNode = "";
	private String hoveredNode = "";
	private String firstNode = "";
	private boolean bIsShowingCost = false;
	private String name = "";
}
