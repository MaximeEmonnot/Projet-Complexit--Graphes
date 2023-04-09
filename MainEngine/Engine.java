package MainEngine;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import AudioEngine.AudioManager;
import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Graph.Algorithms;
import MainEngine.Graph.TSPGraph;
import MainEngine.Graph.UnorderedPair;
import UIEngine.*;

/*
 * Coeur de l'application
 * Une seule scène affichant un graphe et les différentes actions possible pour tester les algorithmes sur les graphes
 */
public class Engine {

    private Engine() throws Exception {
        graph = new TSPGraph("Assets/Graphs/test.graphe", new Point(290, 265), 200);

        buttonRandom = new UIButton(new Rectangle(540, 50, 225, 50), "Random Cycle",
                () -> RunRandomCycle(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonNearest = new UIButton(new Rectangle(540, 100, 225, 50), "Nearest Neighbor",
                () -> RunNearestNeighbor(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonLinKernighan = new UIButton(new Rectangle(540, 150, 225, 50), "Lin Kernighan",
                () -> RunLinKernighan(false), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        buttonReset = new UIButton(new Rectangle(540, 200, 225, 50), "Reset",
                () -> ResetAll(), Color.WHITE, Color.LIGHT_GRAY, Color.DARK_GRAY);

        Point selectMenuPosition = new Point(540, 500);
        TextBoxSelectionGraphsTitle = new UITextBox(new Rectangle(selectMenuPosition.x, selectMenuPosition.y, 225, 50),
                "Select Graph");
        selectionMenuGraphs = new UISelectionMenu(
                new Rectangle(selectMenuPosition.x, selectMenuPosition.y + 50, 225, 170));

        Point selectStartMenuPosition = new Point(780, 50);
        TextBoxSelectionStartNodeTitle = new UITextBox(
                new Rectangle(selectStartMenuPosition.x, selectStartMenuPosition.y, 225, 50), "Select Start Node : Random");

        selectionMenuStartNode = new UISelectionMenu(
                new Rectangle(selectStartMenuPosition.x, selectStartMenuPosition.y + 50, 225, 600));

        testAlgorithmParameter = new UIInputBox(new Rectangle(15, 500, 250, 60), "Number of test");
        testAlgorithmParameter.SetNewAuthorizedChar("0123456789");
        testAlgorithmParameter.SetNewMaximalSize(16);
        testAlgorithmSelector = new UISelectionMenu(new Rectangle(265, 500, 250, 60));
        LinkedHashMap<String, UILambda> items = new LinkedHashMap<String, UILambda>();
        items.put("Random Cycle", () -> RunRandomCycle(true));
        items.put("Nearest Neighbor", () -> RunNearestNeighbor(true));
        items.put("Lin Kernighan", () -> RunLinKernighan(true));
        testAlgorithmSelector.UpdateSelections(items);
        testAlgorithmSelector.SetItemHeight(60);
        testAlgorithmSelector.SetScrollBarSize(5);
        saveTest = new UIButton(new Rectangle(414, 649, 100, 50), "Save", () -> SaveTestResults(), Color.WHITE,
                Color.LIGHT_GRAY, Color.DARK_GRAY);
    }

    public static Engine GetInstance() throws Exception {
        if (instance == null)
            instance = new Engine();
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
        selectionMenuStartNode.Update();
        testAlgorithmParameter.Update();
        testAlgorithmSelector.Update();

        if (selectedAlgorithm != null && remainingIterations > 0) {
            selectedAlgorithm.func();
            remainingIterations--;
            if (remainingIterations == 0) AudioManager.GetInstance().PlaySound("Assets/Sounds/success.wav");
        }
        if (remainingIterations == 0 && selectedAlgorithm != null)
            saveTest.Update();

        RefreshSelectionMenuGraphs();
        RefreshSelectionMenuStartNode();

    }

    private void Draw() {
        GraphicsSystem.GetInstance().SetBackgroundColor(Color.DARK_GRAY);

        buttonRandom.Draw(10);
        buttonNearest.Draw(10);
        buttonLinKernighan.Draw(10);
        buttonReset.Draw(10);
        TextBoxSelectionGraphsTitle.Draw(10);
        TextBoxSelectionStartNodeTitle.Draw(10);
        selectionMenuGraphs.Draw(10);
        selectionMenuStartNode.Draw(10);
        testAlgorithmParameter.Draw(10);
        testAlgorithmSelector.Draw(10);
        graph.Draw();

        // Affichage résultat simple
        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(540, 315, 225, 135), Color.BLACK, true, 2);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(541, 316, 223, 133), Color.WHITE, true, 3);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
                "Graph size : " + Integer.toString(graph.GetNodes().size()), new Point(558, 350), Color.RED, 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText("Show costs : " + (graph.IsShowingCost() ? "On" : "Off"),
                new Point(558, 375), (graph.IsShowingCost() ? Color.GREEN : Color.RED), 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
                "Duration : " + Float.toString(algorithmTime * 1000) + " ms",
                new Point(558, 400), Color.RED, 5);
        GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
                "Cycle length : " + Integer.toString(graph.GetCycleCost()), new Point(558, 425),
                Color.RED, 5);

        // Affichage résultat test
        if (remainingIterations == 0 && selectedAlgorithm != null) {
            GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(15, 560, 500, 140), Color.BLACK, true,
                    4);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawRect(new Rectangle(16, 561, 498, 138), Color.WHITE, true,
                    5);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText("TEST RESULT - " + selectedAlgorithmName,
                    new Point(150, 580), Color.RED, 10);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
                    "Mean time : " + Float.toString(sumAlgorithmTime * 1000 / totalIterations) + " ms",
                    new Point(40, 625), Color.RED,
                    10);
            GraphicsEngine.GraphicsSystem.GetInstance().DrawText(
                    "Mean length : " + Float.toString(sumAlgorithmResult / totalIterations), new Point(40, 650),
                    Color.RED, 10);
            saveTest.Draw(10);
        }
    }

    private void BeginLoop() {
        Timer.GetInstance().Update();
    }

    private void EndLoop() {
        Mouse.GetInstance().Pop();
        Keyboard.GetInstance().Pop();

        GraphicsSystem.GetInstance().Render();
    }

    private void RefreshSelectionMenuGraphs() throws Exception {
        LinkedHashMap<String, UILambda> items = new LinkedHashMap<String, UILambda>();

        final File folder = new File("./Assets/Graphs");
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isFile()) {
                items.put(fileEntry.getName(), () -> {
                    if (new File(fileEntry.getPath()).exists()){
                        graph = new TSPGraph(fileEntry.getPath(), new Point(290, 265), 200);
                        TextBoxSelectionStartNodeTitle.SetText("Select Start Node : Random");
                    }
                });
            }
        }

        selectionMenuGraphs.UpdateSelections(items);
    }

    private void RefreshSelectionMenuStartNode() throws Exception {
        LinkedHashMap<String, UILambda> items = new LinkedHashMap<String, UILambda>();

        for (String entry : graph.GetNodes()) {
            items.put(entry, () -> {
                graph.SetSelectedNode(entry);
                TextBoxSelectionStartNodeTitle.SetText("Select Start Node : " + entry);
            });
        }

        selectionMenuStartNode.UpdateSelections(items);
    }

    private void RunRandomCycle(boolean bIsTesting) throws Exception {
        if (bIsTesting) {
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()) {
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
        } else {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.RandomCycle(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
            AudioManager.GetInstance().PlaySound("Assets/Sounds/success.wav");
        }
    }

    private void RunNearestNeighbor(boolean bIsTesting) throws Exception {
        if (bIsTesting) {
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()) {
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
        } else {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.NearestNeighbour(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
            AudioManager.GetInstance().PlaySound("Assets/Sounds/success.wav");
        }
    }

    private void RunLinKernighan(boolean bIsTesting) throws Exception {
        if (bIsTesting) {
            String parameter = testAlgorithmParameter.GetText();
            if (!parameter.isEmpty()) {
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
        } else {
            Timer.GetInstance().Update();
            Map.Entry<String, Map<UnorderedPair, Integer>> result = Algorithms.LinKernighanHeuristic(graph);
            graph.SetCycle(result.getValue());
            graph.SetCycleFirstNode(result.getKey());
            Timer.GetInstance().Update();
            algorithmTime = Timer.GetInstance().DeltaTime();
            AudioManager.GetInstance().PlaySound("Assets/Sounds/success.wav");
        }
    }

    private void ResetAll() {
        graph.ResetCycle();
        algorithmTime = 0.f;
        remainingIterations = 0;
        totalIterations = 0;
        selectedAlgorithm = null;
        selectedAlgorithmName = "";
        sumAlgorithmResult = 0.f;
        sumAlgorithmTime = 0.f;
        TextBoxSelectionStartNodeTitle.SetText("Select Start Node : Random");
    }

    private void SaveTestResults() throws Exception {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd_MM_yyyy HH_mm_ss");
        LocalDateTime now = LocalDateTime.now();

        FileWriter output = new FileWriter("Assets/Out/" + dtf.format(now) + ".txt");

        String result = "=====================================================\n";
        result += "\nRESULTS FOR " + graph.getName() + "\n\n";
        result += "Graph size : " + Integer.toString(graph.GetNodes().size()) + "\n";
        result += "Algorithm : " + selectedAlgorithmName + "\n";
        result += "Iterations : " + Integer.toString(totalIterations) + "\n";
        result += "Mean time : " + Float.toString(sumAlgorithmTime * 1000 / totalIterations) + " ms\n";
        result += "Mean length : " + Float.toString(sumAlgorithmResult / totalIterations) + "\n\n";
        result += "=====================================================\n";

        output.append(result);
        output.close();
    }

    private static Engine instance = null;

    TSPGraph graph;
    UIButton buttonRandom;
    UIButton buttonNearest;
    UIButton buttonLinKernighan;
    UIButton buttonReset;
    UITextBox TextBoxSelectionGraphsTitle;
    UITextBox TextBoxSelectionStartNodeTitle;
    UISelectionMenu selectionMenuGraphs;
    UISelectionMenu selectionMenuStartNode;

    float algorithmTime = 0.f;

    UIInputBox testAlgorithmParameter;
    UISelectionMenu testAlgorithmSelector;
    UIButton saveTest;

    UILambda selectedAlgorithm = null;
    String selectedAlgorithmName = "";
    int remainingIterations = 0;
    int totalIterations = 0;
    float sumAlgorithmTime = 0.f;
    float sumAlgorithmResult = 0.f;

}
