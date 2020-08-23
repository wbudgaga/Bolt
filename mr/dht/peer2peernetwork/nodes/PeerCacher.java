package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mr.dht.peer2peernetwork.fingertable.FingerTable;

public class PeerCacher {
	private ConcurrentHashMap<Long,RemotePeer> peers 	= new ConcurrentHashMap<Long,RemotePeer>();//<peerID,RemotePeer>
	private ConcurrentHashMap<Long,Long> peersMin 		= new ConcurrentHashMap<Long,Long>();//<peerID,min>==><max,min>
	private long[]	peersIDs;
	private int 	expectedPeers 				= -1;
	
	public void setNumOfExpectedPeers(int n){
		expectedPeers 					= n;
	}
	
	public ArrayList<RemotePeer> getCachedPeers(){
		return new ArrayList<RemotePeer> (peers.values());
	}
	
	public boolean AreAllPeersReceived(){
		return peers.size() == expectedPeers;
	}
	
	public synchronized void addPeer(RemotePeer rp){
		peers.put(rp.getID(), rp);
		icrArray();
		int i 						= 0;
		while ( i<(peersIDs.length-1) && rp.getID()<peersIDs[i])
			++i;
		insert(i, rp.getID());
		updateMin();
	}
	
	private void icrArray(){
		long[] newArry;
		if (peersIDs==null)
			newArry= new long[1];
		else{
			newArry= new long[peersIDs.length+1];
			for(int i=0; i<peersIDs.length;++i)
				newArry[i]=peersIDs[i];
		}
		peersIDs=newArry;
	}
	private void insert(int idx, long id){
		int i=peersIDs.length-1;
		for (; i>idx; --i)
			peersIDs[i]=peersIDs[i-1];
		peersIDs[i]=id;
	}
	private void updateMin(){
		for (int i=0; i<peersIDs.length-1;++i){
			peersMin.put(peersIDs[i], peersIDs[i+1]+1);
		}
		peersMin.put(peersIDs[peersIDs.length-1], peersIDs[0]+1);
	}
	
	public synchronized void putPeer(Long qKey, RemotePeer rp){
		Long queryKey = qKey % Setting.RING_KEYSPACE;
		long peerID = rp.getID();
		Long min = peersMin.get(peerID);
		if (min==null){
			peers.put(peerID, rp);
			peersMin.put(peerID, queryKey);
		}else{
			if (FingerTable.canBeSuccessor(queryKey, min, peerID))
				peersMin.put(peerID, queryKey);
		}	
	}
	
	public  RemotePeer[] getPeer(Long qKey){// return peer,closest
		Long queryKey = qKey % Setting.RING_KEYSPACE;
		long closestPeer = -1;
		for (Map.Entry<Long,Long> entryItem: peersMin.entrySet()){
			long pID = entryItem.getKey();
			if (FingerTable.canBeSuccessor(entryItem.getValue(), queryKey, pID)){
				return new RemotePeer []{peers.get(pID),null};
			}
			if (FingerTable.canBeSuccessor(closestPeer, pID, queryKey)) 
				closestPeer = pID;
		}
		return new RemotePeer []{null,peers.get(closestPeer)};
	}
	
	public static PeerData getPeerData(long id){
		PeerData pd = new PeerData();
		pd.setPeerID(id);
		return pd;
	}
	public static void main(String args[]) throws IOException {
		PeerCacher pc = new PeerCacher();
		RemotePeer rp =  RemotePeer.getInstance(getPeerData(1000),null);
		pc.addPeer(rp);
		pc.addPeer(RemotePeer.getInstance(getPeerData(600),null));
		pc.addPeer(RemotePeer.getInstance(getPeerData(200),null));
		pc.addPeer(RemotePeer.getInstance(getPeerData(800),null));
		//pc.putPeer(300l, rp);
		RemotePeer rp1[]=  pc.getPeer(1500l);
		//System.out.println(rp1[0].getID());
	}
}
