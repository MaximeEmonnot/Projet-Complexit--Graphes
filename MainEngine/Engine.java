package MainEngine;
import java.awt.*;
import java.util.Map;

import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Graph.Algorithms;
import MainEngine.Graph.TSPGraph;
import MainEngine.Graph.UnorderedPair;
import UIEngine.UIButton;

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
    }
    private void Draw(){
        GraphicsSystem.GetInstance().SetBackgroundColor(Color.DARK_GRAY);
   
        buttonRandom.Draw(10);
        buttonNearest.Draw(10);
        buttonLinKernighan.Draw(10);
        buttonReset.Draw(10);

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

    private static Engine instance = null;

    TSPGraph graph;
    UIButton buttonRandom;
    UIButton buttonNearest;
    UIButton buttonLinKernighan;
    UIButton buttonReset;

    float algorithmTime = 0.f;
}
