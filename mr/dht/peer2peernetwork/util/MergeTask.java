package mr.dht.peer2peernetwork.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import mr.dht.peer2peernetwork.threadpool.Task;

public class MergeTask extends Task{
	private String 	fileName;
	private HashMap<String,Float> links 	= new HashMap<String,Float>();
	
	public MergeTask(File existingFile, String newLinks){
		addLinks(newLinks);
		addLinks( ByteStream.byteArrayToString(ByteStream.readFileBytes(existingFile)) );
		fileName 			= existingFile.getAbsolutePath();
	}
	
	public void addLinks(String linkScore){
		String[] linksList 		= linkScore.split(System.getProperty("line.separator")) ;
		for (int i = 0; i < linksList.length ; ++i ){
			String[] linkScr 	= linksList[i].split(",");
			links.put(linkScr[0], Float.parseFloat(linkScr[1]));
		}
	}
	
	private void createUniformSearchFile(){
		String text="";
        links = (HashMap<String, Float>) UtilClass.sortByValue(links);
    	for (Map.Entry<String,Float> entity : links.entrySet())
    			text += entity.getKey()+","+entity.getValue()+System.getProperty("line.separator");
    	UtilClass.storeFile(new File(fileName), text);
	}	
	
	@Override
	public void execute() {
		createUniformSearchFile();
	}
}
