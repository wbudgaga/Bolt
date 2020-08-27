package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.wireformates.GetSuccessor;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.Predecessor;
import mr.dht.peer2peernetwork.wireformates.QueryResult;
import mr.dht.peer2peernetwork.wireformates.Successor;

public class RemotePeer extends RNode{
	public RemotePeer(long id, String name, String host, int port, PacketChannel pChannel) throws IOException {
		super(id, name, host, port, pChannel);
	}

	public void validyChannel(PacketChannel pChannel) throws IOException{
		if (!pChannel.setRemotePeer(this))
			throw new IOException("New remote peer could created because there is already one for this channel");
	}
	public RemotePeer(PeerData nodeData, PacketChannel pChannel) throws IOException {
		super(nodeData, pChannel);
	}

	public static RemotePeer getInstance(PeerData nodeData, PacketChannel pChannel) throws IOException{
		return new RemotePeer(nodeData, pChannel);
	}	
	
	public static RemotePeer getInstance(PeerInfo peerInfo, PacketChannel pChannel)  throws IOException{
		return getInstance(peerInfo.getPeer(), pChannel);
	}

	public long lookup(long key, int respHandlerID, PeerData sourceNode) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException{
		Lookup lookupMSG = getLookupMSG(key, respHandlerID, sourceNode);
		if (Setting.PRINT_QUERY_MESSAGES)
			System.out.println("\nLookup query started for the key: "+ key +" and sent to the peer ID:"+getID());
		System.out.println("####################beforesendAFTER append a chunk : "+key+ "   [0]="+getID());
		sendMessage(lookupMSG);
		System.out.println("####################====AFTER append a chunk : "+key+ "   [0]="+getID());
		return lookupMSG.getMsgUUID(); 
	}
	public void forward(Lookup forward) throws IOException{
		if (Setting.PRINT_QUERY_MESSAGES){
		//	System.out.println("\nLookup query searching for the key: "+ forward.getQueryKey() +" is forwarded to peer ID: "+getID());
		}
		if (forward.getHops()>25){
			System.out.println("\nLookup could not be forwarded because the number of hops is :" +forward.getHops());
			return;
		}

		forward.incHop();
		sendMessage(forward);
	}
	
	public void setSuccessor(PeerData pData) throws IOException{
		Successor successorMSG = new Successor();
		successorMSG.setPeer(pData);
		sendMessage(successorMSG);
	}
	public void setPredecessor(PeerData pData) throws IOException{
		Predecessor predecessorMSG = new Predecessor();
		predecessorMSG.setPeer(pData);
		sendMessage(predecessorMSG);
	}
	
	public void setSuccessor1(PeerData pData) throws IOException{
		setPeerInfo(pData);
	}

	public void setPredecessor1(PeerData pData) throws IOException{
		setPeerInfo(pData);
	}

	public void setPeerInfo(PeerData pData) throws IOException{
		PeerInfo pInfo = new PeerInfo();
		pInfo.setPeer(pData);
		sendMessage(pInfo);
	}

	public void getSuccessor(PeerData pd) throws IOException{
		GetSuccessor succMSG = new GetSuccessor();
		succMSG.setPeer(pd);
		sendMessage(succMSG);
	}
	public void queryResult(long queryKey, long msgID, int handlerID, PeerData responsible) throws IOException{
		QueryResult queryResultMSG = new QueryResult();
		queryResultMSG.setPeer(getPeerInfo(responsible));
		queryResultMSG.setHandlerID(handlerID);
		queryResultMSG.setMsgUUID(msgID);
		queryResultMSG.setQueryKey(queryKey);
		queryResultMSG.setHandlerID(handlerID);
		sendMessage(queryResultMSG);
	}


/*
*/

/*	
		
	public void storeFile(Message msg) throws IOException{
		sendMessage(msg);
	}
*/
/*	public void getPredecessor(PeerData pd) throws IOException{
		GetPredecessor predecessorMSG = new GetPredecessor();
		predecessorMSG.setPeer(pd);
		sendMessage(predecessorMSG);
	}

	public void message(Message msg) throws IOException{
		sendMessage(msg);
	}
	
	public void getFT(PeerData localPeerData) throws IOException{
		FingerTableRequest ftRequest = new FingerTableRequest();
		ftRequest.setPeer(localPeerData);
		System.out.println("inside getFT method*****************************");
		sendMessage(ftRequest);
	}

	public void addNewPeer(NewPeer newPeer) throws IOException{
		sendMessage(newPeer);
	}

*/
	public static PeerInfo getPeerInfo(PeerData nodeData){
		PeerInfo peerInfo = new PeerInfo();
		peerInfo.setPeer(nodeData);
		return peerInfo;
	}
	
	public PeerInfo getPeerInfo() throws IOException{
		return getPeerInfo(nodeData);
	}
	
	public void print(){
		nodeData.print();
	}
}
