package mr.dht.peer2peernetwork.handlers.client_handlers;

import java.io.IOException;
import java.net.Socket;

import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.QueryResult;

public class SubmitDataQResultHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	public void handleQueryResult(Socket link, QueryResult queryResult) {

	}	
	
	@Override
	public void handle(Socket link, Message msg) {
		QueryResult queryResult 		= (QueryResult) msg;
		System.out.println("     ############SubmitDataQResultHandler########### searchKey:"+queryResult.getQueryKey()+"   fund peer:"+queryResult.getPeer().getPeer().getPeerID());
	}

	@Override
	public int getHandlerID() {
		return Message.SUBMITDATA_QResult;
	}
}
