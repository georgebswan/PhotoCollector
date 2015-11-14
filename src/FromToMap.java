import java.io.File;
import java.util.ArrayList;

import javax.swing.JTextArea;


public class FromToMap {
	ArrayList <File> fromDirs;
	String toDirName;
	
	public String getToDirName() { return (toDirName); }
	public void setToDirName(String dirName) { toDirName = dirName; }
	public File getFromDir(int i) { return fromDirs.get(i); }
	public int getNumFromDirs() { return fromDirs.size(); }
	
	public FromToMap(String tDirName, File[] fDirs) {
		toDirName=tDirName;
		fromDirs = new ArrayList<File>();
		for(int i = 0 ; i < fDirs.length ; i++ ) {
			fromDirs.add(fDirs[i]);
		}
	}
	
	public FromToMap(String tDirName, File fDir) {
		toDirName=tDirName;
		fromDirs = new ArrayList<File>();
		fromDirs.add(fDir);
	}
	
	public void appendFromDir(File fDir) {
		fromDirs.add(fDir);
	}
	
	public int countPhotosInDirectory(File directory) {
	      int count = 0;
	      for (File file : directory.listFiles( )) {
	          if (file.isFile() && file.getName().toLowerCase().endsWith("jpg")) {
	              count++;
	          }
	          if (file.isDirectory()) {
	        	  //this is a little bit of a hack, but
	              count += countPhotosInDirectory(file);
	          }
	      }
	      return count;
	 }
	
	public int countPhotosInMapping() {
	      int count = 0;
          for (File fromDir : fromDirs){
	    	  count += countPhotosInDirectory(fromDir);
          }
	      return count;
	 }
	
    public File[] toArray() {
    	return ( fromDirs.toArray(new File[fromDirs.size()]));
    }
	
	public void print() {
		System.out.println("-------------------------------------------------------");
		System.out.println("toDirName = " + toDirName);
		System.out.println("FromDirs = ");
		
        for (File fromDir : fromDirs){
           System.out.println("\t" + fromDir.getAbsolutePath());
        }
	}

	public int log(JTextArea log) {
		int photoCount = 0;
		int totalPhotoCount = 0;
		log.append("\nMapped folder(s):\n");
        for (File fromDir : fromDirs){
        	photoCount = countPhotosInDirectory(fromDir);
        	log.append("    " + fromDir.getName() + "\t(" + photoCount + ")\n");
        	totalPhotoCount += photoCount;
        }
		log.append("        To: " + toDirName + "\t(" + totalPhotoCount + ")\n");
		return totalPhotoCount;
	}
}
