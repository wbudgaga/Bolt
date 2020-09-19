package mr.resourcemanagement.execution.mrtasks.management;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;

public class MetaDataManager extends Task{
	private ConcurrentHashMap<Long, DatasetMetaData> datasets 	= new ConcurrentHashMap<Long, DatasetMetaData>();//<hash(datsetName),DMD_MSG>
	private ConcurrentHashMap<Long, FileMetaData> 	files 		= new ConcurrentHashMap<Long, FileMetaData>();//<hash(FileMetaData),fileMetaMSG>
	private Peer localPeer;
	public final String META_BASENAME 				= Setting.DATA_DIR+"%s%d/%s";
	
	public MetaDataManager(Peer lPeer) throws IOException{
		this.localPeer						= lPeer;
		if (Setting.LOAD_METADATA){
			loadDatasetsMetaData(0);
			loadFilesMetaData(0);
		}
	}
	
	public void loadDatasetsMetaData(int replicaNr) throws IOException{
		System.out.println(this.localPeer.getNodeData().getNickName()+"============: "+String.format(META_BASENAME,"datasetsMeta" ,replicaNr, ""));
		ArrayList<String> datasetsNames 			= UtilClass.readFilesFromDir(String.format(META_BASENAME,"datasetsMeta" ,replicaNr, ""));
		
		for(String dataset:datasetsNames){
			DatasetMetaData dmMSG 				= new DatasetMetaData();
			UtilClass.loadData(dmMSG, dataset);
			datasets.put(dmMSG.getDataSetHashKey(), dmMSG);
		}
	}

	public void loadFilesMetaData(int replicaNr) throws IOException{
		ArrayList<String> metaNames 				= UtilClass.readFilesFromDir(String.format(META_BASENAME,"filesMeta" ,replicaNr,""));
		for(String metaName:metaNames){
			FileMetaData dmMSG 				= new FileMetaData();
			UtilClass.loadData(dmMSG, metaName);
			files.put(dmMSG.getFileHashedKey(), dmMSG);
		}
	}

	public void addDataset(Long hashedKey, DatasetMetaData filesData){
		datasets.put(hashedKey, filesData);
	}
	
	public DatasetMetaData getDataset(long hk){
		return datasets.get(hk);
	}

	public ArrayList<DatasetMetaData> getAllDatasets(){
		if (datasets.size() > 0)
			return new ArrayList<DatasetMetaData>(datasets.values());
		return null;
	}

	public void addFile(Long hashedKey, FileMetaData fileData){
		files.put(hashedKey, fileData);
	}
	
	public FileMetaData getFile(long hk){
		return files.get(hk);
	}

	public ArrayList<FileMetaData> getAllFiles(){
		if (files.size() > 0)
			return new ArrayList<FileMetaData>(files.values());
		return null;
	}

	public void flushDatasets() throws IOException{
		for (Map.Entry<Long, DatasetMetaData> dataset: datasets.entrySet()){
			DatasetMetaData dmMSG     				= dataset.getValue();
			String outoutName 					= String.format(META_BASENAME,"datasetsMeta" ,dmMSG.getReplicateNr()-1,dataset.getKey());
			UtilClass.flushData(dataset.getValue(), outoutName);
		}
	}
	public void flushFiles() throws IOException{
		for (Map.Entry<Long, FileMetaData> file: files.entrySet()){
			FileMetaData fmMSG = file.getValue();
			String outoutName = String.format(META_BASENAME,"filesMeta" ,fmMSG.getReplicateNr()-1,file.getKey());
			System.out.println(Setting.HOSTNAME+"#######################Flushing file "+outoutName);
			UtilClass.flushData(file.getValue(), outoutName);
		}
	}
	public static void main(String[] a){
		String 		META_BASENAME = Setting.DATA_DIR+"%s%d/%s";
		System.out.println(String.format(META_BASENAME,"datasetsMeta" ,0,""));
	}
	@Override
	public void execute() throws IOException, InterruptedException {		
	}
	
}
