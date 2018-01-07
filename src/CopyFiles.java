import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;

import aberscan.MappingList;

public class CopyFiles extends Thread {
	int photoCount = 1;
	FromToMapList fromToMapList;
	File destDir;
	MapLogPane logPane;
	MapTablePane tablePane;
	String imageType;
	boolean fragmentFlag;
	MappingList dirPhotoMappings;
	
	public CopyFiles(String str, FromToMapList mapList, File destDir, MapLogPane logPane, String imageType, int startNumber) {
		super(str);
		this.fromToMapList = mapList;
		this.destDir = destDir;
		this.logPane = logPane;
		this.imageType = imageType;
		photoCount = startNumber;
	}
	
	 public void run() {
	    	File copyFromDir = null;
	    	File copyToDir = null;
	    	
	    	//uniquify the mappings to make it easy to do the copying
	    	File[][] uniqueMaps = fromToMapList.uniquifyMaps(destDir, "OriginalScans");
	    	
	    	//copy into the OriginalScans folder
	    	logPane.println("Copying " + imageType + "s ...");
	    	for(int i = 0 ; i < uniqueMaps.length ; i++ ) {
	    		//log what we are doing
	    		copyFromDir = uniqueMaps[i][1];
	    		copyToDir = uniqueMaps[i][0];
	    		logPane.print("\t'" + copyFromDir.getAbsolutePath() + "' to '" + copyToDir.getAbsolutePath() +"'");
	    		//tablePane.setSelectedRow(i);
	    		
	    		copyPhotos(copyFromDir, copyToDir, true, false);
	    		logPane.println(" done");
	    	}
	    	
	    	//now create the EnhancedScans folder
	    	photoCount = 1;
    		logPane.print("\nCreating the Output Folder '" + destDir.getAbsolutePath() + "\\EnhancedScans'");
	    	createEnhancedScansFolder(destDir);	
    		logPane.println(" done");
	    	
	    	JOptionPane.showMessageDialog(null, "Copy Complete");
	    }
	 
	public void copyPhotos(File copyFromDir, File copyToDir, boolean rename, boolean storeMap) {
		//here is where the file copy is done - within a thread
		//first make sure the toDirectory exists
		try {
			FileUtils.forceMkdir(copyToDir);
		} catch (IOException e1) {
			e1.printStackTrace();
	    	JOptionPane.showMessageDialog(null, "ERROR: Unable to create the dir '" + copyToDir.getAbsolutePath() + "'");
		}
		
		//copy all the files in the fromDirectory
		try {
			copyFilesInDirectory(copyFromDir, "", copyToDir, rename, storeMap);
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "ERROR: Directory copy failed: fromDir = '" + copyFromDir + "', toDir = '" + copyToDir + "'");
			e.printStackTrace();
		}
	 }
	
	 private void copyFilesInDirectory(File fromDir, String fromSubDirName, File toDir, boolean rename, boolean storeMap) throws IOException {
		 
		 //System.out.println("copyFilesInDirectory: fromDir = '" + fromDir.getAbsolutePath());
	    	//find the contents of the fromDir, then process all the files first
	    	File[] contents = fromDir.listFiles();
	    	File toFile;
	    	File newToDir = toDir;
	    	for (File file : contents) {
		        if (file.isFile() && (file.getName().toLowerCase().endsWith("jpg") ||
		        		file.getName().toLowerCase().endsWith("jpeg") ||
		        		file.getName().toLowerCase().endsWith("tif") ||
		        		file.getName().toLowerCase().endsWith("tiff"))) {
		             //are we keeping the original name for toFile, or creating a new one?
		        	 if(rename == true) {
		        		 //here if renaming it. first create the name of the destination file
		        		 String ext = getExtension(file);
		        		 toFile = new File(newToDir.getAbsolutePath() + "\\" + imageType + String.format("%04d", photoCount) + "." + ext);
		        	  }
		        	  else {
		        		 //here if keeping the original name
		        		  toFile = new File(newToDir.getAbsolutePath() + "\\" + file.getName());
		        	  }
		        		 //System.out.println(toFile.getAbsolutePath() + " <- " + file.getAbsolutePath());
		        	  try {
		        		  if((photoCount++ % 10) == 0) {
		        			  logPane.print(".");
		        		  }
		        		  //System.out.println("file = " + file + ", toFile = " + toFile);
		        		  FileUtils.copyFile(file, toFile);
		        		  
		        		  //now log this copy in the FromToMappingList
		        		  if(storeMap == true) {
		        			  dirPhotoMappings.addMapping(fromSubDirName + "\\", toFile.getName());
		        		  }
		        		  
		        	  } catch (IOException e) {
		  				  // Let me know that there was a problem
		        		  throw e;
		  			  }
		        }
	    	}
	    	
	    	//now recurse through any directories
	    	for (File dir : contents) {
		          if (dir.isDirectory()) {
		              copyFilesInDirectory(dir, dir.getName(), toDir, rename, storeMap);
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
	    	String srcFolderName = destDir.getAbsolutePath() + "\\OriginalScans";
	    	String outputFolderName = destDir.getAbsolutePath() + "\\EnhancedScans";
	    	File outputFolder = new File(outputFolderName);
	    	
	    	//create a mappings file to hold the dir, file mappings
	    	dirPhotoMappings = new MappingList();

	    	
	    	//copy the content from OriginalScans into EnhancedScans
	    	copyPhotos(new File(srcFolderName), outputFolder, false, true);
	    	
	    	//now create the aberscanMapping file in EnhancedScans folder
    		try {
    			//dirPhotoMappings.print();
				dirPhotoMappings.createMappingsFile(outputFolder);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "ERROR: Can't create the Mappings file in dir '" + outputFolderName + "'");
			}
	 }
}
