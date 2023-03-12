package GraphicsEngine;
import java.awt.*;
import java.util.ArrayList;
import java.util.Map;

import javax.swing.*;

public class GraphicsSystem extends JPanel {
    private interface GraphicsLambda {
        void func(Graphics g);
    }

    private GraphicsSystem() {}

    public synchronized static GraphicsSystem GetInstance() {
        if (instance == null)
            instance = new GraphicsSystem();
        return instance;
    }

    @Override
    public void paintComponent(Graphics g){
        synchronized(renderMap){
            super.paintComponent(g);

            setBackground(backgroundColor);
            renderMap.forEach(e -> e.getValue().func(g));
            renderMap.clear();
        }
    }

    public void Render() {
        synchronized(renderMap){
            renderMap.sort((e0, e1) -> e0.getKey() - e1.getKey());
            repaint();
        }
    }

    public void SetBackgroundColor(Color c){
        synchronized (renderMap){
            renderMap.add(Map.entry(0, (Graphics g) -> {
                g.setColor(c);
                g.fillRect(0, 0, 800, 600);
            }));
        }
    }

    public void DrawPixel(Point p, Color c){
        DrawPixel(p, c, 0);
    }
    public void DrawPixel(Point p, Color c, int priority){
        synchronized (renderMap){
            renderMap.add(Map.entry(priority, (Graphics g) -> {
                g.setColor(c);
                g.drawLine(p.x, p.y, p.x, p.y);
            }));
        }
    }

    public void DrawLine(Point p0, Point p1, Color c){
        DrawLine(p0, p1, c, 0);
    }
    public void DrawLine(Point p0, Point p1, Color c, int priority)
    {
        synchronized (renderMap){
            renderMap.add(Map.entry(priority, (Graphics g) -> {
                g.setColor(c);
                g.drawLine(p0.x, p0.y, p1.x, p1.y);
            }));
        }
    }

    public void DrawRect(Rectangle r, Color c, boolean bIsFilled)
    {
        DrawRect(r, c, bIsFilled, 0);
    }
    public void DrawRect(Rectangle r, Color c, boolean bIsFilled, int priority)
    {
        synchronized (renderMap){
            renderMap.add(Map.entry(priority, (Graphics g) -> {
                g.setColor(c);
                if (bIsFilled) g.fillRect(r.x, r.y, r.width, r.height);
                else g.drawRect(r.x, r.y, r.width, r.height);
            }));
            }
    }

    public void DrawRoundRect(Rectangle r, Dimension roundness, Color c, boolean bIsFilled){
        DrawRoundRect(r, roundness, c, bIsFilled, 0);
    }
    public void DrawRoundRect(Rectangle r, Dimension roundness, Color c, boolean bIsFilled, int priority)
    {
        synchronized (renderMap){
            renderMap.add(Map.entry(priority, (Graphics g) -> {
                g.setColor(c);
                if (bIsFilled) g.fillRoundRect(r.x, r.y, r.width, r.height, roundness.width, roundness.height);
                else g.drawRoundRect(r.x, r.y, r.width, r.height, roundness.width, roundness.height);
            }));
            }
    }

    public void DrawText(String text, Point p, Color c)
    {
        DrawText(text, p, c, 0);
    }
    public void DrawText(String text, Point p, Color c, int priority){
        DrawText(text, p, new Font("Arial Bold", Font.PLAIN, 16), c, priority);
    }
    public void DrawText(String text, Point p, Font f, Color c)
    {
        DrawText(text, p, f, c, 0);
    }
    public void DrawText(String text, Point p, Font f, Color c, int priority)
    {
        synchronized (renderMap){
            renderMap.add(Map.entry(priority,(Graphics g) -> {
                g.setFont(f);
                g.setColor(c);
                g.drawString(text, p.x, p.y);
            }));
        }
    }

    public void DrawSprite(ImageIcon sprite, Point p){
        DrawSprite(sprite, p, 0);
    }
    public void DrawSprite(ImageIcon sprite, Point p, int priority){
        DrawSprite(sprite, new Rectangle(p.x, p.y, sprite.getIconWidth(), sprite.getIconHeight()), priority);
    }
    public void DrawSprite(ImageIcon sprite, Rectangle dest) {
        DrawSprite(sprite, dest, 0);
    }
    public void DrawSprite(ImageIcon sprite, Rectangle dest, int priority){
        DrawSprite(sprite, dest, new Rectangle(0, 0, sprite.getIconWidth(), sprite.getIconHeight()), priority);
    }
    public void DrawSprite(ImageIcon sprite, Rectangle dest, Rectangle src) {
        DrawSprite(sprite, dest, src, 0);
    }
    public void DrawSprite(ImageIcon sprite, Rectangle dest, Rectangle src, int priority){
        synchronized (renderMap) {
            renderMap.add(Map.entry(priority, (Graphics g) -> { 
                g.drawImage(sprite.getImage(), dest.x, dest.y, dest.x + dest.width, dest.y + dest.height, 
                                   src.x, src.y, src.x + src.width, src.y + src.height, null);
            }
            ));
        }
    }

    private static GraphicsSystem instance = null;

    private Color backgroundColor = Color.BLACK;
    private ArrayList<Map.Entry<Integer, GraphicsLambda>> renderMap = new ArrayList<Map.Entry<Integer, GraphicsLambda>>();
}
