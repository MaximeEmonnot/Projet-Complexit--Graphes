package MainEngine;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Graph.Algorithms;
import MainEngine.Graph.TSPGraph;
import MainEngine.Graph.UnorderedPair;
import UIEngine.*;
import UIEngine.UIButton.Lambda;

public class Engine {
    
    private Engine() throws Exception {
        graph = new TSPGraph("Assets/Graphs/test.graphe", new Point(300, 265), 200);
        
        buttonRandom = new UIButton(new Rectangle(620, 50, 150, 50), "Random Cycle", 
        () -> RunRandomCycle(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
        
        buttonNearest = new UIButton(new Rectangle(620, 100, 150, 50), "Nearest Neighbor", 
        () -> RunNearestNeighbor(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonLinKernighan = new UIButton(new Rectangle(620, 150, 150, 50), "Lin Kernighan", 
        () -> RunLinKernighan(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonReset = new UIButton(new Rectangle(620, 200, 150, 50), "Reset", 
        () -> {
            graph.ResetCycle();
            algorithmTime = 0.f;
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
        
        Point selectMenuPosition = new Point(580, 500);
        TextBoxSelectionGraphsTitle = new UITextBox(new Rectangle(selectMenuPosition.x, selectMenuPosition.y, 190, 50), "Select Graph");
        selectionMenuGraphs = new UISelectionMenu(new Rectangle(selectMenuPosition.x, selectMenuPosition.y + 50, 190, 170));
        
        testAlgorithmParameter = new UIInputBox(new Rectangle(25, 500, 250, 60), "Number of test");
        testAlgorithmParameter.SetNewAuthorizedChar("0123456789");
        testAlgorithmParameter.SetNewMaximalSize(16);
        testAlgorithmSelector = new UISelectionMenu(new Rectangle(275, 500, 250, 60));
    	LinkedHashMap<String, UIButton.Lambda> items = new LinkedHashMap<String, UIButton.Lambda>();
        items.put("Random Cycle", () -> RunRandomCycle(true));
        items.put("Nearest Neighbor", () -> RunNearestNeighbor(true));
        items.put("Lin Kernighan", () -> RunLinKernighan(true));
        testAlgorithmSelector.UpdateSelections(items);
        testAlgorithmSelector.SetItemHeight(60);
        testAlgorithmSelector.SetScrollBarSize(5);
        saveTest = new UIButton(new Rectangle(424, 649, 100, 50), "Save", () -> {}, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
    }

    public static Engine GetInstance() throws Exception{
        if (instance == null) instance = new Engine();
        return instance;
    }

    public synchronized void EngineLoop() throws Exception {
        BeginLoop();
        Update();
        Draw();
        EndLoop();
    }

    private void Update() throws Exception {
        graph.Update();

        buttonRandom.Update();
        buttonNearest.Update();    
        buttonLinKernighan.Update();
        buttonReset.Update();
        selectionMenuGraphs.Update();
        testAlgorithmParameter.Update();
        testAlgorithmSelector.Update();

        if (selectedAlgorithm != null && remainingIterations > 0){
           selectedAlgorithm.func();
           remainingIterations--;
        }
        if (remainingIterations == 0 && selectedAlgorithm != null) saveTest.Update();
        
        RefreshSelectionMenuGraphs();
    }
    private void Draw(){
        GraphicsSystem.GetInstance().SetBackgroundColor(Color.DARK_GRAY);
   
        buttonRandom.Draw(10);
        buttonNearest.Draw(10);
        buttonLinKernighan.Draw(10);
        buttonReset.Draw(10);
        TextBoxSelectionGraphsTitle.Draw(10);
        selectionMenuGraphs.Draw(10);
        testAlgorithmParameter.Draw(10);
        testAlgorithmSelector.Draw(10);
        graph.Draw();

        // Affichage résultat simple
        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(550, 315, 225, 135), Color.BLACK, true, 2);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(552, 317, 221, 131), Color.WHITE, true, 3);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Graph size : " + Integer.toString(graph.GetNodes().size()), new Point(568, 350), Color.RED, 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Show cost : " + (graph.IsShowingCost() ? "On" : "Off"), new Point(568, 375), (graph.IsShowingCost() ? Color.GREEN : Color.RED), 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Algorithm duration : " + Float.toString(algorithmTime), new Point(568, 400), Color.RED, 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Algorithm result : " + Integer.toString(graph.GetCycleCost()), new Point(568, 425), Color.RED, 5);
   
        // Affichage résultat test
        if (remainingIterations == 0 && selectedAlgorithm != null){
            GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(25, 560, 500, 140), Color.BLACK, true, 4);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(26, 561, 498, 138), Color.WHITE, true, 5);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText("TEST RESULT - " + selectedAlgorithmName, new Point(175, 580), Color.RED, 10);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Mean time : " + Float.toString(sumAlgorithmTime / totalIterations), new Point(50, 625), Color.RED, 10);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Mean result : " + Float.toString(sumAlgorithmResult / totalIterations), new Point(50, 650), Color.RED, 10);
            saveTest.Draw(10);
        }
    }

    private void BeginLoop(){
        Timer.GetInstance().Update();
    }

    private void EndLoop() {
        Mouse.GetInstance().Pop();
        Keyboard.GetInstance().Pop();

        GraphicsSystem.GetInstance().Render();
    }
    
    private void RefreshSelectionMenuGraphs() throws Exception{
    	LinkedHashMap<String, UIButton.Lambda> items = new LinkedHashMap<String, UIButton.Lambda>();
    	
    	final File folder = new File("./Assets/Graphs");
    	for (final File fileEntry : folder.listFiles()) {
    		if(fileEntry.isFile()){
    			items.put(fileEntry.getName(), () -> {if(new File(fileEntry.getPath()).exists()) graph = new TSPGraph(fileEntry.getPath(), new Point(300, 275), 200);});
    		}
    	}
    	
    	selectionMenuGraphs.UpdateSelections(items);
    }

    private void RunRandomCycle(boolean bIsTesting){
        if (bIsTesting){
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()){
                remainingIterations = Integer.parseInt(parameter);
                totalIterations = remainingIterations;
                selectedAlgorithmName = "Random Cycle";
                sumAlgorithmTime = 0.f;
                sumAlgorithmResult = 0.f;
                selectedAlgorithm = () -> {
                    Timer.GetInstance().Update();
                    Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.RandomCycle(graph);
                    graph.SetCycle(result.getValue());
                    graph.SetCycleFirstNode(result.getKey());
                    Timer.GetInstance().Update();
                    algorithmTime = Timer.GetInstance().DeltaTime();
                    sumAlgorithmTime += Timer.GetInstance().DeltaTime();
                    sumAlgorithmResult += graph.GetCycleCost();
                };
            }
        }
        else{
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.RandomCycle(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }
    }

    private void RunNearestNeighbor(boolean bIsTesting){
        if (bIsTesting){
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()){
                remainingIterations = Integer.parseInt(parameter);
                totalIterations = remainingIterations;
                selectedAlgorithmName = "Nearest Neighbor";
                sumAlgorithmTime = 0.f;
                sumAlgorithmResult = 0.f;
                selectedAlgorithm = () -> {
                    Timer.GetInstance().Update();
                    Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.NearestNeighbour(graph);
                    graph.SetCycle(result.getValue());
                    graph.SetCycleFirstNode(result.getKey());
                    Timer.GetInstance().Update();
                    algorithmTime = Timer.GetInstance().DeltaTime();
                    sumAlgorithmTime += Timer.GetInstance().DeltaTime();
                    sumAlgorithmResult += graph.GetCycleCost();
                };
            }
        }
        else{
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.NearestNeighbour(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }
    }

    private void RunLinKernighan(boolean bIsTesting){
        if (bIsTesting){
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()){
                remainingIterations = Integer.parseInt(parameter);
                totalIterations = remainingIterations;
                selectedAlgorithmName = "Lin Kernighan";
                sumAlgorithmTime = 0.f;
                sumAlgorithmResult = 0.f;
                selectedAlgorithm = () -> {
                    Timer.GetInstance().Update();
                    Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.LinKernighanHeuristic(graph);
                    graph.SetCycle(result.getValue());
                    graph.SetCycleFirstNode(result.getKey());
                    Timer.GetInstance().Update();
                    algorithmTime = Timer.GetInstance().DeltaTime();
                    sumAlgorithmTime += Timer.GetInstance().DeltaTime();
                    sumAlgorithmResult += graph.GetCycleCost();
                };
            }
        }
        else {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.LinKernighanHeuristic(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }
    }

    private static Engine instance = null;

    TSPGraph graph;
    UIButton buttonRandom;
    UIButton buttonNearest;
    UIButton buttonLinKernighan;
    UIButton buttonReset;
    UITextBox TextBoxSelectionGraphsTitle;
    UISelectionMenu selectionMenuGraphs;

    float algorithmTime = 0.f;

    UIInputBox testAlgorithmParameter;
    UISelectionMenu testAlgorithmSelector;
    UIButton saveTest;

    Lambda selectedAlgorithm = null;
    String selectedAlgorithmName = "";
    int remainingIterations = 0;
    int totalIterations = 0;
    float sumAlgorithmTime = 0.f;
    float sumAlgorithmResult = 0.f;
}
