
import java.io.*;
import java.util.ArrayList;

import javax.swing.*;
 
public class MapList {
    ArrayList <FromToMap> mappings;
    
    public ArrayList<FromToMap> getMappings() { return mappings; }
 
    public MapList() {
    	mappings = new ArrayList<FromToMap>();
    }
    
    public void reset() {
    	mappings.clear();
    }
    
    public void addMultipleMaps(String toDirName, File[] fromDirs) {
        for (File fromDir : fromDirs){
        	mergeMap(toDirName, fromDir);
        }
    }
    
    public void addMap(String toDirName, File fromDir) {
    	mergeMap(toDirName, fromDir);
    }
    
    private void mergeMap(String toName, File fromDir) {
    	FromToMap map;
    	boolean matchFound = false;
    	
    	//find the 'toName' in the current list of mappings. If it doesn't exist, then add. Otherwise merge into existing ma
    	 for (int i = 0 ; i < mappings.size(); i++){
    		 map = mappings.get(i);
        	 if(map.getToDirName().equals(toName)) {
        		 map.appendFromDir(fromDir);
        		 matchFound = true;
        	 }
         }
    	 
    	 //if no match found, then add the map to the maplist
    	 if(matchFound == false) {
    	    	map = new FromToMap(toName, fromDir);
    	    	mappings.add(map);
    	 }
    }
    
    public FromToMap[] toArray() {
    	return ( mappings.toArray(new FromToMap[mappings.size()]));
    }
    
    public File[][] uniquifyMaps(File destDir, String subDirName) {
    	//first, find out how many unique mappings there are
    	int count = 0;
    	int index = 0;
    	FromToMap map;
    	
    	for (FromToMap map1 : mappings){
    		count += map1.getNumFromDirs();
        }

    	//set up the array
    	File[][] tmpMaps = new File[count][2];
    	
    	//now populate it
    	for (int i = 0 ; i < mappings.size() ; i++){
    		map = mappings.get(i);
			
    		for(int j = 0 ; j < map.getNumFromDirs() ; j++ ) {
    			//path = destDir.getAbsolutePath() + "\\" + map.getToDirName();
    			tmpMaps[index][0] = new File(destDir.getAbsolutePath() + "\\" + subDirName + "\\" + map.getToDirName());
    			tmpMaps[index++][1] = map.getFromDir(j).getAbsoluteFile();
    			//System.out.println(tmpMaps[index-1][0] + "' <- " + tmpMaps[index-1][1]);
    		}
        }
    	
    	return(tmpMaps);
    }
    
    public int log(JTextArea log) {
        int totalPhotoCount = 0;
        for (FromToMap map : mappings){
        	totalPhotoCount += map.log(log);
        }
        return (totalPhotoCount);
    }
    
    public void print(String word) {
    	System.out.println("===========" + word + "=========start");
        for (FromToMap map : mappings){
        	map.print();
        }
    	System.out.println("===========" + word + "=========end");
    }
}
