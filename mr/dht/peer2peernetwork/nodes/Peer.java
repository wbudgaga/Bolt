package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import mr.communication.handlers.Acceptor;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.fingertable.FTManager;
import mr.dht.peer2peernetwork.handlers.PeerMessageHandler;
import mr.dht.peer2peernetwork.handlers.connection.Connecting4QueryResultsHandler;
import mr.dht.peer2peernetwork.handlers.connection.Connecting4ReducerPeerHandler;
import mr.dht.peer2peernetwork.handlers.connection.NewPeerNotifyingHandler;
import mr.dht.peer2peernetwork.handlers.connection.PeerConnectingHandler;
import mr.dht.peer2peernetwork.handlers.connection.RegisteringHandler;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;
import mr.resourcemanagement.execution.mrtasks.management.ResourceManager;

public class Peer extends LNode{
	private FTManager 	ftManager;
	private ResourceManager resourceManager;
	private boolean alreadyAskeForFT						= false;
	private ConcurrentHashMap<String, PacketChannel> pendingAcceptedConnections 	= new ConcurrentHashMap<String, PacketChannel>();

	public Peer(String name,long id, int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		super(name, id, port);
	}
	
	public boolean inPeerRange(long id){
		return ftManager.inPeerRange(id);
	}

	public RemotePeer getResponsiblePeer(long key) throws IOException{
		if (key >= Setting.RING_KEYSPACE)
			key 								= key % Setting.RING_KEYSPACE;	
		return ftManager.lookup(key);//null means the local peer is responsible for a given key
	}
	
	public RemotePeer getQueryPeer(Lookup lookupMSG) throws IOException{
		PeerData srcPeerData 							= lookupMSG.getSourcePeer().getPeer();
		return ftManager.getPeer(srcPeerData.getPeerID());
	}

	public void cachePeer(RemotePeer rp){
		ftManager.storePeer(rp);
	}
	
	public void lookup(Lookup lookupMSG) throws IOException{
		RemotePeer remotePeer 							= getResponsiblePeer(lookupMSG.getQueryKey());
		if (remotePeer == null){//null means the local peer is responsible for a given key
			RemotePeer srcPeer 						= getQueryPeer(lookupMSG);
			if(srcPeer == null){
				PeerData srcPeerData 					= lookupMSG.getSourcePeer().getPeer();
				initiateConnectionManager(srcPeerData.getHost(),srcPeerData.getPortNum(), new Connecting4QueryResultsHandler(this, lookupMSG));
			}else
				srcPeer.queryResult(lookupMSG.getQueryKey(), lookupMSG.getMsgUUID(), lookupMSG.getSrcPeerHandlerID(), getNodeData());
		}else
			remotePeer.forward(lookupMSG);
	}
	
	public void setPredecessor(RemotePeer remotePeer) throws IOException, InvalidFingerTableEntry {
		if (remotePeer	!= null)
			ftManager.setPredecessor(remotePeer);//updatePredecessor(remotePeer);
	}

	public RemotePeer getPredecessor() {
		return ftManager.getPredecessor();
	}
	public RemotePeer getSuccessor() {
		return ftManager.getSuccessor();
	}
	
	public PeerInfo[]  getAllRemotePeersInfo() throws IOException{
		HashMap<Long, RemotePeer> list 						= ftManager.getAllRemotePeers();
		int 		size 							= list.size();
		PeerInfo[] 	peerInfoList 						= new PeerInfo[size];
		int i = 0;
		for (Map.Entry<Long, RemotePeer> entry : list.entrySet()) {
			peerInfoList[i]= entry.getValue().getPeerInfo();
			++i;
		}
		return peerInfoList;
	}

	public void handleQueryResult(long queryKey, RemotePeer rp) throws InvalidFingerTableEntry, IOException{
		addNewPeer(rp);
		if (alreadyAskeForFT == false){
			alreadyAskeForFT 						= true;
			System.out.println("ASKING FOR FT ..............................................");
//			ftManager.getSuccessor().getFT(getNodeData());
		}
	}

	public void setSuccessor(RemotePeer remotePeer) throws IOException, InvalidFingerTableEntry {
		if (remotePeer!=null)
			ftManager.setSuccessor(remotePeer);
	}

	public void print(){
		PeerData.printHeader();
		getNodeData().print();
		PeerData.printRowSeparator();
	}

	public void printFT(){
		ftManager.print();
	}
	
	public void notifyRemotePeer(PeerData peer) throws IOException, InvalidFingerTableEntry{
		initiateConnectionManager(peer.getHost(),peer.getPortNum(), new NewPeerNotifyingHandler(this,peer));
	}

	public void addNewPeer(RemotePeer peer) throws IOException, InvalidFingerTableEntry{
		if (peer.getID() != getNodeData().getPeerID())
			if (ftManager.addNewPeer(peer))
				ftManager.print();
	}
	
	public ResourceManager getResourceManager(){
		return resourceManager;
	}
	
	public void startup() throws Exception{
		ftManager 		= new FTManager(this, Setting.NUMBER_OF_FT_ENTRIES);
		resourceManager = new ResourceManager(this);
		resourceManager.start();
		initiateConnectionManager(Setting.DISCOVER_HOST,Setting.DISCOVER_PORT, new RegisteringHandler(this));
	}
	

	@Override
	public void socketConnected(Acceptor acceptor, SocketChannel sc) {
	//	System.out.println("["+ acceptor + "] connection received: " + sc);
		try {
			PacketChannel pc = new PacketChannel(sc, selector, new MultiMSGDecoder(), new PeerMessageHandler(this));
			pendingAcceptedConnections.putIfAbsent(sc.socket().getInetAddress().toString(), pc);
			pc.resumeReading();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		      e.printStackTrace();
	    }
	}
	
	@Override
	public void socketError(Acceptor acceptor, Exception ex) {
		System.out.println("["+ acceptor + "] Error: " + ex.getMessage());
	}

	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException {
		Peer peer;
		int 	port ;
		String 	nickName 					= args[1];
		long    peerID;
		//System.out.println(Setting.FLUSH_PEER_DATA+"#############" +args.length+"   "+(Setting.FLUSH_PEER_DATA==false || args.length==0));
		Setting.setPeerFolder(nickName.split("_")[1]);
		if (Setting.FLUSH_PEER_DATA==false){
			PeerInfo pi 					= Peer.loadPeerData();
			port 						= pi.getPeer().getPortNum();
			nickName 					= pi.getPeer().getNickName();
			peerID 						= pi.getPeer().getPeerID();
		}else{
			if (args.length < 2) {
				System.err.println("Peer Node:  Usage:");
				System.err.println("         java mr.dht.peer2peernetwork.nodes.Peer portnum nickname [ID]");
			    return;
			}			
			port 						= Integer.parseInt(args[0]);
			peerID						= Long.parseLong(args[2]);
		}
		try{
			
			peer 						= new Peer(nickName,peerID,port);
			if (Setting.FLUSH_PEER_DATA)
				peer.flushPeerData();
			
			peer.startup();
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void flushPeerData() throws IOException{
		PeerInfo pi 						= new PeerInfo();
		pi.setPeer(getNodeData());
		UtilClass.flushData(pi, Setting.DATA_DIR + "setting.txt");
	}
	private static PeerInfo loadPeerData() throws IOException{
		PeerInfo pi 						= new PeerInfo();
		UtilClass.loadData(pi, Setting.DATA_DIR	+ "setting.txt");
		return pi;
	}

	@Override
	public void socketException(PacketChannel pc, Exception ex) {
		System.out.println("Exception is thrown on the connection from " + Setting.HOSTNAME + " RemotePeer: " + pc + "\n Exception:" + ex.getMessage());
		System.exit(-1);
	}

	@Override
	public void socketDisconnected(PacketChannel pc) {
		//.out.println("Connection is disconnected betweem me("+Setting.HOSTNAME+" and  " +pc);		
	}
}
