package mr.resourcemanagement.io;

import java.io.File;
import java.util.ArrayList;

import mr.dht.peer2peernetwork.util.UtilClass;

public class DataReader {
	protected ArrayList<String> 		dirFiles;
	protected int				curFileIdx	= -1;
	
	public DataReader(String srcDir, String ext){
		dirFiles 					= UtilClass.readFilesFromDir(srcDir, ext);
	}
	
	public DataReader(String srcDir){
		dirFiles 					= UtilClass.readFilesFromDir(srcDir);
	}
	
	private File getFile(int fIndex){
		String fName = dirFiles.get(fIndex);
		return new File(fName); 
	}

	public File nextFile(){
		if (++curFileIdx < dirFiles.size()){
			return getFile(curFileIdx);
		}
		return null;
	}

}
