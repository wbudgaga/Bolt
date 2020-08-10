package mr.dht.peer2peernetwork.datastructure;

import java.util.HashMap;

import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.threadpool.Task;
/*
 * This class is designed to hold a list of members and with ability to cache(fixed size) removed peers 
 */
public class CircularTaskQueue extends CircularQueue{
	private HashMap<Long,Task> objects  	= new HashMap<Long,Task>();
	
	public CircularTaskQueue(HashMap<Long,Task> objects,int size){
		super(size);
		this.objects 			= objects;
	}
	
	public CircularTaskQueue(int size){
		super(size);
	}

	/*======================================================================
	cache management
	======================================================================*/	
	public Task getCacheNextPeer(){
		return objects.get(getCacheNextKey());
	}
	public void getCachedTasks(HashMap<Long, Task> tasksList){
		resetTheItrator();
		long  taskID = getCacheNextKey();
		while (taskID != NOTEXIST){
			if (taskID!= 0 && !tasksList.containsKey(taskID)){
				tasksList.put(taskID, getTask(taskID));
			}
			taskID = getNextKeyOnCirularCache();
		}
	}
	/*======================================================================
	active peers management
	======================================================================*/	
	public boolean add(Long k, Task peer){
		if (objects.containsKey(k)){
			resetPos(k);//remove it from the cache since it is reactived by calling this method
			return false;
		}
		objects.put(k, peer);
		return true;
	}
	public Task remove(Long k){
		return objects.remove(k);
	}

	//return its pos 
	public boolean contains(long key){
		return objects.containsKey(key) && isInCache(key)==NOTEXIST;
	}

	public Task getTask(Long taskID){
		return objects.get(taskID);
	}
	
	public boolean backup(Long k){
		if (contains(k)  && remove(addToCache(k))!=null)
			return true;
		return false;
	}

	//======================================================================	
	
}
