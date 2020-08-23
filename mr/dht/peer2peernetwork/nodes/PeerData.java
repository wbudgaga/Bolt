package mr.dht.peer2peernetwork.nodes;



public class PeerData {
	private long 	peerID;
	private String 	nickName;
	private String 	host;
	private int 	portNum;
	
	public String getHost() {
		return host;
	}
	
	public void setHost(String hostName) {
		this.host 		= hostName;
	}
	
	public long getPeerID() {
		return peerID;
	}
	public void setPeerID(long peerID) {
		this.peerID = peerID;
	}
	public int getPortNum() {
		return portNum;
	}
	public void setPortNum(int portNum) {
		this.portNum = portNum;
	}
	public String getNickName() {
		return nickName;
	}
	public void setNickName(String nickName) {
		this.nickName = nickName;
	}
	
	public String getAddress() {
		return  host+":"+portNum;
	}

	public static void printRowSeparator(){
		System.out.println("======================================================");
	}
	public static void printHeader(){
		printRowSeparator();
		System.out.println(String.format(" %-6s  %-20s  %s ",		"PeerID",	"Peer's NickName"," Host Address"));
		printRowSeparator();
	}
	
	public void print(){
		System.out.println(String.format(" %-6s  %-20s  %s ",		peerID,	nickName,getAddress()));
	}
}
