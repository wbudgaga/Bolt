package mr.dht.peer2peernetwork.wireformates;


public class GetDatasetMetaData extends Get{
	public GetDatasetMetaData() {
		super(GET_DATASET_METADATA, GET_DATASET_METADATA);
	}
	public String getMessageType() {
		return "GET_DATASET_METADATA";
	}
}
