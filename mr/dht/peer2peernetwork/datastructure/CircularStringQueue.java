package mr.dht.peer2peernetwork.datastructure;

import java.util.HashMap;
/*
 * This class is designed to hold a list of members and with ability to cache(fixed size) removed peers 
 */
public class CircularStringQueue extends CircularQueue{
	private HashMap<Long,String> objects  		= new HashMap<Long,String>();
	
	public CircularStringQueue(HashMap<Long,String> objects,int size){
		super(size);
		this.objects 				= objects;
	}
	
	public CircularStringQueue(int size){
		super(size);
	}

	/*======================================================================
	cache management
	======================================================================*/	
	public String getCacheNextPeer(){
		return objects.get(getCacheNextKey());
	}
	
	public void getCachedStrings(HashMap<Long, String> StringsList){
		resetTheItrator();
		long  StringID 				= getCacheNextKey();
		while (StringID != NOTEXIST){
			if (StringID != 0 && !StringsList.containsKey(StringID)){
				StringsList.put(StringID, getString(StringID));
			}
			StringID 			= getNextKeyOnCirularCache();
		}
	}
	/*======================================================================
	active peers management
	======================================================================*/	
	public boolean add(Long k, String peer){
		if (objects.containsKey(k)){
			resetPos(k);//remove it from the cache since it is reactived by calling this method
			return false;
		}
		objects.put(k, peer);
		return true;
	}
	public String remove(Long k){
		return objects.remove(k);
	}

	//return its pos 
	public boolean contains(long key){
		return objects.containsKey(key) && isInCache(key)==NOTEXIST;
	}

	public String getString(Long StringID){
		return objects.get(StringID);
	}
	
	public boolean backup(Long k){
		if (contains(k)  && remove(addToCache(k))!=null)
			return true;
		return false;
	}	
}
