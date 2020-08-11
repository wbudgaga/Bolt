package mr.dht.peer2peernetwork.handlers;


import java.io.IOException;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;
import mr.resourcemanagement.execution.mrtasks.management.CopyAndForwardTask;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;

public class FileMetaDataHandler extends MessageHandler{
	
	public void handleDatasetMetaData(PacketChannel pc, FileMetaData metaData) {
		System.out.println(" file>> " + metaData.getFileHashedKey()+ "  "+metaData.getFileName());
		Peer lp 				= (Peer) node; 
		MetaDataManager metaMGR = lp.getResourceManager().getMetadataManager();
		metaMGR.addFile(metaData.getFileHashedKey(), metaData);
		try {
			metaMGR.flushFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	

	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleDatasetMetaData(pc, (FileMetaData)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.FILE_META;
	}
}
