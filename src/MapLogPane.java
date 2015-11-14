import java.awt.Insets;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;


public class MapLogPane extends JPanel implements Runnable {
	static final long serialVersionUID = 3;
    JTextArea log;
    Thread runner;
    String text;
    JFrame frame;
    
    public MapLogPane(MainGUI gui) {
    	frame = gui;
        log = new JTextArea(15,90);
        log.setMargin(new Insets(5,5,5,5));
        log.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(log);
        //Add the scroll pane to this panel.
        add(logScrollPane);
        
        if (runner == null) {
            runner = new Thread(this);
            runner.start();
        }
    }
    
    public void println(String text) {
    	text = text + "\n";
		log.append(text);
	}
    
    public void print(String text) {
		log.append(text);
	}
    
    public void run() {
        try {
	        while (true) {
	        	log.append(text);
	        	repaint();
	        	frame.repaint();
                Thread.sleep(1000);
	        }
        }
        catch (InterruptedException e) { }
    }
}
