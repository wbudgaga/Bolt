package mr.dht.peer2peernetwork.handlers;

public interface HandlerTypes {
/*	
	
	public static final int STORE_FILE_REQUEST	 			= 10;
	public static final int GET					 			= 11;
	public static final int GET_RESPONSE		 			= 12;
*/
/*	public static enum MapReduceMSGHandlerClasses{
		RegisterResponseHandler,	
		LookupHandler,
		ForwardHandler,
		QueryResultHandler,
		PredecessorHandler,
		SuccessorHandler,
		PeerInfoHandler,
		GetPredecessorHandler,
		GetPredecessorResponseHandler,
		GetSuccessorHandler,
		GetSuccessorResponseHandler,
		NewPeerHandler,
		FingerTableRequestHandler,
		FingerTableResponseHandler, 
		RandomPeerHandler;
*/
	
	public static enum ClientMSGHandlerClasses{
		RandomPeerHandler,
		QueryResultHandler,
		SubmitDataQResultHandler,
		GetAllPeersResponseHandler,
		DatasetMetaDataHandler,
		FileMetaDataHandler;
/*		
		Successor,
		Lookup,
		Forward,
		
		GetPredecessor,
		RandomPeerRequest,
		StoreFileRequest,
		Get,
		GetResponse;
*/
		 public static String get(int i){
			 return values()[i].toString();
		 }
	}

	public static enum PeerMSGHandlerClasses{
		RegisterResponseHandler,	
		LookupHandler,
		ForwardHandler,
		QueryResultHandler,
		PredecessorHandler,
		SuccessorHandler,
		PeerInfoHandler,
		GetPredecessorHandler,
		GetPredecessorResponseHandler,
		GetSuccessorHandler,
		GetSuccessorResponseHandler,
		NewPeerHandler,
		FingerTableRequestHandler,
		FingerTableResponseHandler, 
		PeersListHandler,
		RandomPeerHandler,
		StartMapTaskHandler,
		StartReduceTaskHandler,
		TextNumTaskDataHandler,
		SubmitDataQResultHandler,
		NotifyFinishedMapHandler,
		ReducerPeerHandler,
		FindRunningReducerHandler,
		FinishedMapTaskNotifyHandler,
		StartJobBatchHandler,
		StoreDataMessageHandler,
		DatasetMetaDataHandler,
		GetFileMetaDataHandler,
		FileMetaDataHandler,
		GetDatasetMetaDataHandler,
		GetAllPeersHandler;
/*		
		Successor,
		Lookup,
		Forward,
		
		GetPredecessor,
		RandomPeerRequest,
		StoreFileRequest,
		Get,
		GetResponse;
*/
		 public static String get(int i){
			 return values()[i].toString();
		 }
	}
	
	public static enum DiscoveryMSGHandlerClasses{
		RegisterRequestHandler,			
		PeerInfoHandler,
		RandomPeerRequestHandler,
		GetAllPeersHandler;
/*		Predecessor,
		Successor,
		Lookup,
		Forward,
		QueryResult,
		GetPredecessor,
		RandomPeerRequest,
		StoreFileRequest,
		Get,
		GetResponse;
*/
		 public static String get(int i){
			 return values()[i].toString();
		 }
	}

}
