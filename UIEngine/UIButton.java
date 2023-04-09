package UIEngine;
import java.awt.*;

import CoreEngine.Mouse;
import GraphicsEngine.GraphicsSystem;

/*
 * Classe d'un bouton. Défini par un rectangle, un titre, une fonction et différentes couleurs selon l'état
 * Définition des états : Base, Hover, Press
 */
public class UIButton {
    private enum EButtonState{
        BaseState,
        HoverState,
        PressState
    }

    public UIButton(Rectangle _rect, String _text, UILambda _func, Color _baseColor, Color _hoverColor, Color _pressColor){
        rect = _rect;
        text = _text;
        func = _func;
        baseColor = _baseColor;
        hoverColor = _hoverColor;
        pressColor = _pressColor;
    }

    // Update d'un bouton
    // On vérifie si la souris survole le bouton (Hover)
    // On vérifie ensuite si un clic est réalisé (Press)
    // On exécute ensuite la fonction lambda enregistrée
    public void Update() throws Exception {
        if (rect.contains(Mouse.GetInstance().GetMousePos())){
            state = EButtonState.HoverState;
            if (Mouse.GetInstance().Read() == Mouse.EEventType.LRelease){
                state = EButtonState.PressState;
                func.func();
            }
        }
        else state = EButtonState.BaseState;
    }

    // Affichage d'un bouton
    // On affiche le rectangle, puis le texte
    public void Draw(){
        Draw(0);
    }
    public void Draw(int priority){
        Color buttonColor = baseColor;
        switch(state){
        case HoverState:
            buttonColor = hoverColor;
            break;
        case PressState:
            buttonColor = pressColor;
            break;
        default:
             break;
        }

        GraphicsSystem.GetInstance().DrawRect(rect, buttonColor, true, priority);
        GraphicsSystem.GetInstance().DrawRect(rect, Color.BLACK, false, priority + 1);
        GraphicsSystem.GetInstance().DrawText(text, new Point(
            rect.x + (int)((rect.width - text.length() * 8) / 2),
            rect.y + (int)(0.5f * rect.height + 0.5f * FONTSIZE * 0.75f)
        ), new Font("Arial Bold", Font.PLAIN, FONTSIZE), Color.BLACK, priority + 2);
    }

    // Différents setters pour redéfinir les paramètres du bouton
    public void SetRectangle(Rectangle newRect){
        rect = newRect;
    }

    public void SetText(String newText){
        text = newText;
    }

    public void SetFunction(UILambda newFunc){
        func = newFunc;
    }

    public void SetBaseColor(Color newBaseColor){
        baseColor = newBaseColor;
    }

    public void SetHoverColor(Color newHoverColor){
        hoverColor = newHoverColor;
    }

    public void SetPressColor(Color newPressColor){
        pressColor = newPressColor;
    }

    private Rectangle rect;
    private String text;
    private UILambda func;
    private Color baseColor;
    private Color hoverColor;
    private Color pressColor;
    private EButtonState state = EButtonState.BaseState;
    private final static int FONTSIZE = 16;
}
