package MainEngine;
import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;
import MainEngine.Levels.AddAnimalLevel;
import MainEngine.Levels.TestLevel;

public class Engine {
    
    private Engine() throws Exception {
        // Initialisation des levels
        LevelManager.GetInstance().AddLevel(new TestLevel("Test Level"));
        LevelManager.GetInstance().AddLevel(new AddAnimalLevel("add Animal Level"));
    }

    public static Engine GetInstance() throws Exception {
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
        LevelManager.GetInstance().Update();
    }
    private void Draw() throws Exception {
        LevelManager.GetInstance().Draw();
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

}
