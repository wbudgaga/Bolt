package mr.dht.peer2peernetwork.test;

import mr.dht.peer2peernetwork.handlers.LocalMessageHandler;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.Predecessor;
import mr.dht.peer2peernetwork.wireformates.RegisterRequest;
import mr.dht.peer2peernetwork.wireformates.RegisterResponse;
import mr.dht.peer2peernetwork.wireformates.Request;

public class WireformatesTest {
	public static Message getMSG(byte[] msg) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		MessageFactory mf 		= MessageFactory.getInstance();
		return mf.createMessage(msg);
	}
	
	public static PeerData getPeerData(long id,String n, String host, int port) throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		PeerData pd 			= new PeerData();
		pd.setHost(host);
		pd.setNickName(n);
		pd.setPeerID(id);
		pd.setPortNum(port);
		return pd;
	}

	public static void testPeerInfoHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		PeerInfo msg 			= new PeerInfo();
		msg.setPeer(getPeerData(876519041972L,"Walid S.","red-rock",  1093 ));	
		byte[] bytes 			= msg.packMessage();
		System.out.println(msg.getMessageID() + "  " + msg.getHandlerID() + "  " + msg.getPeer().getPeerID() + "  " + msg.getPeer().getNickName() + "  " + msg.getPeer().getHost() + "  " + msg.getPeer().getPortNum() + "  ");
		
		PeerInfo msg1 			= (PeerInfo) getMSG(bytes);
		LocalMessageHandler handler 	= new LocalMessageHandler(null);
		handler.handle(null, msg1);
		
	}

	public static void testRegisterRequestHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		RegisterRequest msg 		= new RegisterRequest();
		msg.setPeer(getPeerData(876519041972L,"Walid S.","red-rock",  1093 ));	
		byte[] bytes 			= msg.packMessage();
		System.out.println(msg.getMessageID() + "  " + msg.getHandlerID() + "  " + msg.getPeer().getPeer().getPeerID() + "  " + msg.getPeer().getPeer().getNickName() + "  " + msg.getPeer().getPeer().getHost() + "  " + msg.getPeer().getPeer().getPortNum() + "  ");
		
		RegisterRequest msg1 		= (RegisterRequest) getMSG(bytes);
		LocalMessageHandler handler 	= new LocalMessageHandler(null);
		handler.handle(null, msg1);
	}

	public static void testRegisterResponseHandler() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		RegisterResponse msg = new RegisterResponse();
		msg.setAdditionalInfo(" 876519041972L,Walid S.,red-rock,  1093 ");
		msg.setStatusCode(Message.SUCCEESS);
		byte[] bytes = msg.packMessage();
		System.out.println(msg.getMessageID()+"  "+msg.getHandlerID()+"  "+ msg.getStatusCode()+"  "+msg.getAdditionalInfo());
		
		RegisterResponse msg1 = (RegisterResponse) getMSG(bytes);
		LocalMessageHandler handler = new LocalMessageHandler(null);
		handler.handle(null, msg1);
	}

	public static void testPeerInfo() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		PeerInfo msg = new PeerInfo();
		msg.setPeer(getPeerData(876519041972L,"Walid S.","red-rock",  1093 ));	
		byte[] bytes = msg.packMessage();
		PeerInfo msg1 = (PeerInfo) getMSG(bytes);
		System.out.println(msg.getMessageID()+"  "+msg.getHandlerID()+"  "+ msg.getPeer().getPeerID()+"  "+msg.getPeer().getNickName()+"  "+msg.getPeer().getHost()+"  "+msg.getPeer().getPortNum()+"  ");
		System.out.println(msg1.getMessageID()+"  "+msg1.getHandlerID()+"  "+ msg1.getPeer().getPeerID()+"  "+msg1.getPeer().getNickName()+"  "+msg1.getPeer().getHost()+"  "+msg1.getPeer().getPortNum()+"  ");
	}
	
	public static void testPredecessor() throws InstantiationException, IllegalAccessException, ClassNotFoundException{
		Predecessor msg = new Predecessor();
		msg.setPeer(getPeerData(876519041972L,"Walid S.","red-rock",  173 ));	
		byte[] bytes = msg.packMessage();
		Predecessor msg1 = (Predecessor) getMSG(bytes);
		System.out.println(msg.getMessageID()+"  "+msg.getHandlerID()+"  "+ msg.getPeer().getPeerID()+"  "+msg.getPeer().getNickName()+"  "+msg.getPeer().getHost()+"  "+msg.getPeer().getPortNum()+"  ");
		System.out.println(msg1.getMessageID()+"  "+msg1.getHandlerID()+"  "+ msg1.getPeer().getPeerID()+"  "+msg1.getPeer().getNickName()+"  "+msg1.getPeer().getHost()+"  "+msg1.getPeer().getPortNum()+"  ");
	}

	/**
	 * @param args
	 * @throws ClassNotFoundException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 */
	public static void main(String[] args) throws InstantiationException, IllegalAccessException, ClassNotFoundException {
		//testPeerInfo();
		//testPeerInfoHandler();
		//testRegisterRequestHandler();
		testRegisterResponseHandler();
		//testPredecessor();

	}

}
