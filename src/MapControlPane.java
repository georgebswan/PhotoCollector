import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JToolBar;


public class MapControlPane extends JPanel{
	static final long serialVersionUID = 2;
    static private final String fromRootDir = "C:\\Scanned_Pictures";
	static private final String destRootDir = "C:\\AberscanInProgress";
    JButton copyButton, toSelectButton, fromSelectButton, resetButton, finalizeButton;
    JComboBox<String> imageTypeList;
    JTable table;
    JTextArea log;
    JFileChooser fromChooser, destChooser;
    MapList mapList;
    File[] fromDirs;
    File destDir;
    String toDirName;
    JTextField matchField, startField;
    String matchText = "";
    String startText = "";
    JCheckBox matchCheck;
    boolean matchCheckStatus = false;
    MapTablePane tablePane;
    MapLogPane logPane;
    String imageType = "photo";
    boolean fromFilesSelected = false;
    int startNumber = 1;

 
    public MapControlPane(MapTablePane tPane, MapLogPane lPane) {
        super(new BorderLayout());
        mapList = new MapList();
        tablePane = tPane;
        logPane = lPane;

 
        //Create  the 'from' file chooser
        fromChooser = new JFileChooser();
        fromChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        fromChooser.setMultiSelectionEnabled(true);
        fromChooser.setCurrentDirectory(new File(fromRootDir));
        fromChooser.setDialogTitle("Select the Folder(s) you want to copy photos from");
        
        //Create  the 'to' file chooser
        destChooser = new JFileChooser();
        destChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        destChooser.setMultiSelectionEnabled(false);
        destChooser.setCurrentDirectory(new File(destRootDir));
        destChooser.setDialogTitle("Select the Folder you want to copy photos to");
 
        //Create the map button. Set it to disabled to start
    	ImageIcon destIcon = new ImageIcon("images/selectToDirs.jpg");
        toSelectButton = new JButton(destIcon);
        toSelectButton.setEnabled(false);
        toSelectButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
		        	int returnVal;
		        	
		            returnVal = destChooser.showDialog(MapControlPane.this, "Apply Selected Folder");
		            if (returnVal == JFileChooser.APPROVE_OPTION) {
		            	destDir = destChooser.getSelectedFile();
		            	
		            	// add the value into the right cell in the table
		            	tablePane.addFirstCell(destDir.getAbsolutePath());
		            	//tablePane.addRow(destDir.getAbsolutePath(), "", 0, "", 0);
		            	
		            	//enable the copy button
		            	copyButton.setEnabled(true);
		            } 
		            else {
		            	JOptionPane.showMessageDialog(null, "Warning: No destination folder was selected - please try again");
		            }
        		}
        	}
        );
 
        //Create the map button. Set it to disabled to start
    	ImageIcon fromIcon = new ImageIcon("images/selectFromDirs.jpg");
        fromSelectButton = new JButton(fromIcon);
        fromSelectButton.setEnabled(true);
        fromSelectButton.addActionListener(
        	new ActionListener() {
        		public void actionPerformed(ActionEvent e) {
		        	int returnVal;
		        	
		        	//first of all, do some checks to make sure the relevant info is available
		        	if(matchCheckStatus == true && matchText.equals("")) {
		        		JOptionPane.showMessageDialog(null, "Warning: The 'group folders' box is checked, but there is no name entered in the text field");
		        	}
		        	else {
			            returnVal = fromChooser.showDialog(MapControlPane.this, "Apply Selected Folder(s)");
			            if (returnVal == JFileChooser.APPROVE_OPTION) {
			            	fromDirs = fromChooser.getSelectedFiles();
			                
			            	//was the 'group folder' checkbox selected?
				            if(matchCheckStatus == false) {
				            	addMultipleMappings("", fromDirs);
				            }
				            else {
				            	//create the mappings
				            	generateMappings(fromDirs);
				            }
				            
				            //now update the Table Pane with the new mappings
				            updateTable();
				            
				            //note that the fromFiles have been selected
				            fromFilesSelected = true;
				            
				            //enable the toSelect Button
				            toSelectButton.setEnabled(true);
				            imageTypeList.setEnabled(true);
				            startField.setEnabled(true);
				          
				        	
			            } 
			            else {
			            	JOptionPane.showMessageDialog(null, "Warning: No source folders were selected - please try again");
			                //log.append("Warning: Copy directory not selected - please try again" + newline);
			            }
		        	}
        		}
        	}
        );
        
        //Create the reset button that will reset the mappings. Set it to disabled to start
    	ImageIcon resetIcon = new ImageIcon("images/resetMappings.jpg");
        resetButton = new JButton(resetIcon);
        resetButton.setEnabled(false);
        resetButton.addActionListener(
            	new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			mapList.reset();
            			tablePane.removeAllRows();
            			fromFilesSelected = false;
		            	resetButton.setEnabled(false);
            		}
            	}
            );
        
        ImageIcon finalizeIcon = new ImageIcon("images/finalizePhotos.jpg");
        finalizeButton = new JButton(finalizeIcon);
        finalizeButton.setEnabled(false);
        finalizeButton.addActionListener(
            	new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			Thread finalizeThread;
            			//copyPhotos();
		            	//JOptionPane.showMessageDialog(null, "Photos are now copied");
            			finalizeThread = new FinalizeFiles("Finalize Files", destDir, logPane);
                		finalizeThread.start();
                		
		            	finalizeButton.setEnabled(false);
            		}
            	}
            );
	
        
        //Create the copy button. Set it to disabled to start
    	ImageIcon copyIcon = new ImageIcon("images/copyPhotos.jpg");
        copyButton = new JButton(copyIcon);
        copyButton.setEnabled(false);
        copyButton.addActionListener(
            	new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			Thread copyThread;
            			//copyPhotos();
		            	//JOptionPane.showMessageDialog(null, "Photos are now copied");
            			copyThread = new CopyFiles("Copy Files", mapList, destDir, logPane, imageType, startNumber);
                		copyThread.start();
                		
		            	copyButton.setEnabled(false);
		            	finalizeButton.setEnabled(true);
            		}
            	}
            );
        
        //Text field for match name (if needed)
        matchField = new JTextField(20);
        matchField.setEditable(false);
        matchField.addFocusListener(
    		new FocusListener() {
        		public void focusGained(FocusEvent e) {	}
        		
        		public void focusLost(FocusEvent e) {
        			if(e.getSource() == matchField) {
        				matchText = matchField.getText();
        				//JOptionPane.showMessageDialog(null, matchText);
        				
        				//now check to see if we need to group the selection, since it was picked before setting the name
        				if(fromFilesSelected == true) {
    			            mapList.reset();
                			tablePane.removeAllRows();
			            	generateMappings(fromDirs);
        				}

			            
			            //now update the Table Pane with the new mappings
			            updateTable();
        			}
        		}
  
    		}
        );
        
        //Check box for match name
        matchCheck = new JCheckBox("Group Folders?");
        matchCheck.setSelected(false);
        matchCheck.addItemListener(
    	    new ItemListener() {
    	        public void itemStateChanged(ItemEvent e) {
    	            // Set "ignore" whenever box is checked or unchecked.
    	            matchCheckStatus = (e.getStateChange() == ItemEvent.SELECTED);
    	            
    	            //if checked, then enable the matchField for editing
    	            matchField.setEditable(matchCheckStatus);
    	        }
    	    }
    	);
		
        //pull down box for image type selection
        String[] imageTypes = {"photo", "slide", "negative"};
        imageTypeList = new JComboBox<String>(imageTypes);
        imageTypeList.setSelectedIndex(0);
        imageTypeList.setEnabled(false);
        imageTypeList.addActionListener(
        		new ActionListener() {
            		public void actionPerformed(ActionEvent e) {
            			@SuppressWarnings("unchecked")
						JComboBox<String> cb = (JComboBox<String>)e.getSource();
            	        imageType = (String)cb.getSelectedItem();
            		}
            	}
            );
        
       //Text field for starting number
        ImageIcon startNumberIcon = new ImageIcon("images/startNumber.jpg");
		JButton startButton = new JButton(startNumberIcon);
        startField = new JTextField(5);
        startField.setEditable(true);
        startField.setEnabled(false);
        startField.setText("1");
        startField.addFocusListener(
    		new FocusListener() {
        		public void focusGained(FocusEvent e) {	}
        		
        		public void focusLost(FocusEvent e) {
        			if(e.getSource() == startField) {
        				startText = startField.getText();
        				startNumber = Integer.parseInt(startText);
        				System.out.println("Start Number = " + startText);
        			}
        		}
  
    		}
        );

        //For layout purposes, put the buttons in a separate panel
        Box box = Box.createVerticalBox();
        int fillerWidth = 20;
        int edgeSpacer = 12;
        
		JToolBar fromBar = new JToolBar();
		fromBar.setRollover(true);
		
        fromBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        fromBar.add(matchCheck);
        fromBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        fromBar.add(matchField);
        fromBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        fromBar.add(fromSelectButton);
        fromBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        fromBar.add(resetButton);
        fromBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
		add(fromBar, BorderLayout.LINE_START);
		
		JToolBar toBar = new JToolBar();
		toBar.setRollover(true);
        //toBar.add(Box.createRigidArea(new Dimension(fillerWidth*edgeSpacer,0)));
        toBar.add(startButton);
        toBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        toBar.add(startField);
        toBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        toBar.add(imageTypeList);
        toBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        toBar.add(toSelectButton);
        toBar.add(Box.createRigidArea(new Dimension(fillerWidth,0)));
        toBar.add(copyButton);
        toBar.add(Box.createRigidArea(new Dimension(fillerWidth*edgeSpacer,0)));
		add(toBar, BorderLayout.LINE_START);
		
		JToolBar finalizeBar = new JToolBar();
		finalizeBar.setRollover(true);
        finalizeBar.add(finalizeButton);
		add(finalizeBar, BorderLayout.LINE_START);
		
		box.add(fromBar);
		box.add(toBar);
		box.add(finalizeBar);
		
 
        //Add the buttons  to this panel.
        add(box, BorderLayout.PAGE_END);

    }
    
    private void addMultipleMappings(String toDirName, File[] fDirs) {
    	mapList.addMultipleMaps(toDirName, fDirs);
    	
    	//now that we have at least one mapping, enable the reset Button
    	resetButton.setEnabled(true);
    }
    
    private void addMapping(String toDirName, File fDir) {
    	mapList.addMap(toDirName, fDir);
    	
    	//now that we have at least one mapping, enable the reset Button
    	resetButton.setEnabled(true);
    }
    
    private void generateMappings(File[] fromDirs) {
		//File[] fromDirs = new File[] {new File("George Swan Boy Set A_111"), new File("George Swan Set B_111"), new File("George Swan Folder 1_111"), new File("George Swan Folder 1 Set A_111")};
		//matchName = "George Swan";
		
		String toName;

		for(int i = 0 ; i < fromDirs.length ; i++ ) {
			//System.out.println("origName : '" + fromDirs[i].getName() + "'");
			//first check to see if the fromDir is actually one we want (error in selecting dirs?)
			toName = matchDir(matchText, fromDirs[i]);
			
			//was a match found?
			if(toName.equals("") == false) {
				//System.out.println("Mapping created is '" + toName + "' -> '" + fromDirs[i] + "'");
				addMapping(toName, fromDirs[i]);
			}
			else {
				JOptionPane.showMessageDialog(this, "Warning: No match found for folder '" + fromDirs[i].getName() + "'");
				//System.out.println("No match : matchName : '" + matchName + "' : getName = '" + fromDirs[i].getName() + "'");
			}
		}		
	}
    
    private String matchDir(String matchName, File fromDir) {
    	String replacedFromDir = "";
    	
		//first, I need to make a regex out of the matchName
		String regexMatchName = "^" + matchName + ".*";
		String regexReplaceName = matchName + "\\s*";

		//System.out.println("----matchName : '" + matchName + "'");
		//start by stripping out the chars that were put in automatically by Kodak
		
		//System.out.println("origName : '" + fromDirs[i].getName() + "'");
		//first check to see if the fromDir is actually one we want (error in selecting dirs?)
		if(fromDir.getName().matches(regexMatchName) == true) {

			replacedFromDir = fromDir.getName().replaceAll(regexReplaceName, "");
			//System.out.println("Name = '" + replacedFromDir + "'");
		
			// pull out "Set" and everything after it (created by Kodak)
			replacedFromDir = replacedFromDir.replaceAll("\\sSet.*$", "");
			//System.out.println("Name1 = '" + replacedFromDir + "'");
		
			// if there was no set, there might still be a "_1111" created by kodak
			replacedFromDir = replacedFromDir.replaceAll("_.*$", "");
			//System.out.println("Name2 = '" + replacedFromDir + "'");
			
			return(replacedFromDir);
		}
		else {
			//no match found
			return("");
		}
    }
    
    private void updateTable () {
    	String toDir;
    	int toCount;
    	int fromCount;
    	String prevToDir = "";
    	int totalCount = 0;
    	
    	//start by removing all the current rows in the existing table
    	tablePane.removeAllRows();
    	
    	// populate the table
    	FromToMap[] mapArray = mapList.toArray();
    	for(int i = 0 ; i < mapArray.length ; i++ ) {
    		//iterate through all the fromDirs
    		File[] fromDirs = mapArray[i].toArray();
    		toDir = mapArray[i].getToDirName();
    		toCount = mapArray[i].countPhotosInMapping();
    		totalCount += toCount;
    		for(int f = 0 ; f < fromDirs.length ; f++ ) {
	    		fromCount = mapArray[i].countPhotosInDirectory(fromDirs[f]);

	    		
	    		//generally each mapping is added as a row, but take out the 'toDir' and 'toCount' for multiple fromDirs
	    		if(toDir.equals(prevToDir)) {
		    		tablePane.addRow("", 
		    				"",
		    				0,
		    				fromDirs[f].getName(),
		    				fromCount);
	    		}
	    		else {
		    		tablePane.addRow("",
		    				mapArray[i].getToDirName(),
		    				toCount,
		    				fromDirs[f].getName(),
		    				fromCount);
	    		}
	    		prevToDir = toDir;
    		}
    	}
    	
		
		//lastly, put in a totals row
		//tablePane.addRow("", "", 0, "", 0);
		tablePane.addRow("", "", totalCount, "", totalCount);
    }
}
