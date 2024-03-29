package CoreEngine;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.BitSet;
import java.util.LinkedList;
import java.util.Queue;

/*
 * Singleton responsable du traitement des entrées clavier de l'utilisateur
 * Garde en mémoire les différentes entrées
 */
public class Keyboard implements KeyListener {

    static public class Event {
        public enum EKeyEvent {
            Pressed,
            Released,
            None
        }

        public Event(int _keycode, EKeyEvent _event){
            keycode = _keycode;
            event = _event;
        }

        public final int keycode;
        public final EKeyEvent event;
    }

    private Keyboard(){}

    public synchronized static Keyboard GetInstance(){
        if (instance == null)
            instance = new Keyboard();
        return instance;
    }

    // Lecture de la dernière touche enregistrée (Appuyée ou Relachée)
    public Event ReadKey(){
        if (!keyBuffer.isEmpty()) return keyBuffer.peek();
        return new Event(0, Event.EKeyEvent.None);
    }

    // Lecture du dernier caractère
    public char ReadChar(){
        if (!charBuffer.isEmpty()) return charBuffer.peek();
        return 0;
    }

    // Méthode renvoyant l'état de la liste des évènement (vide ou non)
    public boolean IsEmpty(){
        return keyBuffer.isEmpty();
    }

    // Retrait du dernier évènement. Appelé à chaque frame
    public void Pop(){
        if (!keyBuffer.isEmpty()) keyBuffer.remove();
        if (!charBuffer.isEmpty()) charBuffer.remove();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        keyStates.set(e.getKeyCode(), true);
        keyBuffer.add(new Event(e.getKeyCode(), Event.EKeyEvent.Pressed));
        TrimBuffer(keyBuffer);
    }

    @Override
    public void keyReleased(KeyEvent e) {
        keyStates.set(e.getKeyCode(), false);
        keyBuffer.add(new Event(e.getKeyCode(), Event.EKeyEvent.Released));
        TrimBuffer(keyBuffer);
    }

    @Override
    public void keyTyped(KeyEvent e) {
        charBuffer.add(e.getKeyChar());
        TrimBuffer(charBuffer);
    }

    @Override
    public String toString(){
        return "Key pressed : " + keyBuffer.size() + " Char pressed : " + charBuffer.size();
    }

    private synchronized <T> void TrimBuffer(Queue<T> buffer){
        if (buffer.size() > sizeBuffer) buffer.remove();
    } 
    
    private static Keyboard instance = null;

    private final static short sizeBuffer = 4;
    private BitSet keyStates = new BitSet(256);
    private Queue<Event> keyBuffer = new LinkedList<Event>();
    private Queue<Character> charBuffer = new LinkedList<Character>();
}
