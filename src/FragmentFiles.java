import java.io.File;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import aberscan.MappingList;
import aberscan.MappingList.Map;



public class FragmentFiles extends Thread {
	File destDir;
	MapLogPane logPane;
	MappingList dirPhotoMappings;
	
	public FragmentFiles(String str, File destDir, MapLogPane logPane) {
		this.destDir = destDir;
		this.logPane = logPane;
		dirPhotoMappings = new MappingList();
	}
	
	 public void run() {
 		String outputFolder = destDir.getAbsolutePath() + "\\EnhancedScans";
	    String outputFragmentedFolder = destDir.getAbsolutePath() + "\\EnhancedScansFragmented";
	    	
    	//now create the Fragmented Enhanced folder
		logPane.print("\nCreating the Fragmented Folder '" + outputFragmentedFolder);

    	//copy the content from EnhancedScans into EnhancedFragmentedScans
    	fragmentPhotos(outputFolder, outputFragmentedFolder);
		logPane.println(" done");	
    	
    	JOptionPane.showMessageDialog(null, "Fragmentation Complete");
	    }
	 
	public void fragmentPhotos(String enhanceDirName, String fragDirName) {
		
		//here is where the file copy is done - within a thread
		//first make sure the frag Directory exists
		try {
			FileUtils.forceMkdir(new File(fragDirName));
		} catch (IOException e1) {
			e1.printStackTrace();
	    	JOptionPane.showMessageDialog(null, "ERROR: Could not create Fragmented folder '" + fragDirName + "'");
		}
		
		//read the current mappings out of the mapping file in copyFromDir
		dirPhotoMappings.loadMappingsFromFile(new File(enhanceDirName));
		
		//copy all the files in the fromDirectory
		int numMaps = dirPhotoMappings.getNumMaps();
		File copyFromFile;
		File fragSubDir = new File(fragDirName);  //just an initialization - this assignment has no meaning
		String fragSubDirName = "";
		
		for(int i = 0;i < numMaps; i++) {
			Map map = dirPhotoMappings.getMap(i);
    		copyFromFile = new File(enhanceDirName + "\\" + map.getFileName());
			
			if((i % 200) == 0) {
				fragSubDirName = String.format( "photos%04d-%04d", i+1, i + 200);
				fragSubDir = new File(fragDirName + "\\" + fragSubDirName);
				try {
					FileUtils.forceMkdir(fragSubDir);
				} catch (IOException e1) {
					e1.printStackTrace();
			    	JOptionPane.showMessageDialog(null, "ERROR: Could not create Sub Fragmented folder '" + fragSubDir.getAbsolutePath() + "'");
				}
    		}
			
			if((i % 10) == 0) {
  			  logPane.print(".");
			}
			
        	try {
        		FileUtils.copyFileToDirectory(copyFromFile, fragSubDir);
        	} catch (IOException e) {
  			    // Let me know that there was a problem
				e.printStackTrace();
            	JOptionPane.showMessageDialog(null, "ERROR: Copy of file '" + copyFromFile.getAbsolutePath() + "' to dir'" + fragSubDir.getAbsolutePath() + "' failed");
	        }
        	
        	//now update the fileName in the mappings file to include the fragSubDir
        	map.addSubDirNameToFile(fragSubDirName);
		}
		

		//write back out the modified mappings
		//now create the aberscanMapping file in EnhancedScans folder
		try {
			//dirPhotoMappings.print();
			dirPhotoMappings.createMappingsFile(new File(fragDirName));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "ERROR: Can't create the Mappings file in dir '" + fragDirName + "'");
		}
	}
}
	 

