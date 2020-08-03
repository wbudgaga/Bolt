package mr.dht.peer2peernetwork.fingertable;

import java.io.IOException;
import java.util.HashMap;

import mr.dht.peer2peernetwork.datastructure.CircularPeerQueue;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;


public class FTManager {
	private FingerTable 		ft;
	private Peer 		  	localPeer;
	private FTFixerThread 		ftFixer;
	private CircularPeerQueue 	peerQueue; 
	private int 			ftSize;
	
	public FTManager(Peer peer, int fingerTableSize){
		createFT(peer.getID(), fingerTableSize);
		peerQueue			= new CircularPeerQueue(Setting.PEER_CACHE_SIZE);
		this.localPeer 			= peer;
	}
	
	private void createFT(long peerID, int fingerTableSize){
		this.ft	 			= new FingerTable(this,peerID, fingerTableSize);
		ftSize				= fingerTableSize;
//		ftFixer 			= new FTFixerThread(this);
	//	ftFixer.start();
	}
	
	public boolean inPeerRange(long id){
		return ft.inPeerRange(id % Setting.RING_KEYSPACE);
	}
	
	public RemotePeer lookup(long id){
		if (inPeerRange(id))
			return null;
		else
			return getPeer(ft.lookup(id % Setting.RING_KEYSPACE));
	}

	public synchronized boolean addNewPeer(RemotePeer newPeer) throws InvalidFingerTableEntry, IOException {
/*		if (peerQueue.contains(newPeer.getID()))
			return false;//throwing an exception
*/		return  tryToAddNewPeer(newPeer);
	}

	private boolean tryToAddNewPeer(RemotePeer newPeer)throws InvalidFingerTableEntry, IOException{
		RemotePeer oldPredecessor 	= getPeer(ft.getPredecessorID());
		boolean predUpdateStatus 	= ft.updatePredecessor(newPeer.getID());
		if (predUpdateStatus)
			predecessorChangeNotification(oldPredecessor, newPeer);

		boolean succUpdateStatus 	= tryToUpdateSucc(newPeer);
		
		if (predUpdateStatus || succUpdateStatus){
			storePeer(newPeer);
			return true;
		}
		//Node.getThreadPoolManager().addTask(new FileTransporterTask(this));
		return false;
	}
	
	private void predecessorChangeNotification(RemotePeer oldPre, RemotePeer newPre) throws IOException{
		System.out.println("Predecessor has been changed");
		//tiger a process to let the new peer to change its predecessor to cur node 
		if (oldPre != null){//inform the old predecessor about the new successor 
			newPre.setPredecessor1(oldPre.getNodeData());
			oldPre.setSuccessor1(newPre.getNodeData());
		}
		newPre.setSuccessor(localPeer.getNodeData());
	}
	
	public void setSuccessor(RemotePeer remotePeer) throws IOException, InvalidFingerTableEntry {
		if (ft.getSuccessor() == remotePeer.getID())
			return;
		if (tryToUpdateSucc(remotePeer))
			print();
	}
	
	private boolean tryToUpdateSucc(RemotePeer newPeer)throws InvalidFingerTableEntry, IOException{
		long oldSuccID 			= ft.getContentAtPos(FingerTable.SUCCESSORIdx);
		if (tryToUpdateFTEnteries(newPeer)){
			storePeer(newPeer);
			if (oldSuccID != ft.getSuccessor()){
				successorChangeNotification(getPeer(oldSuccID), newPeer);
			}
			return true;
		}
		return false;
	}
	
	private void successorChangeNotification(RemotePeer oldSucc, RemotePeer newSucc) throws IOException{
		System.out.println("Successor has been changed");
		//tiger a process to let the new peer to change its predecessor to cur node 
		newSucc.setPredecessor(localPeer.getNodeData());
		if (oldSucc != null){
			newSucc.setSuccessor1(oldSucc.getNodeData());
			//oldSucc.setPredecessor(newSucc.getNodeData());
		}
	}

/////////////////////////////////////////////////////////////
	public HashMap<Long, RemotePeer> getAllRemotePeers(){
		HashMap<Long, RemotePeer> peersList = new HashMap<Long, RemotePeer>();
		for (int i=0; i<ftSize; ++i){
			long peerID = ft.getContentAtPos(i);
			if (!peersList.containsKey(peerID) && peerID != localPeer.getID())
				peersList.put(peerID, peerQueue.getPeer(peerID));
		}
		peerQueue.getCachedPeers(peersList);
		return peersList;
	}

	protected void confirmDirectNeighbors() throws IOException{
		RemotePeer succ = getSuccessor();
/*		if (succ != null)
			succ.getPredecessor(localPeer.getNodeData());
*//*		RemotePeer pred = getPredecessor();
		if (pred != null)
			pred.getSuccessor(localPeer.getNodeData());
*/
	}
	
/*	// check the local responsibly and update the others so that this node will be updated with chord changes
	public synchronized void updateTableEntries(int index) throws IOException{
		if (index < ft.length){
			long remotePeerID = ft.getContentAtPos(index);
			if (ft.inPeerRange(remotePeerID))
				ft.insertContentAtPos(index, localPeer.getID());
			else{
				ft.succ(remotePeerID).lookup(entryPeerID,peer.getNodeData());
				updateTableEntries(index+1);
			}
		}
	}
	
*/	
	
	public synchronized boolean storePeer(RemotePeer peer) {
		peerQueue.add(peer.getID(), peer);
		return true;
	}
	public synchronized RemotePeer getPeer(long peerID) {
		return peerQueue.getPeer(peerID);
	}

	protected boolean  isHealthyPeer(long peerID){
		if (getHealthyPeer(peerID)==null)
			return false;
		return true;
	}
	private RemotePeer getHealthyPeer(long peerID){
		RemotePeer peer = peerQueue.getPeer(peerID);
		if (peer != null && !peer.isAlive()){
			removePeer(peerID);
			return null;
		}	
		return  peer;
	}
	
	private void removePeer(long peerID){
		peerQueue.backup(peerID);
	}
	
	private boolean tryToUpdateFTEnteries(RemotePeer newPeer) throws InvalidFingerTableEntry{
		int indexOfAddedElement = ft.addEntry(newPeer.getID());
		if (indexOfAddedElement == FingerTable.NONE)
			return false;
		return true;
	}
	
	public void setPredecessor(RemotePeer remotePeer) throws IOException, InvalidFingerTableEntry {
		if (ft.getPredecessorID()== remotePeer.getID())
			return;
		if (tryToAddNewPeer(remotePeer))
			print();
	}

	public RemotePeer getSuccessor(){
		return peerQueue.getPeer(ft.getSuccessor());
	}
	public RemotePeer getPredecessor(){
		return peerQueue.getPeer(ft.getPredecessorID());
	}

	public FingerTable getFt() {
		return ft;
	}
	public void print() {
		ft.print();
	}

	public void setFt(FingerTable ft) {
		this.ft = ft;
	}
/*	public static void main(String s[]) throws InvalidFingerTableEntry, ClassNotFoundException, InstantiationException, IllegalAccessException{
		Peer p = new Peer("test", 5, 0);
		
		FTManager ftm = new FTManager(p, 4);
		
		FingerTable ft = ftm.getFt();
		ft.print();
		
	}
*/}
