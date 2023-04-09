package MainEngine.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import java.util.Iterator;

/*
 * Les différents algorithmes utilisés, trois sont présents
 * RandomCycle pour créer un cycle aléatoire (pour tester le dispositif)
 * NearestNeighbour pour l'algorithme du même nom (Approximation)
 * LinKernighan pour l'heuristique de Lin Kernighan
 */
public class Algorithms {

    public static Map.Entry<String, Map<UnorderedPair, Integer>> RandomCycle(TSPGraph graph){
        // On créé une liste de noeuds
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

		List<String> unvisitedNodes = new ArrayList<String>();
		for (String node : graph.GetNodes()) unvisitedNodes.add(node);

		// On prend ensuite un noeud aléatoire (si aucun noeud n'est sélectionné, sinon on prend le noeud sélectionné)
        String current = graph.GetSelectedNode();
        if (current.isEmpty()) current = unvisitedNodes.get((int)(Math.random() * unvisitedNodes.size()));
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
    
        // Choisir un point de départ aléatoire (si aucun noeud sélectionné, sinon on démarre à partir du noeud sélectionné)
        String currentNode = graph.GetSelectedNode();
        if (currentNode.isEmpty()) currentNode = getRandomElement(nodes);
    
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
            UnorderedPair firstArc = new UnorderedPair(currentNode, nearestNeighbour);
            output.put(firstArc, distances.get(firstArc));
            currentNode = nearestNeighbour;
            unvisitedNodes.remove(currentNode);
        }
    
        // Ajout du dernier arc pour revenir au point de départ
        UnorderedPair lastArc = new UnorderedPair(currentNode, firstNode);
        output.put(lastArc, distances.get(lastArc));
    
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
    	
    	// LinKernighanHeuristic 2-opt partant d'un cycle aleatoire

        // Recuperation des valeurs utiles
        Map<UnorderedPair, Integer> distances = graph.GetDistances();
        Map.Entry<String, Map<UnorderedPair, Integer>> randomCycle =  RandomCycle(graph);
        String selectedNode = randomCycle.getKey();
        Set<UnorderedPair> cycle = randomCycle.getValue().keySet();
        
        
        // Initialisation du cycle
        String current = selectedNode;
        List<String> nodeOrder = new ArrayList<>();
        do {
        	for(UnorderedPair arc : cycle) {
        		if(arc.getLeft().equals(current)) {
        			nodeOrder.add(current);
        			current = arc.getRight();
        			break;
        		}
        	}	
        }while(!current.equals(selectedNode));
        
        final int cycleSize = nodeOrder.size();

        
        // Boucle de recherche 2-opt
        boolean improvement = true;
        while (improvement) {
            improvement = false;

            // Parcourir les paires d'arcs
            for (int i = 0; i < cycleSize; i++) {
                for (int j = i + 1; j < cycleSize; j++) {
                    if (j - i == 1 || (i == 0 && j == cycleSize - 1)) {
                        continue; // si paire d'arc consecutif, pas la peine de considérer
                    }

                    String node1 = nodeOrder.get(i);
                    String node2 = nodeOrder.get(j);
                    String node1Next = nodeOrder.get((i + 1) % cycleSize);
                    String node2Next = nodeOrder.get((j + 1) % cycleSize);

                    int gain = distances.get(new UnorderedPair(node1, node1Next)) +
                            distances.get(new UnorderedPair(node2, node2Next)) -
                            distances.get(new UnorderedPair(node1, node2)) -
                            distances.get(new UnorderedPair(node1Next, node2Next));

                    // Si optimisation possible
                    if (gain > 0) {
                        // effectuer la modification
                    	
                    	// On inverse tel que la paire d'arc (A, B) (C, D) devient (A, C) (B, D)
                    	nodeOrder.set((i + 1) % cycleSize, node2);
                    	nodeOrder.set(j, node1Next);
                    	
                    	
                    	// On inverse le sens de tous les arcs entre C et B
                    	final int inverseFrom = i > j ? (j + 1) % cycleSize + 1 : (i + 1) % cycleSize + 1;
                    	final int inverseTo = i > j ? i - 1 : j - 1;
                    	
                    	List<String> nodesToInverse = new ArrayList<String>(nodeOrder.subList(inverseFrom, inverseTo + 1));
                    	
                    	for(int n = inverseFrom; n <= inverseTo; n++) {
                    		nodeOrder.set(n, nodesToInverse.get(inverseTo - n));
                    	}

                        // Indique qu'une amélioration a été trouvée
                        improvement = true;
                    }
                }
            }
        }
        
        // Creation du resultat dans le format attendu
        Map<UnorderedPair, Integer> output = new HashMap<>();
        for (int l = 0; l < cycleSize; l++) {
            String node = nodeOrder.get(l);
            String nextNode = nodeOrder.get((l + 1) % cycleSize);
            output.put(new UnorderedPair(node, nextNode), distances.get(new UnorderedPair(node, nextNode)));
        }

        return Map.entry(selectedNode, output);
    }
}
