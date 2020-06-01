package mr.dht.peer2peernetwork.handlers;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.wireformates.Lookup;
import mr.dht.peer2peernetwork.wireformates.Message;

public class ForwardHandler extends LookupHandler{	
	@Override
	public void handle(PacketChannel pc, Message msg) {
		Lookup forward = (Lookup) msg;
		if (forward.getSourcePeer().getPeer().getPeerID() == getNode().getID() || forward.getHops() > Setting.NUMBER_OF_FT_ENTRIES ){
			System.out.println(forward.getSourcePeer().getPeer().getPeerID() +"There is something wrong in handling forward MSG"+forward.getHops()+"   "+getNode().getID());
			return;
		}
		lookup(forward);
	}

	@Override
	public int getHandlerID() {
		return Message.FORWARD;
	}
}
