package mr.dht.peer2peernetwork.handlers;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.Predecessor;

public abstract class LocalMessageHandler {
	private Map<Integer,MessageHandler> messageHandlers 		= new HashMap<Integer,MessageHandler>(); 
	protected LNode node;

	public LocalMessageHandler(LNode node) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		this.node 						= node;
		loadMessageHandlers();
	}
	
	protected void createMessageHandlerObject(Class<MessageHandler> handlerClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		MessageHandler msgHandler 				= (MessageHandler) handlerClass.newInstance();
		msgHandler.setNode(node);
		addMessageHandler(msgHandler.getHandlerID(), msgHandler);
	}
	
	public void handle(PacketChannel pc, Message msg) throws InstantiationException, IllegalAccessException{
		MessageHandler handler = getMessageHandler(msg.getHandlerID());
		if (handler == null){
			System.out.println("There is no message handler for message-id "+msg.getMessageID());
			return;
		}
		handler.handle(pc, msg);
	}

	protected void addMessageHandler(Integer handlerID, MessageHandler msgHandler){
		messageHandlers.put(handlerID, msgHandler);
	}
	
	protected MessageHandler getMessageHandler(Integer handlerID){
		return messageHandlers.get(handlerID);
	}
	/////////////////////////////////////////////////////////////
	  public void socketException(PacketChannel pc, Exception ex) {
		  node.socketException(pc, ex);
	  }

	  public void socketDisconnected(PacketChannel pc) {
	    node.socketDisconnected(pc);
	   // connectionClosed();
	  }
		  
	  public void packetSent(PacketChannel pc, ByteBuffer pckt) {//##########################################################
	    try {
	      pc.resumeReading();
	    } catch (Exception e) {    
	      e.printStackTrace();
	    }
	  }
	  public void packetArrived(PacketChannel pc, Message msg) {
		  try {
			handle(pc, msg);
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		//Predecessor msg1 = (Predecessor) msg;
		//System.out.println(" msgdddddddddddddddddd1: "+msg1.getPeer().getAddress()+"  "+ msg1.getPeer().getPeerID()+"  "+msg1.getPeer().getNickName());
	  }
	  
	/////////////////////////////////////////////////////////////
	  
	protected abstract void loadMessageHandlers() throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
