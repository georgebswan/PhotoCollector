import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.io.FileUtils;


public class MappingFile {
	String mapFileName;
	int photoCount;
	String fragDirName;
	
	public MappingFile() {
		mapFileName = "\\AberscanPhotoMappings.txt";
		photoCount = 1;
		fragDirName = "";
	}
	
	public void createMappingFile(File copyFromDir, File copyToDir, String subFolder, boolean fragment) throws IOException {
	    BufferedWriter out;
	    //if first time here, open file instead of append
	    if(subFolder.equals("")) {
	    	out = new BufferedWriter(new FileWriter(copyToDir.getAbsolutePath() + mapFileName, false));
	    }
	    else {
	    	out = new BufferedWriter(new FileWriter(copyToDir.getAbsolutePath() + mapFileName, true));
	    }
	    
	    File[] contents = copyFromDir.listFiles();
	    for (File file : contents) {
		    if (file.isFile() && (file.getName().toLowerCase().endsWith("jpg") ||
		    	file.getName().toLowerCase().endsWith("jpeg") ||
		    	file.getName().toLowerCase().endsWith("tif") ||
		    	file.getName().toLowerCase().endsWith("tiff"))) {
		    	
		    	//first, check if fragDirName needs to be updated
		    	if((fragment == true) && ((photoCount - 1) % 200) == 0) {
	    			fragDirName = String.format( "dir%04d\\", photoCount - 1);
	    			//System.out.println("DirName = " + newToDir.getAbsolutePath());
	    		}
		    	
		    	try {
		    		out.write(subFolder + "," + fragDirName + file.getName());
		        	out.newLine();
		        } catch (IOException e) {
		  			// TODO Auto-generated catch block
		  			e.printStackTrace();
		  		}
			photoCount++;
		    }
	    }
	    	
	    out.close();
	    	
	    //now recurse through any directories
	    for (File file : contents) {
		    if (file.isDirectory()) {
		    createMappingFile(new File(copyFromDir.getAbsolutePath() + "\\" + file.getName()), copyToDir, file.getName() + "\\", fragment);
	    	}
	    }
	}
}
