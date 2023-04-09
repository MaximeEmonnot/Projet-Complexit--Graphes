package CoreEngine;
import java.awt.Point;
import java.awt.event.*;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Singleton responsable des entrées souris de l'utilisateur
 * Garde en mémoire les différents évènements de la souris
 */
public class Mouse implements MouseListener, MouseWheelListener, MouseMotionListener {
    
    public enum EEventType {
        LClick,
        LPress,
        LRelease,
        MClick,
        MPress,
        MRelease,
        RClick,
        RPress,
        RRelease,
        WheelUp,
        WheelDown,
        Move,
        None
    }

    private Mouse() {}

    public synchronized static Mouse GetInstance() {
        if (instance == null)
            instance = new Mouse();
        return instance;
    }

    // Lecture du dernier évènement enregistré
    public EEventType Read() {
        if (!buffer.isEmpty()) return buffer.peek();
        return EEventType.None;
    }

    // Récupération de la position de la souris
    public int GetMousePosX(){
        return position.x;
    }
    public int GetMousePosY() {
        return position.y;
    }
    public Point GetMousePos(){
        return position;
    }

    // Récupération des états des boutons
    public boolean LeftIsPressed() {
        return bLeftIsPressed;
    }
    public boolean MiddleIsPressed() {
        return bMiddleIsPressed;
    }
    public boolean RightIsPressed() {
        return bRightIsPressed;
    }

    // Retrait du dernier évènement. Appelé à chaque frame
    public void Pop(){
        if (!buffer.isEmpty()) buffer.remove();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        buffer.add(EEventType.Move);
        TrimBuffer();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        buffer.add(EEventType.Move);
        TrimBuffer();
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        buffer.add((e.getWheelRotation() < 0) ? EEventType.WheelUp : EEventType.WheelDown); 
        TrimBuffer();
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        switch(e.getButton()){
            case MouseEvent.BUTTON1:
            buffer.add(EEventType.LClick);
            break;
            case MouseEvent.BUTTON2:
            buffer.add(EEventType.MClick);
            break;
            case MouseEvent.BUTTON3:
            buffer.add(EEventType.RClick);
            break;
            default:
            break;
        }
        TrimBuffer();
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        buffer.add(EEventType.Move);
        TrimBuffer();
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        buffer.add(EEventType.Move);
        TrimBuffer();
    }
    
    @Override
    public void mousePressed(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);
        
        switch(e.getButton()){
            case MouseEvent.BUTTON1:
            buffer.add(EEventType.LPress);
            bLeftIsPressed = true;
            break;
            case MouseEvent.BUTTON2:
            buffer.add(EEventType.MPress);
            bMiddleIsPressed = true;
            break;
            case MouseEvent.BUTTON3:
            buffer.add(EEventType.RPress);
            bRightIsPressed = true;
            break;
            default:
            break;
        }
        TrimBuffer();
    }
    
    @Override
    public void mouseReleased(MouseEvent e) {
        position = e.getPoint();
        position.translate(WINDOW_OFFSET.x, WINDOW_OFFSET.y);

        switch(e.getButton()){
        case MouseEvent.BUTTON1:
            buffer.add(EEventType.LRelease);
            bLeftIsPressed = false;
            break;
        case MouseEvent.BUTTON2:
            buffer.add(EEventType.MRelease);
            bMiddleIsPressed = false;
            break;
        case MouseEvent.BUTTON3:
            buffer.add(EEventType.RRelease);
            bRightIsPressed = false;
            break;
        default:
            break;
        }
        TrimBuffer();
    }

    @Override
    public String toString() {
        return "Mouse postion : (" + position.x + ", " + position.y + ") Buttons State : L = " + bLeftIsPressed + " M = " + bMiddleIsPressed + " R = " + bRightIsPressed;
    }

    private synchronized void TrimBuffer() {
        if (buffer.size() > bufferSize) buffer.remove();
    }

    private static Mouse instance = null;

    private final static Point WINDOW_OFFSET = new Point(-8, -31);

    private boolean bLeftIsPressed = false;
    private boolean bMiddleIsPressed = false;
    private boolean bRightIsPressed = false;
    private Point position = new Point(0, 0);
    private final static short bufferSize = 4;
    private Queue<EEventType> buffer = new LinkedList<EEventType>();
}
