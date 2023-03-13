package MainEngine;
import java.awt.*;
import java.io.File;
import java.util.LinkedHashMap;

import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Graph.Algorithms;
import MainEngine.Graph.TSPGraph;
import UIEngine.UIButton;
import UIEngine.UISelectionMenu;
import UIEngine.UITextBox;

public class Engine {
    
    private Engine() throws Exception {
        graph = new TSPGraph("Assets/Graphs/test.graphe");
        
        buttonRandom = new UIButton(new Rectangle(620, 50, 150, 50), "Random Cycle", 
        () -> {
            Timer.GetInstance().Update();
            graph.SetCycle(Algorithms.RandomCycle(graph));
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
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
            algorithmTime = 0.f;
        }, Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        TextBoxSelectionGraphsTitle = new UITextBox(new Rectangle(580, 300, 190, 50), "Select Graph");
        selectionMenuGraphs = new UISelectionMenu(new Rectangle(580, 350, 190, 170));
        RefreshSelectionMenuGraphs();
         
        buttonRefreshMenu = new UIButton(new Rectangle(580, 520, 150, 50), "Refresh List", 
                () -> {
                	RefreshSelectionMenuGraphs();
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
        selectionMenuGraphs.Update();
        buttonRefreshMenu.Update();
    }
    private void Draw(){
        GraphicsSystem.GetInstance().SetBackgroundColor(Color.DARK_GRAY);
   
        buttonRandom.Draw(10);
        buttonNearest.Draw(10);
        buttonLinKernighan.Draw(10);
        buttonReset.Draw(10);
        selectionMenuGraphs.Draw(10);
        TextBoxSelectionGraphsTitle.Draw(10);
        buttonRefreshMenu.Draw(10);

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
    
    
    private void RefreshSelectionMenuGraphs() throws Exception{
    	LinkedHashMap<String, UIButton.Lambda> items = new LinkedHashMap<String, UIButton.Lambda>();
    	
    	final File folder = new File("./Assets/Graphs");
    	for (final File fileEntry : folder.listFiles()) {
    		String test = fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1);
    		if(fileEntry.isFile() && fileEntry.getName().substring(fileEntry.getName().lastIndexOf('.') + 1).equals("graphe")){
    			items.put(fileEntry.getName(), () -> {if(new File(fileEntry.getPath()).exists()) graph = new TSPGraph(fileEntry.getPath());});
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
    UIButton buttonRefreshMenu;

    float algorithmTime = 0.f;
}
