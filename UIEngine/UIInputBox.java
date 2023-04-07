package UIEngine;
import java.awt.*;

import CoreEngine.Keyboard;
import CoreEngine.Mouse;
import CoreEngine.Timer;
import GraphicsEngine.GraphicsSystem;

/*
 * Champ de texte, défini par un rectangle et une description
 */
public class UIInputBox {
    
    public UIInputBox(Rectangle _rect, String _description){
        rect = _rect;
        description = _description;
    }

    // On vérifie si le champ a été sélectionné (focus)
    // Si oui, alors on lit les entrées clavier de l'utilisateur
    public void Update(){
        if (Mouse.GetInstance().Read() == Mouse.EEventType.LRelease)
            bIsFocused = rect.contains(Mouse.GetInstance().GetMousePos());

        if (bIsFocused){
            IndicatorRoutine();
            ReadRoutine();
        }
        else text = text.replace("|", "");
    }

    // Affichage du champ de texte
    // Affichage du rectangle, puis du texte. Si aucun texte n'a été saisi par l'utilisateur, alors on affiche le texte de description
    public void Draw(){
        Draw(0);
    }
    public void Draw(int priority){
        GraphicsSystem.GetInstance().DrawRect(rect, Color.BLACK, true, priority);
        int ratio = bIsFocused ? 2 : 1;
        Rectangle innerRect = new Rectangle(rect.x + ratio, rect.y + ratio, rect.width - 2*ratio, rect.height - 2*ratio);
        GraphicsSystem.GetInstance().DrawRect(innerRect, Color.WHITE, true, priority + 1);
        String textToDraw = "";
        Color textColor = Color.BLACK;
        Font textFont = null;
        if (!bIsFocused && text.length() == 0){
            textToDraw = description;
            textColor = Color.GRAY;
            textFont = new Font("Arial Bold", Font.ITALIC, FONTSIZE);
        }
        else{
            textToDraw = text;
            textFont = new Font("Arial Bold", Font.PLAIN, FONTSIZE);
        } 
        GraphicsSystem.GetInstance().DrawText(textToDraw, new Point(
            rect.x + (int)(0.1f * rect.width),
            rect.y + (int)(0.5f * rect.height + 0.5f * FONTSIZE * 0.75f)
        ), textFont, textColor, priority + 2);
    }

    // Redéfinition des caractères autorisés
    public void SetNewAuthorizedChar(String newAuthorizedChar){
        authorizedChar = newAuthorizedChar;
    }

    // Définition de la taille maximale du champ de texte
    public void SetNewMaximalSize(int newMaximalSize){
        maximalSize = newMaximalSize;
    }

    public String GetText() {
        return text.replace("|","");
    }

    public void SetText(String _text){
        text = _text;
    }
    
    public void SetDescription(String newDescription){
        description = newDescription;
    }

    // Méthode pour l'indicateur (barre clignotante)
    private void IndicatorRoutine() {
        currentTimer += Timer.GetInstance().DeltaTime();
        if (currentTimer > timer){
            if (text.endsWith("|")) text = text.replace("|", "");
            else text += "|";
            currentTimer = 0.f;
        }
    }

    // Méthode pour la lecture des entrées clavier
    private void ReadRoutine() {
        char c = Keyboard.GetInstance().ReadChar();
        if (c != 0) {
            text = text.replace("|", "");
            currentTimer = 0.f;
        }
        if (authorizedChar.isEmpty() && defaultAuthorizedChar.contains(Character.toString(c)) && text.length() < maximalSize) text += c;
        else if (!authorizedChar.isEmpty() && authorizedChar.contains(Character.toString(c)) && text.length() < maximalSize) text += c;
        else if (c == 8 && !text.isEmpty()) text = text.substring(0, text.length() - 1);
    }
    
    private Rectangle rect;
    private String description;
    private String text = "";
    private boolean bIsFocused = false;
    private String authorizedChar = "";
    private int maximalSize = Integer.MAX_VALUE;
    private final static String defaultAuthorizedChar = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789?!,;.:/\\\'\"°+-*@&éèêàùç²{([|])}=_><%$€#~ ";
    private final static int FONTSIZE = 16;
    
    private final float timer = 0.75f;
    private float currentTimer = 0.0f; 
}