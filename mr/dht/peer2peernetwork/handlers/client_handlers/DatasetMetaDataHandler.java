package mr.dht.peer2peernetwork.handlers.client_handlers;

import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.handlers.MessageHandler;
import mr.dht.peer2peernetwork.nodes.Client;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;

public class DatasetMetaDataHandler extends MessageHandler{
	@Override
	public void handle(PacketChannel pc, Message msg) {
		try {
			((Client) node).setDatasetMetaData((DatasetMetaData)msg);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	@Override
	public int getHandlerID() {
		return Message.DATASET_META;
	}
}
