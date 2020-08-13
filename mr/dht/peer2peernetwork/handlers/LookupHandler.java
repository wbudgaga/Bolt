package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;

public class LookupHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
/*	
	protected void lookup(Lookup msg){
		try {
			if (Setting.PRINT_QUERY_MESSAGES)
				System.out.println("\nLookup query searching for the key: "+ msg.getQueryKey() +" is received." +
					"\nThe source of the query is the node:"+msg.getSourcePeer().getPeer().getPeerID()+" and # Of Hops:"+msg.getHops());
	
			RemotePeer remotePeer = getNode().lookup(msg.getQueryKey());
			if (remotePeer!= null)
				if(remotePeer.getID()== node.getID()){
					remotePeer.queryResult(msg.getQueryKey(), msg.getMsgUUID(), msg.getSrcPeerHandlerID(), msg.getSourcePeer().getPeer());
				}else{
					msg.setHandlerID(Message.FORWARD);
					remotePeer.forward(msg);
				}
			return remotePeer;
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
*/	
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		Lookup lookupMSG 		=  (Lookup)msg;
		try {
			getNode().lookup(lookupMSG);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.LOOKUP;
	}
}
