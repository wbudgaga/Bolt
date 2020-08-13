package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;
import java.net.Socket;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.exceptions.InvalidFingerTableEntry;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.QueryResult;

public class QueryResultHandler extends MessageHandler{
	public Peer getNode(){
		return (Peer) node;
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		QueryResult queryResult 		= (QueryResult) msg;
		try {
			if (queryResult.getPeer() != null){
				RemotePeer remotePeer 	= RemotePeer.getInstance(queryResult.getPeer(),pc);
				getNode().handleQueryResult(queryResult.getQueryKey(), remotePeer);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InvalidFingerTableEntry e) {
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.QUERY_RESULT;
	}
}
