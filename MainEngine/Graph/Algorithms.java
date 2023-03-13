package MainEngine.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Iterator;

public class Algorithms {

    public static Map.Entry<String, Map<UnorderedPair, Integer>> RandomCycle(TSPGraph graph){
        // On créé une liste de noeuds
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

		List<String> unvisitedNodes = new ArrayList<String>();
		for (String node : graph.GetNodes()) unvisitedNodes.add(node);

		// On prend ensuite un noeud aléatoire : 
		String current = unvisitedNodes.get((int)(Math.random() * unvisitedNodes.size()));
        String firstNode = current;
		unvisitedNodes.remove(current);
		// Et on génère le cycle aléatoire
		while (!unvisitedNodes.isEmpty()){
			String next = unvisitedNodes.get((int)(Math.random() * unvisitedNodes.size()));
			unvisitedNodes.remove(next);
			output.put(new UnorderedPair(current, next), graph.GetDistances().get(new UnorderedPair(current, next)));
			current = next;
		}

		// On ajoute à présent le dernier arc pour fermer le cycle : 
		output.put(new UnorderedPair(current, firstNode), graph.GetDistances().get(new UnorderedPair(current, firstNode)));

        return Map.entry(firstNode, output);
    }

    public static Map.Entry<String, Map<UnorderedPair, Integer>> NearestNeighbour(TSPGraph graph){
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();
    
        Map<UnorderedPair, Integer> distances = graph.GetDistances();
        Set<String> nodes = graph.GetNodes();
    
        // Choisir un point de départ aléatoire
        String currentNode = getRandomElement(nodes);
    
        String firstNode = currentNode;
        Set<String> unvisitedNodes = new HashSet<String>(nodes);
        unvisitedNodes.remove(currentNode);
    
        while (unvisitedNodes.size() > 0) {
            // Calcule du point le plus proche du point courant
            String nearestNeighbour = null;
            int nearestDistance = Integer.MAX_VALUE;
            for (String other : unvisitedNodes) {
                UnorderedPair pair = new UnorderedPair(currentNode, other);
                int distance = distances.get(pair);
                if (distance < nearestDistance) {
                    nearestDistance = distance;
                    nearestNeighbour = other;
                }
            }
            if (nearestNeighbour == null) {
                System.out.println("Erreur : le voisin le plus proche du point" + currentNode + " n'a pas été trouvé.");
                return null;
            }
    
            System.out.println("Current node: " + currentNode);
            System.out.println("Nearest neighbour: " + nearestNeighbour);
            UnorderedPair firstArc = new UnorderedPair(currentNode, nearestNeighbour);
            output.put(firstArc, distances.get(firstArc));
            currentNode = nearestNeighbour;
            unvisitedNodes.remove(currentNode);
        }
    
        // Ajout du dernier arc pour revenir au point de départ
        UnorderedPair lastArc = new UnorderedPair(currentNode, firstNode);
        output.put(lastArc, distances.get(lastArc));
    
        for (Map.Entry<UnorderedPair, Integer> entry : output.entrySet()) {
            UnorderedPair pair = entry.getKey();
            int distance = entry.getValue();
            System.out.println("Arc: " + pair.getLeft() + " - " + pair.getRight() + ", Distance: " + distance);
        }
    
        return Map.entry(firstNode, output);
    }
    
    private static <T> T getRandomElement(Set<T> set) {
        int index = (int) (Math.random() * set.size());
        Iterator<T> iter = set.iterator();
        for (int i = 0; i < index; i++) {
            iter.next();
        }
        return iter.next();
    }
    

    public static Map.Entry<String, Map<UnorderedPair, Integer>> LinKernighanHeuristic(TSPGraph graph){
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

        // TODO

        return Map.entry("", output);
    }
}
