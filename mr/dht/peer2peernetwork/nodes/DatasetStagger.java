package mr.dht.peer2peernetwork.nodes;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.peer2peernetwork.util.MurmurHash3;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.resourcemanagement.io.DataReader;

public class DatasetStagger  extends Task{
	private String 	datasetName;
	private String 	datasetDir;
	private ThreadPoolManager taskThreadPool;  
	private final BlockingQueue<FileStagger> fileStaggerPool;
	protected final DataStagger	dataStagger;
	
	public DatasetStagger(ThreadPoolManager  taskThreadPool, DataStagger ds, int staggerSize) {
		this.taskThreadPool 		= taskThreadPool;
		dataStagger 			= ds;
		fileStaggerPool			= new ArrayBlockingQueue<FileStagger>(staggerSize);
		for (int i = 0; i < staggerSize; ++ i){
			returnObjToQueue(new FileStagger(this));
		}
	}
	
	public DataStagger getDataStagger(){
		return dataStagger;
	}
	public void setDataset(String datasetName, String  dataDir){
		this.datasetName	= datasetName;
		this.datasetDir 	= dataDir;
	}
	
	protected void returnObjToQueue(FileStagger fs){
		fileStaggerPool.offer(fs);
	}

	
	public void submitDataset() throws InterruptedException, ClassNotFoundException, InstantiationException, IllegalAccessException, IOException{
		DataReader 	dataReader 	= new DataReader(datasetDir); 
		String 		baseName 	=  datasetName+"_";//  .%04d" ;
		File 		dataFile 	= dataReader.nextFile();
		DatasetMetaData dataMeta = new DatasetMetaData();
		
		dataMeta.setDataSetHashKey(UtilClass.hashMKey(datasetName));
//		int i = 0;
		while(dataFile != null){
			FileStagger fs = fileStaggerPool.take();
			fs.setFile(dataFile);
			fs.setChunkNameFormat(baseName+dataFile.getName()+".%04d");
			taskThreadPool.addTask(fs);
			dataMeta.addFileData(dataFile.getName(), dataFile.length());
/*			if (dataFile!=null)
				break;
			
*/			dataFile = dataReader.nextFile();
		}
		dataStagger.sendDataToPeer(dataMeta.getDataSetHashKey(), dataMeta);//submit dataset' metadata
		//dataStagger.pendChunk(dataMeta.getDataSetHashKey(), DataStagger.POISION_MSG);

	}

	@Override
	public void execute() throws IOException, InterruptedException {
		try {
			System.out.println("Submitting the dataset "+datasetName+"  ...");
			submitDataset();
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException e) {
			e.printStackTrace();
		}
		
	}
}	
