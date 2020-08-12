package mr.dht.peer2peernetwork.handlers;

import java.util.ArrayList;

import mr.communication.handlers.PacketChannel;
import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.wireformates.DatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.FileMetaData;
import mr.dht.peer2peernetwork.wireformates.GetDatasetMetaData;
import mr.dht.peer2peernetwork.wireformates.GetFileMetaData;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.resourcemanagement.execution.mrtasks.management.MetaDataManager;

public class GetFileMetaDataHandler extends MessageHandler{
	private  Peer getNode(){
		return (Peer) node;
	}
	
	public  void handleGetAllFilesMetaData(PacketChannel pc, MetaDataManager metaMGR) {
		ArrayList<FileMetaData> metaList 		= metaMGR.getAllFiles();
		for (FileMetaData metaData:metaList)
			sendMessage(pc,metaData);

	}
	public synchronized void handleGetFileMetaData(PacketChannel pc, GetFileMetaData message) {
		MetaDataManager metaMGR 			= getNode().getResourceManager().getMetadataManager();
		if (message.getHashedKey()==-1)
			handleGetAllFilesMetaData(pc, metaMGR);
		else
			sendMessage(pc,metaMGR.getFile(message.getHashedKey()));
	}
		
	@Override
	public void handle(PacketChannel pc, Message msg) {
		handleGetFileMetaData(pc, (GetFileMetaData)msg);
	}

	@Override
	public int getHandlerID() {
		return Message.GET_FILE_METADATA;
	}
}
