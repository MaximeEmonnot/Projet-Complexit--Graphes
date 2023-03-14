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

public class Engine {
    
    private Engine() throws Exception {
        graph = new TSPGraph("Assets/Graphs/test.graphe", new Point(300, 275), 200);
        
        buttonRandom = new UIButton(new Rectangle(620, 50, 150, 50), "Random Cycle", 
        () -> {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.RandomCycle(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
        
        buttonNearest = new UIButton(new Rectangle(620, 100, 150, 50), "Nearest Neighbor", 
        () -> {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.NearestNeighbour(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonLinKernighan = new UIButton(new Rectangle(620, 150, 150, 50), "Lin Kernighan", 
        () -> {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.LinKernighanHeuristic(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonReset = new UIButton(new Rectangle(620, 200, 150, 50), "Reset", 
        () -> {
            graph.ResetCycle();
            algorithmTime = 0.f;
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);
        
        Point selectMenuPosition = new Point(580, 500);
        
        TextBoxSelectionGraphsTitle = new UITextBox(new Rectangle(selectMenuPosition.x, selectMenuPosition.y, 190, 50), "Select Graph");
        selectionMenuGraphs = new UISelectionMenu(new Rectangle(selectMenuPosition.x, selectMenuPosition.y + 50, 190, 170));
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

        graph.Draw();

        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(550, 370, 225, 75), Color.BLACK, true, 2);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(552, 372, 221, 71), Color.WHITE, true, 3);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Algorithm duration : " + Float.toString(algorithmTime), new Point(568, 400), Color.RED, 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Algorithm result : " + Integer.toString(graph.GetCycleCost()), new Point(568, 425), Color.RED, 5);
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
    		String test = fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1);
    		if(fileEntry.isFile() && fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1).equals("graphe")){
    			items.put(fileEntry.getName(), () -> {if(new File(fileEntry.getPath()).exists()) graph = new TSPGraph(fileEntry.getPath(), new Point(300, 275), 200);});
    		}
    	}
    	
    	selectionMenuGraphs.UpdateSelections(items);
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
}
