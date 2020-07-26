
package mr.communication.handlers;

import java.io.IOException;
import java.nio.ByteBuffer;

import mr.communication.io.ProtocolDecoder;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.Predecessor;

final public class SimpleProtocolDecoder implements ProtocolDecoder {
	private final static int 	BUFFER_SIZE 	= 10*1024;
	private byte[] 			buffer 		= new byte[BUFFER_SIZE];
	private int 			pos 		= 0;
	private int 			stillNeededBytes= 0;
	private final MessageFactory messageFactory;
  
  public SimpleProtocolDecoder() throws ClassNotFoundException{
	  try {
		  this.messageFactory   		= MessageFactory.getInstance();
	  } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		  throw new ClassNotFoundException("ProtocolDecoder: Couldn't  have an instance of MessageFactory");
	  }
  }
  public Message decode(ByteBuffer socketBuffer) throws IOException {    
	  if (stillNeededBytes == 0)
		  stillNeededBytes 			= socketBuffer.getInt();
	  
	  int stillAvailableBytes 			= socketBuffer.remaining();
	  
	  if (stillAvailableBytes >= stillNeededBytes){
		  socketBuffer 				= socketBuffer.get(buffer, pos, stillNeededBytes);
		  byte[] newBuffer 			= new byte[pos + stillNeededBytes];
		  stillNeededBytes 			= pos = 0;
    	  System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
		  try {
			return messageFactory.createMessage(newBuffer);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IOException("Couldn't create message for the recieved bytes!");
		}
	  }else{
		  socketBuffer 				= socketBuffer.get(buffer, pos, stillAvailableBytes);
		  pos 					+= stillAvailableBytes;
		  stillNeededBytes 			= stillNeededBytes - stillAvailableBytes;
	  }
	  return null;
  }
  
  //Just for testing
  public static void main(String[] args) throws ClassNotFoundException, IOException {
	  SimpleProtocolDecoder d 			= new SimpleProtocolDecoder();
	  Predecessor r 				= new Predecessor();
	  PeerData peer 				= new PeerData();
	  peer.setHost("host1");
	  peer.setNickName("Walid Budgaga");
	  peer.setPeerID(19041972);
	  peer.setPortNum(5005);
	  r.setPeer(peer);
	  byte[] msgBody 				=  r.packMessage();
	  byte[] all 					= ByteStream.join(ByteStream.intToByteArray(msgBody.length), msgBody);
	  byte[]all1 					= new byte[28];
	  byte[]all2 					= new byte[30];
	  System.arraycopy(all, 0, all1, 0, all1.length);
	  System.arraycopy(all, 28, all2, 0, all2.length);
	  ByteBuffer packetBuffer 			= ByteBuffer.wrap(all1); 
	   
	  Predecessor r1 				= (Predecessor) d.decode(packetBuffer);
	  ByteBuffer packetBuffer1 = ByteBuffer.wrap(all2); 
	  
	  Predecessor r2 				= (Predecessor) d.decode(packetBuffer1);
	  System.out.println(r2);
	  
  }
}
