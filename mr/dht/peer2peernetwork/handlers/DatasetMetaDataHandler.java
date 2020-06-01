package mr.dht.peer2peernetwork.handlers;


import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;
import mr.resourcemanagement.execution.mrtasks.management.CopyAndForwardTask;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;

public class DatasetMetaDataHandler extends MessageHandler{
	
	public void handleDatasetMetaData(PacketChannel pc, DatasetMetaData metaData) {
		System.out.println(" dataSet>> " + metaData.getDataSetHashKey());
		System.out.println(metaData.getFileNameList());
		System.out.println(metaData.getFileSizeList());
		Peer lp = (Peer) node; 
		MetaDataManager metaMGR = lp.getResourceManager().getMetadataManager();
		metaMGR.addDataset(metaData.getDataSetHashKey(), metaData);
		try {
			metaMGR.flushDatasets();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleDatasetMetaData(pc, (DatasetMetaData)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.DATASET_META;
	}

}
