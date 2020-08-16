package mr.dht.peer2peernetwork.test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.PacketChannel;
import mr.communication.handlers.SimpleProtocolDecoder;
import mr.communication.io.SelectorThread;
import mr.dht.peer2peernetwork.handlers.PeerMessageHandler;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.wireformates.Predecessor;

public class SingleClient implements ConnectorListener {
  private final SelectorThread st;  
  private PacketChannel pChannel 	= null;
  private int packetsSent 		= 0;
 
  public SingleClient(InetSocketAddress remotePoint, SelectorThread st) throws Exception {    
	this.st 			= st;
    Connector connector 		= new Connector(st, remotePoint, this);
    connector.connect();
  }
  private ByteBuffer generateNextPacket() {
	  Predecessor r 		= new Predecessor();
	  PeerData peer 		= new PeerData();
	  peer.setHost("host1");
	  peer.setNickName("Walid Budgaga"+packetsSent);
	  peer.setPeerID(19041972);
	  peer.setPortNum(5005);
	  r.setPeer(peer);
	  byte[] msgBody 		=  r.packMessage();
	  byte[] all 			= ByteStream.join(ByteStream.intToByteArray(msgBody.length), msgBody);  
	  ByteBuffer buffer 		= ByteBuffer.wrap(all);
	  return buffer;
  }
	
  public void connectionEstablished(Connector connector, SocketChannel sc) {    
    try {
      sc.socket().setReceiveBufferSize(2*1024);
      sc.socket().setSendBufferSize(2*1024);
      PeerMessageHandler h 		= new PeerMessageHandler(null);//#######################
      pChannel 				= new PacketChannel(sc,st, new SimpleProtocolDecoder(), h);    
      System.out.println("["+ connector + "] Connected: " + sc.socket().getInetAddress() );
      checkAllConnected();
    } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
	
  public void connectionFailed(Connector connector, Exception cause) {
    System.out.println("[" + connector + "] Error: " + cause.getMessage());
    checkAllConnected();
  }
	
  private void connectionClosed() {
      st.requestClose();      
      System.exit(1);
  }
	
  private void sendPacket(PacketChannel pc) {
	    ByteBuffer packet 		= generateNextPacket();
	    packetsSent++;
	    pc.sendPacket(packet);  
  }
  private void checkAllConnected() {   
      if (pChannel!=null)
      	sendPacket(pChannel);
     // pChannel = null;
  }
  public static void main(String[] args) throws Exception {    
    InetSocketAddress remotePoint = new InetSocketAddress(args[0], Integer.parseInt(args[1]));
    SelectorThread st = new SelectorThread();
    new SingleClient(remotePoint, st);
  }
}
