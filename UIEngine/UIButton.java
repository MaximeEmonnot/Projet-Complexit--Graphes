package UIEngine;
import java.awt.*;

import CoreEngine.Mouse;
import GraphicsEngine.GraphicsSystem;

public class UIButton {
    public interface Lambda{
        void func() throw Exception;
    }

    private enum EButtonState{
        BaseState,
        HoverState,
        PressState
    }

    public UIButton(Rectangle _rect, String _text, Lambda _func, Color _baseColor, Color _hoverColor, Color _pressColor){
        rect = _rect;
        text = _text;
        func = _func;
        baseColor = _baseColor;
        hoverColor = _hoverColor;
        pressColor = _pressColor;
    }

    public void Update() {
        if (rect.contains(Mouse.GetInstance().GetMousePos())){
            state = EButtonState.HoverState;
            if (Mouse.GetInstance().Read() == Mouse.EEventType.LRelease){
                state = EButtonState.PressState;
                func.func();
            }
        }
        else state = EButtonState.BaseState;
    }

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
            rect.y + (int)(0.6f * rect.height)
        ), Color.BLACK, priority + 2);
    }

    private Rectangle rect;
    private String text;
    private Lambda func;
    private Color baseColor;
    private Color hoverColor;
    private Color pressColor;
    private EButtonState state = EButtonState.BaseState;
}
