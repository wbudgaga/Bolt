package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.net.SocketException;
import java.nio.channels.SocketChannel;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import mr.communication.handlers.Acceptor;
import mr.communication.handlers.MultiMSGDecoder;
import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.DiscoveryMessageHandler;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;

public class Discovery extends LNode{
	private Set<Long> peerIDs 					= new	HashSet<Long>();
	private ConcurrentHashMap<PacketChannel, RemotePeer> peers 	= new	ConcurrentHashMap<PacketChannel, RemotePeer>();
	private boolean peerListChange 					= false;
	
	public Discovery(int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException{
		super(port);
	}		
	
	public PeerInfo[] getPeerInfoList() throws IOException{
		if (peers.size()<1)
			return null;
		PeerInfo[] peersList 					= new PeerInfo[peers.size()];
		int i							= 0;
		for(RemotePeer peer:peers.values()){
			peersList[i]					= peer.getPeerInfo();
			++i;
		}
		return peersList;
	}

	public PeerInfo[] getPeerInfoList(long peerID) throws IOException{
		if (peers.size() < 2)
			return null;
		PeerInfo[] peersList 					= new PeerInfo[peers.size()-1];
		int i							= 0;
		for(RemotePeer peer:peers.values())
			if (peer.getID() != peerID){
				peersList[i]				= peer.getPeerInfo();
				++i;
			}
		return peersList;
	}

	public synchronized RemotePeer getRandomPeer(){
		int size 						= peers.size();
		if(size == 0)
			return null;
		int idx 						= UtilClass.getRandomNumber(0, size -1);
		int c 							= 0;
		for (Map.Entry<PacketChannel, RemotePeer> peerEntry : peers.entrySet()){
			if (c==idx){
				return peerEntry.getValue();
			}
			++c;
		}
		return null;
	}
		
	public synchronized boolean storePeer(RemotePeer peer) {
		if (peerIDs.add(peer.getID())){
			peers.put(peer.getpChannel(), peer);
			peerListChange = true;
			return true;
		}		
		return false;
	}
	
	public synchronized boolean removePeer(PacketChannel  pChannel) {
		RemotePeer remotedPeer = peers.remove(pChannel);
		if (remotedPeer!=null){
			peerIDs.remove(remotedPeer.getID());
			peerListChange = true;
			return true;
		}		
		return false;
	}


	public boolean startup() throws InstantiationException, IllegalAccessException, IOException, InterruptedException, ClassNotFoundException{
		// Discovery main loop
		while(true){
			Thread.sleep(Setting.DISCOVERY_UPDATE_TIME);
			if (peerListChange){
				print();
				peerListChange =false;
			}
		}
	}
	@Override
	public void socketConnected(Acceptor acceptor, SocketChannel sc) {
		System.out.println("["+ acceptor + "] connection received: " + sc);
		try {
	      sc.socket().setReceiveBufferSize(2*1024);
	      sc.socket().setSendBufferSize(2*1024);
	      // The contructor enables reading automatically.
	      PacketChannel pc = new PacketChannel(sc, selector, new MultiMSGDecoder(), new DiscoveryMessageHandler(this));
	      pc.resumeReading();
		} catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		      e.printStackTrace();
	    }
	}

	@Override
	public void socketError(Acceptor acceptor, Exception ex) {
		System.out.println("["+ acceptor + "] Error: " + ex.getMessage());
	}	
	
	public void print(){
		PeerData.printHeader();
		for(RemotePeer peer:peers.values())
			peer.print();
		PeerData.printRowSeparator();
	}
	
	
	@Override
	public void socketException(PacketChannel pc, Exception ex) {
		System.out.println("[" + pc.toString() + "] Error: " + ex.getMessage());
		removePeer(pc);
	}
	@Override
	public void socketDisconnected(PacketChannel pc) {
		RemotePeer rp =  peers.get(pc);
		if (rp==null)
			System.out.println("[" + pc.toString() + "] Disconnected.");
		else
			System.out.println("[" + rp.getID() + "] Disconnected.");
		removePeer(pc);
	}	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException{	      
		if (args.length < 1) {
			System.err.println("Discovery Node:  Usage:");
			System.err.println("         mr.dht.peer2peernetwork.nodes.Discovery portnum");
		    return;
		}
		try{
			int port = Integer.parseInt(args[0]);
			 Discovery discovery = new Discovery(port);
			discovery.startup();
		}catch(NumberFormatException e){
			System.err.println("Discovery Node: the values of portnum must be integer");
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
