package mr.dht.peer2peernetwork.datastructure;

import java.util.HashMap;

import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.threadpool.Task;
/*
 * This class is designed to hold a list of members and with ability to cache(fixed size) removed peers 
 */
public class CircularQueue{
	public final int 	NOTEXIST			= -1;
	public final int 	INCACHE				= 0;
	public final int 	INBUFFER			= 1;
	private long[]		keysList;
	private int 		addPos				= 0;
	private int 		curPos				= 0;
	private int 		posMarker			= 0;
	private int 		maxSize;	
	/*======================================================================
	cache management
	======================================================================*/	
	public CircularQueue(int size){
		this.maxSize 					= size;
		this.keysList 					= new long[maxSize];
	}
	protected void resetPos(long k){
		int idx 					= isInCache(k);
		if (idx!=NOTEXIST)
			keysList[idx] 				= 0;
	}

	public int isInCache(long k){
		for(int i=0; i<keysList.length; ++i)
			if (keysList[i] == k){
				return i;
			}
		return NOTEXIST;
	}
	
	//add to current pos and returns the deleted key
	protected long addToCache(long k){
		long oldKey 					= keysList[addPos];
		keysList[addPos] 				= k;
		addPos 				= (addPos + 1) % maxSize;
		return oldKey;
	}

	public long getNextKeyOnCirularCache(){
		long k = keysList[curPos];
		curPos	 = (curPos + 1) % maxSize;
		return k;
	}
	public void resetTheItrator(){
		posMarker = 0;
	}
	public long getCacheNextKey(){
		if (posMarker == maxSize){
			return -1;
		}
		long k = keysList[posMarker];
		++posMarker;
		return k;
	}
	
	public static void main(String args[]){
		HashMap<Long, RemotePeer> peersList = new HashMap<Long, RemotePeer> ();
		CircularTaskQueue c = new CircularTaskQueue(3);
		Task t=null;
		c.add(30l,t);
		c.add(20l,t);
		c.add(18l,t);
		c.add(15l,t);
		c.add(10l,t);
		for (int i =0; i<3;i++){
			System.out.println(c.getNextKeyOnCirularCache());
		}
		c.backup(15l);
		c.backup(10l);
		c.backup(10l);
		c.backup(15l);
		for (int i =0; i<3;i++){
			System.out.println(c.getNextKeyOnCirularCache());
		}

	}

}

