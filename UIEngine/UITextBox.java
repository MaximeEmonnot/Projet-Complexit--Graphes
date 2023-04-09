package UIEngine;
import java.awt.*;

import GraphicsEngine.GraphicsSystem;

/*
 * Classe définissant un simple rectangle avec du texte
 */
public class UITextBox {
    
    public UITextBox(Rectangle _rect, String _text){
        rect = _rect;
        text = _text;
    }

    public void SetText(String _text){
        text = _text;
    }

    public void SetRectangle(Rectangle _rect){
        rect = _rect;
    }

    // Affichage du rectangle, avec le texte centré
    // Gestion de la priorité d'affichage
    public void Draw(){
        Draw(0);
    }
    public void Draw(int priority){
        GraphicsSystem.GetInstance().DrawRect(rect, Color.WHITE, true, priority);
        GraphicsSystem.GetInstance().DrawRect(rect, Color.BLACK, false, priority + 1);
        GraphicsSystem.GetInstance().DrawText(text, new Point(
            rect.x + (int)((rect.width - text.length() * 8) / 2),
            rect.y + (int)(0.6f * rect.height)
        ), Color.BLACK, priority + 2);
    }

    public String GetText() {
        return text;
    }

    private Rectangle rect;
    private String text;
}
