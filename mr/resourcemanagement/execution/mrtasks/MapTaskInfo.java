package mr.resourcemanagement.execution.mrtasks;

import java.io.FileNotFoundException;
import java.net.MalformedURLException;

import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.dht.util.ClassLoader;
import mr.resourcemanagement.datapartitioning.Partitioner;
import mr.resourcemanagement.io.DataSource;
import mr.resourcemanagement.io.TextFileReader;


public class MapTaskInfo<K1,V1,K2,V2> extends TaskInfo<K1,V1,K2,V2>{
	private String				partitionerClassName;
	private String 				chunkName;
	
	public MapTaskInfo(){
		super(MAP);
	}
	
	public String getTaskTypeAsString(){
		return "Map";
	}
		
	public DataSource getDataReader(ThreadPoolManager ioThreadPool) throws FileNotFoundException, InterruptedException{
		DataSource fr = new TextFileReader(chunkName);
		fr.setIOThreadPool(ioThreadPool);
		fr.setBuffer(mrTask.pendingQueue);
		return  fr;
	}
	public Partitioner<K2,V2>  getPartitioner() throws InstantiationException, IllegalAccessException, MalformedURLException, ClassNotFoundException{
		Class<Partitioner<K2,V2>> partitionerClass = ClassLoader.loadPartitioner(null, partitionerClassName);
		return partitionerClass.newInstance();
	}	
	public String getPartitionerClassName() {
		return partitionerClassName;
	}

	public void setPartitionerClassName(String partitionerClassName) {
		this.partitionerClassName = partitionerClassName;
	}

	public void setInputPath(String chunkFullName) {
		chunkName = chunkFullName;
	}
}
