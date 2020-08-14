package mr.dht.peer2peernetwork.handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Discovery;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.PeerInfo;
import mr.dht.peer2peernetwork.wireformates.PeersList;
import mr.dht.peer2peernetwork.wireformates.RegisterRequest;
import mr.dht.peer2peernetwork.wireformates.RegisterResponse;

public class RegisterRequestHandler extends MessageHandler{
	private  Discovery getNode(){
		return (Discovery) node;
	}
	
	private void sendRegisterResonse(PacketChannel pc,byte registerStatus,String info) throws IOException  {
		RegisterResponse message 			= new RegisterResponse();
		message.setStatusCode(registerStatus);
		message.setAdditionalInfo(info);
		sendMessage(pc, message);
	}

	public synchronized void handleRegisterRequest(PacketChannel pc, RegisterRequest message) {
		try{
			RemotePeer newPeer			= RemotePeer.getInstance(message.getPeer(), pc);
			Discovery discovery 			= getNode();

			if (discovery.storePeer(newPeer)){
				sendRegisterResonse(pc,Message.SUCCEESS,"");
				System.out.println("Peer registration accepted " + newPeer.getID());
				PeerInfo[] allPeersInfo = getNode().getPeerInfoList(newPeer.getID());
				if (allPeersInfo != null)
					sendFingerTableResponse(pc, allPeersInfo);
			}
			else{
				System.out.println("Peer registration not accepted " + newPeer.getID());
				sendRegisterResonse(pc,Message.FAILURE," Peer with the same ID is already registered");
			}
		}catch(IOException e){
			e.printStackTrace();
		}
	}
		
	private void sendFingerTableResponse(PacketChannel pc, PeerInfo[] allPeersInfo) throws IOException{
		PeersList peersListMsg 				= new PeersList();
		peersListMsg.setPeerList(allPeersInfo);
		sendMessage(pc, peersListMsg);
	}
	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleRegisterRequest(pc, (RegisterRequest)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.REGISTER_REQUEST;
	}
}
