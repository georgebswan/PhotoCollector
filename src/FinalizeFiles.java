import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;


public class FinalizeFiles extends Thread {
	final String outputFolderName = "EnhancedScans";
	final String finalFolderName = "FinalScans";
	final String editedFolderName = "toBeEdited";
	File destDir;
	MapLogPane logPane;
	final String mappingsFileName = "AberscanPhotoMappings.txt";
	ArrayList<Map> finalMappings;
	int photoCount;
	
	
	public FinalizeFiles(String str, File destDir, MapLogPane logPane) {
		super(str);
		this.destDir = destDir;
		this.logPane = logPane;
		finalMappings = new ArrayList<Map>();
		photoCount = 0;
	    
	}
	
	 public void run() {
		String prevSubFolderName = "__aberscan__";
		File toDir = null;
		File fromFile;
			
    	File outputFolder = new File(destDir.getAbsolutePath() + "\\" + outputFolderName);
    	File finalFolder = new File(destDir.getAbsolutePath() + "\\" + finalFolderName);
    	File editedFolder = new File(destDir.getAbsolutePath() + "\\" + outputFolderName + "\\" + editedFolderName);
    	
    	logPane.println("\nCreating the Final Folder '" + finalFolder.getAbsolutePath() + "'");
		
    	//first, open up the mappings file in the EnhancedScans folder and read out the info there
    	finalMappings = readMappingsFile();
    	
    	//now do the copy
    	for (Map map : finalMappings){
    		String subFolderName = map.getFolderName();
    		String fileName = map.getFileName();
    		//System.out.println("subFolderName = '" + subFolderName + "', fileName = '" + fileName + "'");
    	
   		 	//check that the toDirectory exists
   		 	if(subFolderName.equals(prevSubFolderName) == false) {
   	    		String toDirName = destDir.getAbsolutePath() + "\\" + finalFolderName + "\\" + subFolderName;
   	    		toDir = new File(toDirName);
	   			try {
	   		    	logPane.println("\tCreating the Folder '" + toDirName + "'");
	   				FileUtils.forceMkdir(toDir);
	   				prevSubFolderName = subFolderName;
	   			} catch (IOException e1) {
	   				e1.printStackTrace();
	   			}
   		 	}
    		
   		 	//copy the file
    		//first see if the photo is in the enhancedScans/toBeEdited folder instead of EnhancedScans
    		fromFile = new File(editedFolder.getAbsolutePath() + "\\" + fileName);
    		if(fromFile.exists() == true) {
    			copyFile(toDir, fromFile);
    		}
    		else {
        		fromFile = new File(outputFolder.getAbsolutePath() + "\\" + fileName);
    			copyFile(toDir, fromFile);
    		}
         }
    	
    	 JOptionPane.showMessageDialog(null, "Files Finalized");
     }
	 
	 private void copyFile(File toDir, File fromFile) {
		 //System.out.println("AAA " + toDir.getAbsolutePath() + " <- " + fromFile.getAbsolutePath());
		 //File toFile = new File(toDir.getAbsolutePath() + "\\" + fromFile);
		 try {
       		  FileUtils.copyFileToDirectory(fromFile, toDir);
       	  } catch (IOException e) {
 				e.printStackTrace();
		  }
	 }
	 
	 private ArrayList<Map> readMappingsFile() {
		 ArrayList<Map> maps = new ArrayList<Map>();

		 try{
			  FileInputStream fstream = new FileInputStream(destDir.getAbsolutePath() + "\\" + outputFolderName + "\\" + mappingsFileName);
			  // Get the object of DataInputStream
			  DataInputStream in = new DataInputStream(fstream);
			  BufferedReader br = new BufferedReader(new InputStreamReader(in));
			  String strLine;
			  //Read File Line By Line
			  while ((strLine = br.readLine()) != null)   {
				  String[] splits = strLine.split(",");
				  maps.add(new Map(splits[0], splits[1]));
				  
				  // Print the content on the console
				  //System.out.println ("folderName = ;" + splits[0] + "', fileName = '" + splits[1] + "'");
			  }
			  //Close the input stream
			  in.close();
		}
		catch (Exception e) {//Catch exception if any
			  System.err.println("Error: Couldn't open the mappings file " + e.getMessage());
		}
		
		return (maps);
	 }
	 
	 private class Map {
		String folderName;
		String fileName;
		
		public String getFolderName() { return (folderName); }
		public String getFileName() { return (fileName); }
		
		public Map(String folderName, String fileName) {
			this.folderName = folderName;
			this.fileName = fileName;
		}
		
		//public void print() {
		//	System.out.println("-------------------------------------------------------");
		//	System.out.println("folderName = " + folderName);
		//	System.out.println("fileName = " + fileName);
		//}
	}
}
