package mr.dht.peer2peernetwork.wireformates;

import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.RemotePeer;
import mr.dht.peer2peernetwork.util.ByteStream;

public class FingerTableResponse extends Message{
	private PeerInfo   peer;
	private PeerInfo   pred;
	private PeerInfo[] peersData;
	
	public FingerTableResponse() {
		super(FINGER_TABLE_RESPONSE, FINGER_TABLE_RESPONSE);
	}

	private PeerInfo unpackPeerInfo(byte[] byteStream){
		byte[] bytes 		= readObjectBytes(byteStream);
		PeerInfo peer 		= new PeerInfo();
		peer.initiate(bytes);
		return peer;
	}
	
	@Override
	public void initiate(byte[] byteStream) {
		peer 			= unpackPeerInfo(byteStream);
		pred 			= unpackPeerInfo(byteStream);
		int size 		= unpackIntField(byteStream);
		peersData 		= new PeerInfo[size];
		for (int i=0; i<size; ++i){
			peersData[i]	= unpackPeerInfo(byteStream);
		}
	}

	@Override
	protected byte[] packMessageBody() {
		byte[] objectBytes = peer.packMessage();
		byte[] bytes= ByteStream.join (ByteStream.intToByteArray(objectBytes.length),objectBytes);
		
		objectBytes = pred.packMessage();
		bytes= ByteStream.join (bytes, ByteStream.intToByteArray(objectBytes.length));
		bytes= ByteStream.join (bytes,objectBytes);
		
		
		bytes		= ByteStream.join (bytes,ByteStream.intToByteArray(peersData.length));
		for (int i=0; i<peersData.length;++i){
			objectBytes = peersData[i].packMessage();
			bytes		= ByteStream.join (bytes ,ByteStream.intToByteArray(objectBytes.length));
			bytes		= ByteStream.join (bytes,objectBytes);
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
	
	public void setPredecessor(PeerInfo pred){
		this.pred = pred; 
	}
	
	public PeerInfo getPredecessor(){
		return pred; 
	}

	public void setPeer(PeerInfo peer){
		this.peer = peer; 
	}
	
	public PeerInfo getPeer(){
		return peer; 
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
		FingerTableResponse AllPeers = new FingerTableResponse();
		PeerInfo peer = getPD(1000, "peer", "peerHost", 5000);
		PeerInfo pred = getPD(500, "pred", "predHost", 4000);
		PeerInfo[] pList = new PeerInfo[5];
		for (int i=0;i<5;++i){
			pList[i] = getPD(1000+i, "peer"+i, "peerHost"+i, 5000+i);
		}
		
		AllPeers.setPredecessor(pred);
		AllPeers.setPeer(peer);
		AllPeers.setPeerList(pList);
		
		MessageFactory f = MessageFactory.getInstance();
		
		byte [] bt = AllPeers.packMessage();
		
		
		FingerTableResponse ftr = (FingerTableResponse) f.createMessage(bt);
		System.out.println(ftr);
	}

}
