package CoreEngine;

import javax.swing.JFrame;

import GraphicsEngine.GraphicsSystem;

/*
 * Singleton responsable de la gestion de la fenêtre
 * Initialisation de la JFrame et lien avec les Manager autres (Keyboard, Mouse, GraphicsSystem)
 */
public class Window extends JFrame {
    private Window() {
        setTitle("C&G Engine");
        setBounds(50, 50, width, height);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        addKeyListener(Keyboard.GetInstance());
        addMouseListener(Mouse.GetInstance());
        addMouseWheelListener(Mouse.GetInstance());
        addMouseMotionListener(Mouse.GetInstance());
        add(GraphicsSystem.GetInstance());
        setVisible(true);
    }

    public synchronized static Window GetInstance() {
        if (instance == null)
            instance = new Window();
        return instance;
    }

    public int GetWidth() {
        return width;
    }

    public int GetHeight() {
        return height;
    }

    private static Window instance = null;

    private final int width = 1030;
    private final int height = 755;
}
