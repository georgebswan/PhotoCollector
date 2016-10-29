import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;


public class CopyFiles extends Thread {
	int photoCount = 1;
	MapList mapList;
	File destDir;
	MapLogPane logPane;
	MapTablePane tablePane;
	String imageType;
	
	public CopyFiles(String str, MapList mapList, File destDir, MapLogPane logPane, String imageType, int startNumber) {
		super(str);
		this.mapList = mapList;
		this.destDir = destDir;
		this.logPane = logPane;
		this.imageType = imageType;
		photoCount = startNumber;
	}
	
	 public void run() {
	    	File copyFromDir = null;
	    	File copyToDir = null;
	    	
	    	//uniquify the mappings to make it easy to do the copying
	    	File[][] uniqueMaps = mapList.uniquifyMaps(destDir, "OriginalScans");
	    	
	    	//copy into the OriginalScans folder
	    	logPane.println("Copying " + imageType + "s ...");
	    	for(int i = 0 ; i < uniqueMaps.length ; i++ ) {
	    		//log what we are doing
	    		copyFromDir = uniqueMaps[i][1];
	    		copyToDir = uniqueMaps[i][0];
	    		logPane.print("\t'" + copyFromDir.getAbsolutePath() + "' to '" + copyToDir.getAbsolutePath() +"'");
	    		//tablePane.setSelectedRow(i);
	    		
	    		copyPhotos(copyFromDir, copyToDir, true);
	    		logPane.println(" done");
	    	}
	    	
	    	//now create the EnhancedScans folder
    		logPane.print("\nCreating the Output Folder '" + destDir.getAbsolutePath() + "\\EnhancedScans'");
	    	createEnhancedScansFolder(destDir);	
    		logPane.println(" done");
	    	
	    	//now create the aberscanMapping file in EnhancedScans folder
	    	try {
				createMappingFile(new File(destDir.getAbsolutePath() + "\\OriginalScans"), new File(destDir.getAbsolutePath() + "\\EnhancedScans"), "");
			} catch (IOException e) {
				e.printStackTrace();
			}
	    	
	    	JOptionPane.showMessageDialog(null, "Copy Complete");
	    }
	 
	public void copyPhotos(File copyFromDir, File copyToDir, boolean rename) {
		//here is where the file copy is done - within a thread
		//first make sure the toDirectory exists
		try {
			FileUtils.forceMkdir(copyToDir);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		//copy all the files in the fromDirectory
		try {
			copyFilesInDirectory(copyFromDir, copyToDir, rename);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "ERROR: Directory copy failed: fromDir = '" + copyFromDir + "', toDir = '" + copyToDir + "'");
			e.printStackTrace();
		}
	 }
	
	 private void copyFilesInDirectory(File fromDir, File toDir, boolean rename) throws IOException {
	    	//find the contents of the fromDir, then process all the files first
	    	File[] contents = fromDir.listFiles();
	    	File toFile;
	    	int copyCount = 0;
	    	for (File file : contents) {
		         if (file.isFile() && (file.getName().toLowerCase().endsWith("jpg") || file.getName().toLowerCase().endsWith("tif"))) {
		             //are we keeping the original name for toFile, or creating a new one?
		        	 if(rename == true) {
		        		 //here if renaming it. first create the name of the destination file
		        		 String ext = getExtension(file);
		        		 toFile = new File(toDir.getAbsolutePath() + "\\" + imageType + String.format("%04d", photoCount++) + "." + ext);
		        	  }
		        	  else {
		        		 //here if keeping the original name
		        		  toFile = new File(toDir.getAbsolutePath() + "\\" + file.getName());
		        	  }
		        		 //System.out.println(toFile.getAbsolutePath() + " <- " + file.getAbsolutePath());
		        	  try {
		        		  if((copyCount++ % 10) == 0) {
		        			  logPane.print(".");
		        		  }
		        		  //System.out.println("file = " + file + ", toFile = " + toFile);
		        		  FileUtils.copyFile(file, toFile);
		        	  } catch (IOException e) {
		  				  // Let me know that there was a problem
		        		  throw e;
		  			  }
		         }
	    	}
	    	
	    	//now recurse through any directories
	    	for (File file : contents) {
		          if (file.isDirectory()) {
		              copyFilesInDirectory(file, toDir, rename);
		    }
	    }
	 }   	
	 private String getExtension(File file) {
		 String absPath = file.getAbsolutePath();
		 int dot = absPath.lastIndexOf('.');
		 return (absPath.substring(dot + 1));
	 }
	
	 //private String getFileName(File file) {
	 //	 String absPath = file.getAbsolutePath();
	 //	 int dot = absPath.lastIndexOf('.');
	 //     int sep = absPath.lastIndexOf('\\');
	 //     return (absPath.substring(sep + 1, dot));
	 //}
	 
	 private void createEnhancedScansFolder(File destDir) {
	    	String srcFolder = destDir.getAbsolutePath() + "\\OriginalScans";
	    	String outputFolder = destDir.getAbsolutePath() + "\\EnhancedScans";
	    	
	    	//copy the content from OriginalScans into EnhancedScans
	    	copyPhotos(new File(srcFolder), new File(outputFolder), false);
	    	
	    	//now create the aberscanMappings.txt file in the EnhancedScans folder
	    }
	 
	 public void createMappingFile(File copyFromDir, File copyToDir, String subFolder) throws IOException {
	    	BufferedWriter out;
	    	//if first time here, open file instead of append
	    	if(subFolder.equals("")) {
	    		out = new BufferedWriter(new FileWriter(copyToDir.getAbsolutePath() + "\\AberscanPhotoMappings.txt", false));
	    	}
	    	else {
	    		out = new BufferedWriter(new FileWriter(copyToDir.getAbsolutePath() + "\\AberscanPhotoMappings.txt", true));
	    	}
	    	
	    	File[] contents = copyFromDir.listFiles();
	    	for (File file : contents) {
		         if (file.isFile() && file.getName().toLowerCase().endsWith("jpg")) {
		        	 try {
		        		  out.write(subFolder + "," + file.getName());
		        		  out.newLine();
		        	  } catch (IOException e) {
		  				// TODO Auto-generated catch block
		  				e.printStackTrace();
		  			  }
		         }
	    	}
	    	
	    	out.close();
	    	
	    	//now recurse through any directories
	    	for (File file : contents) {
		          if (file.isDirectory()) {
		              createMappingFile(new File(copyFromDir.getAbsolutePath() + "\\" + file.getName()), copyToDir, file.getName() + "\\");
		          }
	    	}
	    }
}
