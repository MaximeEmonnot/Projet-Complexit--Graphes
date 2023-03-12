package CoreEngine;
import javax.swing.JFrame;

import GraphicsEngine.GraphicsSystem;

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

    private static Window instance = null;

    private final int width = 800;
    private final int height = 600;
}
