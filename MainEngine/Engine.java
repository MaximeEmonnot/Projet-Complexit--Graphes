package MainEngine;
import java.awt.*;

import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Graph.Algorithms;
import MainEngine.Graph.TSPGraph;
import UIEngine.UIButton;

public class Engine {
    
    private Engine() throws Exception {
        graph = new TSPGraph("Assets/Graphs/test.graphe");
        
        buttonRandom = new UIButton(new Rectangle(620, 50, 150, 50), "Random Cycle", 
        () -> {
            graph.SetCycle(Algorithms.RandomCycle(graph));
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonNearest = new UIButton(new Rectangle(620, 100, 150, 50), "Nearest Neighbor", 
        () -> {
            graph.SetCycle(Algorithms.NearestNeighbour(graph));
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonLinKernighan = new UIButton(new Rectangle(620, 150, 150, 50), "Lin Kernighan", 
        () -> {
            graph.SetCycle(Algorithms.LinKernighanHeuristic(graph));
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonReset = new UIButton(new Rectangle(620, 200, 150, 50), "Reset", 
        () -> {
            graph.ResetCycle();
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

        graph.Draw(new Point(350, 275), 200);
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
}
