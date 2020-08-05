package mr.dht.peer2peernetwork.handlers.client_handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;
import mr.resourcemanagement.execution.mrtasks.management.CopyAndForwardTask;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;

public class FileMetaDataHandler extends MessageHandler{
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			((Client) node).setFileMetaData((FileMetaData)msg);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public int getHandlerID() {
		return Message.FILE_META;
	}
}
