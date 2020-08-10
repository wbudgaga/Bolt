package mr.dht.peer2peernetwork.datastructure;

import java.util.HashMap;

import mr.dht.peer2peernetwork.nodes.RemotePeer;
/*
 * This class is designed to hold a list of members and with ability to cache(fixed size) removed peers 
 */
public class CircularPeerQueue extends CircularQueue{
	private HashMap<Long,RemotePeer> objects  	= new HashMap<Long,RemotePeer>();
	
	public CircularPeerQueue(HashMap<Long,RemotePeer> objects,int size){
		super(size);
		this.objects 				= objects;
	}
	public CircularPeerQueue(int size){
		super(size);
	}

	/*======================================================================
	cache management
	======================================================================*/	
	public RemotePeer getCacheNextPeer(){
		return objects.get(getCacheNextKey());
	}
	public void getCachedPeers(HashMap<Long, RemotePeer> peersList){
		resetTheItrator();
		long  peerID 				= getCacheNextKey();
		while (peerID != NOTEXIST){
			if (peerID!= 0 && !peersList.containsKey(peerID)){
				peersList.put(peerID, getPeer(peerID));
			}
			peerID 				= getNextKeyOnCirularCache();
		}
	}
	/*======================================================================
	active peers management
	======================================================================*/	
	public boolean add(Long k, RemotePeer peer){
		if (objects.containsKey(k)){
			resetPos(k);//remove it from the cache since it is reactived by calling this method
			return false;
		}
		objects.put(k, peer);
		return true;
	}
	public RemotePeer remove(Long k){
		return objects.remove(k);
	}

	//return its pos 
	public boolean contains(long key){
		return objects.containsKey(key) && isInCache(key)==NOTEXIST;
	}

	public RemotePeer getPeer(Long key){
		return objects.get(key);
	}
	
	public boolean backup(Long k){
		if (remove(addToCache(k))!=null)
			return true;
		return false;
	}

	//======================================================================	
	
		public static void main(String args[]){
			CircularPeerQueue c = new CircularPeerQueue(3);
			c.add(30l,null);
			c.add(20l,null);
			c.add(18l,null);
			c.add(15l,null);
			c.add(10l,null);
			for (int i =0; i<3;i++){
				System.out.println(c.getNextKeyOnCirularCache());
			}
			
			c.backup(15l);
			c.backup(10l);
			for (int i =0; i<3;i++){
				System.out.println(c.getNextKeyOnCirularCache());
			}

		}
	
}
