package mr.dht.peer2peernetwork.handlers;

import java.util.ArrayList;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.GetDatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;

public class GetDatasetMetaDataHandler extends MessageHandler{
	private  Peer getNode(){
		return (Peer) node;
	}
	
	public  void handleGetAllDatasetMetaData(PacketChannel pc, MetaDataManager metaMGR) {
		ArrayList<DatasetMetaData> metaList 		= metaMGR.getAllDatasets();
		for (DatasetMetaData metaData:metaList)
			sendMessage(pc,metaData);

	}
	public synchronized void handleGetDatasetMetaData(PacketChannel pc, GetDatasetMetaData message) {
		MetaDataManager metaMGR = getNode().getResourceManager().getMetadataManager();
		if (message.getHashedKey()==-1)
			handleGetAllDatasetMetaData(pc, metaMGR);
		else
			sendMessage(pc,metaMGR.getDataset(message.getHashedKey()));
	}
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleGetDatasetMetaData(pc, (GetDatasetMetaData)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.GET_DATASET_METADATA;
	}
}
