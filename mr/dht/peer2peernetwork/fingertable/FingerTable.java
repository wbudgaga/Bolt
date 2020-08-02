package mr.dht.peer2peernetwork.fingertable;

import java.io.IOException;
import java.util.Arrays;

import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;

public class FingerTable {
	public 	static 	final 	int SUCCESSORIdx = 0;
	public 	static 	final 	int NONE		= -1;
	private FTManager 	ftM;
	private long 		peerID;

	private long 	predecessorID;
	private long	maxNumOfTheRingNodes;
	private long 	table[];
	
	public FingerTable(FTManager ftM, long peerID, int size){
		this.ftM	 			= ftM;
		this.peerID	 			= peerID;
		table 					= new long[size];
		maxNumOfTheRingNodes			= (long) Math.pow(2.0, size);
		resetTable();
	}
	
	protected void resetPredecessor(){
		predecessorID   			= NONE;
	}
	
	private void resetTable(){
		resetPredecessor();
		Arrays.fill(table,peerID); 
	}
	
	//return the finger table entry at position idx
	public long getSuccValueAtPos(int idx){
		long val 				= (long) (peerID + Math.pow(2.0, idx));
		return val % maxNumOfTheRingNodes;
	}
	
	protected long getContentAtPos(int entryID) {
		return table[entryID];
	}
	// set value at position pos and returns the old one 
	protected long setContentAtPos(int pos, long value) {
		long oldValue 				= table[pos]; 
		table[pos] 				= value;
		return oldValue;
	}
	

	//insert at given location and return the last one in the list
	public long insertContentAtPos(int pos, long value) throws InvalidFingerTableEntry{
		if (pos >= table.length)
			throw new InvalidFingerTableEntry("Invalid finger table position to add with!");
		
		for (int i = pos;i<table.length; ++i){
			value =setContentAtPos(i , value);
		}		
		return value;
	}

	public boolean canBeSuccessor(long givenPeerID){
		return canBeSuccessor(peerID,givenPeerID , getSuccessor());
	}
	
	//generic method and its results totally depends only input parameters
	public static boolean canBeSuccessor(long nodeID, long givenPeerID,long currSuccessor){
		return nodeID!=givenPeerID && distance(nodeID,givenPeerID) < distance(nodeID,currSuccessor);
	}
	
	public boolean canBePredecessor(long givenPeerID){
		return predecessorID == NONE || canBeSuccessor(predecessorID, givenPeerID, peerID);
	}
	
	// return the distance(number of nodes ) between two nodes on the ring
	private static long distance(long start,long end){
		if (end < start)
			end = end + Setting.RING_KEYSPACE ;
		return end - start;    
	}
	
	public synchronized boolean updatePredecessor(long newPredecessorID){
		if (canBePredecessor(newPredecessorID)){
			predecessorID = newPredecessorID;
			return true;
		}
		return false;
	}
	
	public boolean haveSuccessor(){
		return getSuccessor() != peerID;
	}

	public boolean inPeerRange(long key){
		return  (getPredecessorID() == NONE) || (key == peerID) || canBeSuccessor(predecessorID, key, peerID);
	}

	// add new peer and return the finger entry index where it added
	protected synchronized int addEntry(long newPeerID) throws InvalidFingerTableEntry{
		if (newPeerID >= maxNumOfTheRingNodes) 
			throw new InvalidFingerTableEntry("The given nodeID("+newPeerID+") > maximum ID("+(maxNumOfTheRingNodes-1)+")");
		int insertedIdx = NONE;
		for (int i =table.length - 1; i > -1; --i){
			long nextSucc = getSuccValueAtPos(i);
			if(nextSucc == newPeerID || canBeSuccessor(nextSucc, newPeerID,getContentAtPos(i))){
				setContentAtPos(i, newPeerID);/*This exception will be never raised because i < table.length*/
				insertedIdx = i;
			}
		}
		return insertedIdx; 
	}

	public long succ(long id){
		if (id == getSuccessor() || canBeSuccessor(peerID,id,getSuccessor()))
			return getSuccessor();
		else
			return findClosestPrecedingNode(id);
	}

	public long fixFingerEntry(int entryID, long previousHealthyID){
		long entryPeerID = getContentAtPos(entryID);
		if(!ftM.isHealthyPeer(entryPeerID)){
			setContentAtPos(entryID, previousHealthyID);
			return previousHealthyID;
		}
		return entryPeerID;				
	}
	
	public synchronized void fixFingerEntries(){
		long curID = getPredecessorID();
		if (!ftM.isHealthyPeer(curID)){
			resetPredecessor();
			curID = getPeerID();
		}
		for(int i =table.length - 1; i >= 0 ; --i){
			curID= fixFingerEntry(i,curID);
		}

		if (curID == getPeerID())
			;// use predecessor to construct ft bc it is empty
		if (getPredecessorID() == NONE)
			predecessorID = getContentAtPos(table.length-1);
	}

	// find the highest predecessor of id
	private long findClosestPrecedingNode(long id){
		for (int i= table.length -1; i>=0 ;--i){
			long entryNodeID = getContentAtPos(i);
			if (entryNodeID != id && canBeSuccessor(peerID, entryNodeID, id ))
				return entryNodeID;
		}
		return peerID;
	}
	
	public void print(){
		System.out.println("Predecessor:"+getPredecessorID());
		System.out.println("The Peer   :"+peerID);
		System.out.println("Successor  :"+getSuccessor());
		for (int i =0; i < table.length; ++i)
			System.out.println("FT["+i+"]="+table[i]);
	}
	public long getPeerID() {
		return peerID;
	}

	public void setPeerID(long peerID) {
		this.peerID = peerID;
	}
	public long getSuccessor() {
		return getContentAtPos(SUCCESSORIdx);
	}
	
	public long getPredecessorID() {
		return predecessorID;
	}
	public long lookup(long id){
		long succID = getContentAtPos(SUCCESSORIdx);
		if (id == succID || canBeSuccessor(getPeerID(),id,succID))
			return succID;
		else
			return findClosestPrecedingNode(id);
	}


	public static void main(String s[]) throws InvalidFingerTableEntry{
		FingerTable ft = new FingerTable(null,5, 4);
		ft.addEntry(8);
		ft.print();
/*		ft.addEntry(9);
		ft.addEntry(15);
		ft.addEntry(3);
		ft.addEntry(4);
		ft.print();
		long id =11;
		long r =-1;
		if (ft.inPeerRange(id))
			 r = ft.getPeerID();
		else
			 r = ft.succ(id);
		System.out.println("lookup for 10 ==>"+r);
*/	}
	
}
