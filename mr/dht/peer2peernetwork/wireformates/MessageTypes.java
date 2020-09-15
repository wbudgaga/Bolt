package mr.dht.peer2peernetwork.wireformates;


// contains the messages types and their equivalent classes' names
// it is very important to keep the order the same in both lists 
public interface MessageTypes {
	public static final byte SUCCEESS 				= 1;
	public static final byte FAILURE 				= 2;
	
	public static final int REGISTER_REQUEST 			= 0;
	public static final int REGISTER_RESPONSE 			= 1;
	public static final int PEER_INFO		 		= 2;
	public static final int PREDECESSOR		 		= 3;
	public static final int SUCCESSOR						= 4;
	public static final int LOOKUP							= 5;
	public static final int FORWARD							= 6;
	public static final int QUERY_RESULT					= 7;
	public static final int GET_PREDECESSOR		 			= 8;
	public static final int RANDOM_PEER	 					= 9;
	public static final int STORE_FILE_REQUEST	 			= 10;
	public static final int GET					 			= 11;
	public static final int GET_RESPONSE		 			= 12;
	public static final int GET_SUCCESSOR		 			= 13;
	public static final int GET_PREDECESSOR_RESPONSE	 	= 14;
	public static final int GET_SUCCESSOR_RESPONSE		 	= 15;
	public static final int New_PEER					 	= 16;
	public static final int FINGER_TABLE_REQUEST 		 	= 17;
	public static final int FINGER_TABLE_RESPONSE 		 	= 18;
	public static final int RANDOM_PEER_REQUEST 		 	= 19;
	public static final int PEERS_LIST			 		 	= 20;
	public static final int DATASET_META		 		 	= 21;
	public static final int FILE_META			 		 	= 22;
	public static final int GET_ALLPEERS		 		 	= 23;
	public static final int GET_DATASET_METADATA 		 	= 24;
	public static final int GET_FILE_METADATA 		 		= 25;
	
	//mapReduce messages IDs
	public static final int START_MAPTASK		 		 	= 100;
	public static final int START_REDUCETask		 		= 101;
	public static final int TASKDATA		 				= 102;
	public static final int TEXTNUM_TASKDATA		 		= 103;
	public static final int SUBMITDATA_QResult		 		= 104;
	public static final int FINISHEDMAPTASK_NOTIFYQRESULT	= 105;
	public static final int FINISHEDMAPTASKNOTIFY 			= 106;
	public static final int REDUCERPEER				 		= 107;
	public static final int START_JOB_BATCH				 	= 108;
	public static final int FIND_RUNNING_REDUCER		 	= 109;
	
	public static enum ClassName{
		RegisterRequest,
		RegisterResponse,
		PeerInfo,
		Predecessor,
		Successor,
		Lookup,
		//Forward,
		QueryResult,
		GetPredecessor,
		RandomPeer,
		StoreFileRequest,
		Get,
		GetResponse,
		GetSuccessor,
		GetPredecessorResponse,
		GetSuccessorResponse,		
		NewPeer, 
		FingerTableRequest,
		FingerTableResponse,
		RandomPeerRequest,
		PeersList,
		DatasetMetaData,
		FileMetaData,
		GetAllPeers,
		GetDatasetMetaData,
		GetFileMetaData,
		StartMapTask,
		StartReduceTask, 
		TaskData,
		TextNumTaskData,
		FinishedMapTaskNotify,
		StartJobBatch,
		FindRunningReducer;

		 public static String get(int i){
			 return values()[i].toString();
		 }
	}
}
