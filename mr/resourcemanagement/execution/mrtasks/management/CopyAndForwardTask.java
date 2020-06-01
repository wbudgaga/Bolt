package mr.resourcemanagement.execution.mrtasks.management;

import java.io.FileOutputStream;
import java.io.IOException;

import mr.dht.peer2peernetwork.nodes.Peer;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.wireformates.StoreFileRequest;

import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class CopyAndForwardTask extends Task{
	private StoreFileRequest 	fileMSG;
	private FileChannel 		outputChannel  = null;
	private Peer				localPeer;
	public CopyAndForwardTask(StoreFileRequest fileMSG, Peer lPeer){
		this.fileMSG 	= fileMSG;
		this.localPeer	= lPeer;
	}
	 
	private void storeFile(int rp) throws IOException{
		int 	dirNr 		=  rp - 1;
		String 	outputName 	= Setting.DATA_DIR+"/d"+dirNr+"/"+fileMSG.getFileName();
		outputChannel 		= new FileOutputStream(outputName, true).getChannel();
		outputChannel.write(ByteBuffer.wrap(fileMSG.getFileBytes(), 0, fileMSG.getBufferSize()));
		outputChannel.close();
	}
	
	private void forwardFile(int newRF) throws IOException{
		RemotePeer successor = localPeer.getSuccessor();
		fileMSG.setReplicarionFactore(newRF);
		successor.sendMessage(fileMSG);
	}

	@Override
	public void execute() throws IOException, InterruptedException {
		int rp = fileMSG.getReplicarionFactore();
		if (rp > 1)
			forwardFile(rp - 1);
		storeFile(rp);
	}
}
