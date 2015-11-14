import java.awt.BorderLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;


public class MainGUI extends JFrame{
	static final long serialVersionUID = 1;
	MapControlPane cFromPane;
	MapTablePane tPane;
	MapLogPane lPane;
	
	MainGUI() {
        //Set up the main frame 
        super("Photo Collector");
        setSize(1000, 600);
        
       //Set up the panel
        JPanel pane = new JPanel();
        pane.setLayout(new BorderLayout());
	
	    //Add the table panel
        tPane = new MapTablePane();
        pane.add("Center", tPane);
        
        //Add the log pane
        lPane = new MapLogPane(this);
        pane.add("North", lPane);
        
	    //Add the from control panel
        cFromPane = new MapControlPane(tPane, lPane);
        pane.add("South", cFromPane);
        
       
        setContentPane(pane);
	}

	public static void main(String[] args) {
    	// Show the mapping GUI
    	JFrame mFrame = new MainGUI();
    	
		//ExitWindow exit = new ExitWindow();
        mFrame.addWindowListener(new WindowAdapter() {

			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
        mFrame.setVisible(true);
	}
}

