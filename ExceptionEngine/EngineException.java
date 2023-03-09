package ExceptionEngine;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class EngineException extends Exception {
    public EngineException() {
        super();
    }

    public EngineException(String message){
        super(message);
    }

    public EngineException(String message, Throwable t){
        super(message, t);
    }

    public static void ShowMessageBox(Exception e, String title) {
        String message = "Message : " + e.getMessage() + "\n\n";

        for (int i = 0; i < e.getStackTrace().length; i++){
            message += 
                "[Method name] : " + e.getStackTrace()[i].getMethodName() +
                "\n[File] : " + e.getStackTrace()[i].getFileName() +
                "\n[Line] : " + e.getStackTrace()[i].getLineNumber() + "\n\n";
        }

        JOptionPane.showMessageDialog(new JFrame(title), message, title, JOptionPane.ERROR_MESSAGE);
    }
}
