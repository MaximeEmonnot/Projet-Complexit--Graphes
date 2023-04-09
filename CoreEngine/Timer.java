package CoreEngine;

/*
 * Singleton responsable de la gestion du temps
 * Calcul le temps écoulé entre chaque appel de la méthode Update()
 */
public class Timer {
    private Timer() {
        oldTime = System.currentTimeMillis();
    }

    public synchronized static Timer GetInstance() {
        if (instance == null)
            instance = new Timer();
        return instance;
    }

    public void Update() {
        long newTime = System.currentTimeMillis();
        deltaTime = (float)(newTime - oldTime) / 1000.f;
        oldTime = newTime;
    }

    public float DeltaTime() {
        return deltaTime;
    }

    private static Timer instance = null;
    private long oldTime;
    private float deltaTime = 0.f;
}
