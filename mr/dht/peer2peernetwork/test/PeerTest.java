package mr.dht.peer2peernetwork.test;

import java.io.IOException;
import java.net.SocketException;

import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.CommandLineInterface;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemoteDiscovery;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.util.CommandLineThread;
import mr.dht.peer2peernetwork.util.UtilClass;


public class PeerTest implements CommandLineInterface{
	private Peer 			p;
	private RemoteDiscovery remoteDiscovery;

	public PeerTest(String name,int id, int port) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException {
		p 						= new Peer(name, id, port);
		//p.startup();		
	}
		
	public void lookup(long key){
		RemotePeer rp= p.lookup(key);
		System.out.println("key: "+key+ " has been found at "+rp.getID());
	}

	public void print(){
		PeerData.printHeader();
		p.getNodeData().print();
		PeerData.printRowSeparator();
	}
		
	public void addNewPeer() throws IOException, InvalidFingerTableEntry{
		long id 					= UtilClass.getRandomNumber(0, 16);
		PeerData pd 					= new PeerData();
		pd.setHost("host_"+id);
		pd.setPeerID(id);
		pd.setPortNum((int)id +123);
		pd.setNickName("name_"+id);
		RemotePeer rp 					= RemotePeer.getInstance(pd);
		p.setSuccessor(rp);
	}
	
	public static void main(String args[]) throws InstantiationException, IllegalAccessException, IOException, ClassNotFoundException, InvalidFingerTableEntry {
		PeerTest peer;
		try{
			int 	port 				= 5;
			String 	nickName 			= "mainTest";
			peer = new PeerTest(nickName,5,port);
			CommandLineThread cl = new CommandLineThread(peer);
			cl.start();
/*			peer.addNewPeer();
			peer.print();
			peer.addNewPeer();
*/
		}catch(NumberFormatException e){
			System.err.println("Peer Node: the values of portnum must be integer");
		} catch (SocketException e) {
			System.err.println(e.getMessage());
		}
	}

	@Override
	public void sendData(String srcFile) {}
	@Override
	public void send() {}
	public void createSearch() {}
	@Override
	public void search(String word1, String word2) {}
	
	@Override
	public void listFiles() {
		//UtilClass.listFiles(Setting.LOCAL_DIR);
	}

	@Override
	public void printFT() {
		// TODO Auto-generated method stub
		
	}

}
