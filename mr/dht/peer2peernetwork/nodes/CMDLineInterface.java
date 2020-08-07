package mr.dht.peer2peernetwork.nodes;

import java.util.HashMap;

import mr.dht.peer2peernetwork.datastructure.JobDescriptor;

public interface CMDLineInterface {
	public void findRandomPeer();
	public void lookup(long key, int respHandlerID);
	public void submitJob(JobDescriptor job);
	public void submitTestBuffer(HashMap<String, Long[]> outputBuf, long jobID, int numOfReducers,int reducerIDX);
	public void submitDataSet(String name, String loc);	
}
