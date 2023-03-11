import CoreEngine.Window;
import ExceptionEngine.EngineException;
import MainEngine.Engine;
import MainEngine.Graph.TSPGraph;

public class Main
{
    public static void main(String[] args)
    {
        try{
            Window.GetInstance();
            while(true){
                long beginFrame = System.currentTimeMillis();
                Engine.GetInstance().EngineLoop();
                while(System.currentTimeMillis() - beginFrame < (1 / FPS) * 1000);
            } 
        }
        catch(Exception e){
            EngineException.ShowMessageBox(e, "Java Swing Engine Exception");
        }
    }
    private final static float FPS = 60.f;
};