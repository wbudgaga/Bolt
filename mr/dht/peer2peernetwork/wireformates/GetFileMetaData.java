package mr.dht.peer2peernetwork.wireformates;

public class GetFileMetaData extends Get{
	public GetFileMetaData() {
		super(GET_FILE_METADATA, GET_FILE_METADATA);
	}
	public String getMessageType() {
		return "GET_FILE_METADATA";
	}
}
