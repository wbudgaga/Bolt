package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;

public class StartReduceTask extends StartMapTask{
	
	public StartReduceTask() {
		super(START_REDUCETask, START_REDUCETask);
	}
	
	public void setNumberOfMappers(int num){
		setNumOfReducers(num);
	}
	
	public int getNumberOfMappers(){
		return getNumOfReducers();
	}
	
	public String getReducerOutputName() {
		return getOutputName();
	}
	
	public void setReducerOutputName(String dataOutputName) {
		setOutputName(dataOutputName);
	}

	@Override
	public String getMessageType() {
		return "StartReduceTask";
	}
}
