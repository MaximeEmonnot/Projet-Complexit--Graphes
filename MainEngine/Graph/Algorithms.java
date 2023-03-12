package MainEngine.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Algorithms {

    public static Map<UnorderedPair, Integer> RandomCycle(TSPGraph graph){
        // On créé une liste de noeuds
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

		List<String> unvisitedNodes = new ArrayList<String>();
		for (String node : graph.GetNodes()) unvisitedNodes.add(node);

		// On prend ensuite un noeud aléatoire : 
		String current = unvisitedNodes.get((int)(Math.random() * unvisitedNodes.size()));
		unvisitedNodes.remove(current);
		String first = current;
		// Et on génère le cycle aléatoire
		while (!unvisitedNodes.isEmpty()){
			String next = unvisitedNodes.get((int)(Math.random() * unvisitedNodes.size()));
			unvisitedNodes.remove(next);
			output.put(new UnorderedPair(current, next), graph.GetDistances().get(new UnorderedPair(current, next)));
			current = next;
		}

		// On ajoute à présent le dernier arc pour fermer le cycle : 
		output.put(new UnorderedPair(current, first), graph.GetDistances().get(new UnorderedPair(current, first)));
        return output;
    }

    public static Map<UnorderedPair, Integer> NearestNeighbour(TSPGraph graph){
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

        // TODO

        return output;
    }
    public static Map<UnorderedPair, Integer> LinKernighanHeuristic(TSPGraph graph){
        Map<UnorderedPair, Integer> output = new HashMap<UnorderedPair, Integer>();

        // TODO

        return output;
    }
}
