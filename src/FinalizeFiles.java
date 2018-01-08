import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import aberscan.MappingList;
import aberscan.MappingList.Map;


public class FinalizeFiles extends Thread {
	final String outputFolderName = "EnhancedScans";
	final String outputFragmentedFolderName = "EnhancedScansFragmented";
	final String finalFolderName = "Aberscan Imaging";
	final String editedFolderName = "toBeEdited";
	File destDir;
	MapLogPane logPane;
	final String mappingsFileName = "AberscanPhotoMappings.txt";
	MappingList dirPhotoMappings;
	
	
	public FinalizeFiles(String str, File destDir, MapLogPane logPane) {
		super(str);
		this.destDir = destDir;
		this.logPane = logPane;
		dirPhotoMappings = new MappingList();   
	}
	
	 public void run() {
		String prevSubFolderName = "__aberscan__";
		File toDir = null;
		File fromFile;
			
    	File outputFolder = new File(destDir.getAbsolutePath() + "\\" + outputFolderName);
    	File outputFragmentedFolder = new File(destDir.getAbsolutePath() + "\\" + outputFragmentedFolderName);
    	File finalFolder = new File(destDir.getAbsolutePath() + "\\" + finalFolderName);
    	File tmpOutputFolder = null;
    	
    	logPane.println("\nCreating the Final Folder '" + finalFolder.getAbsolutePath() + "'");
    	
    	//first, we need to know if we are copying from the FragmentedOutputFolder or a regular OutputFolder
    	if(outputFragmentedFolder.exists()) {
    		tmpOutputFolder = outputFragmentedFolder;
    		
    		//Note that the enhanced Folder is out of date, since recent changes are all in the fragmented folder
        	File invalidOutputFolder = new File(outputFolder + "OutOfDate");
    		try {
				FileUtils.moveDirectory(outputFolder, invalidOutputFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
   				JOptionPane.showMessageDialog(null, "ERROR: Move of folder '" + outputFolder.getAbsolutePath() + "' to '" + invalidOutputFolder.getAbsolutePath() + "' failed");
			}
    	}
    	else {
    		tmpOutputFolder = outputFolder;
    	}
		
    	//first, open up the mappings file in the EnhancedScans folder or EnhancedFragmentedScans and read out the info there
		//read the current mappings out of the mapping file in copyFromDir
		dirPhotoMappings.loadMappingsFromFile(tmpOutputFolder);
    	
    	//now do the copy
		int numMaps = dirPhotoMappings.getNumMaps();
    	for (int i = 0; i < numMaps; i++){
			Map map = dirPhotoMappings.getMap(i);
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
	   				JOptionPane.showMessageDialog(null, "ERROR: Creation of a Final Folder '" + toDir.getAbsolutePath() + "' failed");
	   			}
   		 	}
    		
   		 	//copy the file
    		//first see if the photo is in the enhancedScans/toBeEdited folder instead of EnhancedScans
   	    	File editedFolder = new File(tmpOutputFolder.getAbsolutePath() + "\\" + editedFolderName);
    		fromFile = new File(editedFolder.getAbsolutePath() + "\\" + fileName);
    		if(fromFile.exists() == true) {
    			copyFile(toDir, fromFile);
    		}
    		else {
        		fromFile = new File(tmpOutputFolder.getAbsolutePath() + "\\" + fileName);
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
   				JOptionPane.showMessageDialog(null, "ERROR: Copy of File '" + fromFile.getAbsolutePath() + "' to folder '" + toDir.getAbsolutePath() + "' failed");
		  }
	 }
}
