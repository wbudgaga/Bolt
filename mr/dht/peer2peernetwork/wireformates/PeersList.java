package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.util.ByteStream;

public class PeersList extends Message{
	private PeerInfo[] peersData;
	
	public PeersList() {
		super(PEERS_LIST, PEERS_LIST);
	}

	private PeerInfo unpackPeerInfo(byte[] byteStream){
		byte[] bytes 			= readObjectBytes(byteStream);
		PeerInfo peer 			= new PeerInfo();
		peer.initiate(bytes);
		return peer;
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		int size 			= unpackIntField(byteStream);
		peersData 			= new PeerInfo[size];
		for (int i = 0; i < size; ++i){
			peersData[i]		= unpackPeerInfo(byteStream);
		}
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] objectBytes;
		byte[] bytes= ByteStream.intToByteArray(peersData.length);
		for (int i=0; i<peersData.length;++i){
			objectBytes = peersData[i].packMessage();
			bytes		= ByteStream.join (bytes ,ByteStream.intToByteArray(objectBytes.length));
			bytes		= ByteStream.join (bytes,peersData[i].packMessage());
		}
		return bytes;
	}

	@Override
	public String getMessageType() {
		return null;
	}
	
	public void setPeerList(PeerInfo[] peers){
		peersData = peers; 
	}
	
	public PeerInfo[] getPeerList(){
		return peersData; 
	}	

	public static PeerInfo getPD(long id, String n, String h, int port){
		PeerData 	pd1 = new  PeerData();
		pd1.setNickName(n);
		pd1.setHost(h);
		pd1.setPeerID(id);
		pd1.setPortNum(port);
		PeerInfo pi = new PeerInfo();
		pi.setPeer(pd1);
		return pi;
	}
		
	public static void main(String args[]) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		PeersList AllPeers = new PeersList();
		PeerInfo[] pList = new PeerInfo[5];
		for (int i=0;i<5;++i){
			pList[i] = getPD(1000+i, "peer"+i, "peerHost"+i, 5000+i);
		}
			
		AllPeers.setPeerList(pList);
		
		MessageFactory f = MessageFactory.getInstance();
		
		byte [] bt = AllPeers.packMessage();
		
		
		PeersList ftr = (PeersList) f.createMessage(bt);
		for (PeerInfo pi:ftr.getPeerList())
			System.out.println(pi.getPeer().getHost());
	}

}
