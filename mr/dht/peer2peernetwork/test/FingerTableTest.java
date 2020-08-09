package mr.dht.peer2peernetwork.test;

import java.io.IOException;

import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.fingertable.FTManager;
import mr.dht.peer2peernetwork.fingertable.FingerTable;
import mr.dht.peer2peernetwork.handlers.LocalMessageHandler;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;

public class FingerTableTest {
	
	public static Message getMSG(byte[] msg) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		MessageFactory mf 			= MessageFactory.getInstance();
		return mf.createMessage(msg);
	}
	
	public static PeerData getPeerData(long id,String name, String host, int port) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		PeerData pd 				= new PeerData();
		pd.setHost(host);
		pd.setNickName(name);
		pd.setPeerID(id);
		pd.setPortNum(port);
		return pd;
	}

	public static void testAddFT() throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvalidFingerTableEntry, IOException{
		Peer p 					= new Peer("test", 5, 0);
		PeerData p1 				= getPeerData(8,"test1","host1", 1);
		PeerData p2 				= getPeerData(13,"test2","host2", 2);
		RemotePeer rp 				= RemotePeer.getInstance(p1);
		RemotePeer rp1 				= RemotePeer.getInstance(p2);
		FTManager ftm 				= new FTManager(p, 4);
		
		ftm.addNewPeer(rp);
		ftm.print();
		ftm.addNewPeer(rp1);
		ftm.print();		

	}

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws InvalidFingerTableEntry 
	 * @throws IOException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvalidFingerTableEntry, IOException {
		//testPeerInfo();
		//testPeerInfoHandler();
		//testRegisterRequestHandler();
		//testRegisterResponseHandler();
		testAddFT();
		//testPredecessor();

	}

}
