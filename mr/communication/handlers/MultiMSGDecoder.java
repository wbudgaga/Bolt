
package mr.communication.handlers;

import java.io.IOException;
import java.net.SocketException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import mr.communication.io.ProtocolDecoder;
import mr.dht.peer2peernetwork.nodes.PeerData;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.ByteStream;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.Message;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;
import mr.dht.peer2peernetwork.wireformates.Predecessor;
import mr.dht.peer2peernetwork.wireformates.RegisterRequest;
import mr.dht.peer2peernetwork.wireformates.Successor;

final public class MultiMSGDecoder implements ProtocolDecoder {
	private byte[] 			buffer 			= new byte[Setting.RECEIVEBUFF_SIZE ];
	private volatile byte[]		msgBodyLen		= new byte[4];
	private volatile int 		msgBodyLenPos		= 0;
	private volatile int 		pos 			= 0;
	private volatile int 		stillNeededBytes	= 0;
	private final MessageFactory messageFactory;
  
  public MultiMSGDecoder() throws ClassNotFoundException{
	  try {
		  this.messageFactory   			= MessageFactory.getInstance();
	  } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
		  throw new ClassNotFoundException("ProtocolDecoder: Couldn't  have an ionstance of MessageFactory");
	  }
  }

  public Message decodeMSG(ByteBuffer socketBuffer) throws IOException {
	  int l 						= stillNeededBytes;
	  if (stillNeededBytes == 0)
		stillNeededBytes 				= socketBuffer.getInt();
	  
	  int stillAvailableBytes 				= socketBuffer.remaining();
	  if (stillAvailableBytes >= stillNeededBytes){
		socketBuffer.get(buffer, pos, stillNeededBytes);
		byte[] newBuffer 				= new byte[pos + stillNeededBytes];
		stillNeededBytes 				= pos = 0;
    	  	System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
		try {
			return messageFactory.createMessage(newBuffer);
		} catch (InstantiationException | IllegalAccessException e) {
			throw new IOException("Couldn't create message for the recieved bytes!");
		}
	  }else{
		socketBuffer.get(buffer, pos, stillAvailableBytes);
		pos 						+= stillAvailableBytes;
		stillNeededBytes 				= stillNeededBytes - stillAvailableBytes;
	  }
	  return null;
  }

  private int getMSGLength(ByteBuffer socketBuffer){
	  while(socketBuffer.hasRemaining() && msgBodyLenPos < 4){
		msgBodyLen[msgBodyLenPos++] 			= socketBuffer.get();
	  }
	  if(msgBodyLenPos == 4){
		  msgBodyLenPos 				= 0;
		  return ByteStream.byteArrayToInt(msgBodyLen);
	  }
	  return 0;
  }

  public ArrayList<Message>  decode(ByteBuffer socketBuffer) throws IOException {
	  ArrayList<Message> msgs = new ArrayList<Message>();	
	  int stillAvailableBytes=-1 ;
	  try{
	  while (socketBuffer.hasRemaining()){
		  if (stillNeededBytes == 0){
			  stillNeededBytes = getMSGLength(socketBuffer);
			  if (stillNeededBytes == 0)
				  return msgs;
		  }
		  stillAvailableBytes = socketBuffer.remaining();
		  if (stillAvailableBytes >= stillNeededBytes){
			  socketBuffer.get(buffer, pos, stillNeededBytes);
			  byte[] newBuffer = new byte[pos + stillNeededBytes];
			  stillNeededBytes = pos = 0;
			  System.arraycopy(buffer, 0, newBuffer, 0, newBuffer.length);
			  
			  try {
				  msgs.add(messageFactory.createMessage(newBuffer));
			  } catch (InstantiationException | IllegalAccessException e) {
				  throw new IOException("Couldn't create message for the recieved bytes!");
			  }
		  }else{
			  socketBuffer.get(buffer, pos, stillAvailableBytes);
			  pos += stillAvailableBytes;
			  stillNeededBytes = stillNeededBytes - stillAvailableBytes;
		  }
	  }
	  return msgs;
	  }catch(Exception  e){
		  throw new IOException(Setting.DATA_DIR+": Problem encountered in decoding process where:\n pos= "+pos+" stillNeededBytes:"+stillNeededBytes+"  stillAvailableBytes:"+stillAvailableBytes+"  exception:"+e+" "+e.getMessage());
	  }
  }
  @Override
  public boolean stillNeedData() {
  	return stillNeededBytes>0;
  }
  
  public static PeerData getTestPeer(String ext){
	  PeerData peer = new PeerData();
	  peer.setHost("host1"+ext);
	  peer.setNickName("Walid Budgaga"+ext);
	  peer.setPeerID(19041972);
	  peer.setPortNum(5005);
	  return peer;
  }
  //Just for testing
  public static void main(String[] args) throws ClassNotFoundException, IOException {
	  MultiMSGDecoder d = new MultiMSGDecoder();
	  Predecessor predMSG = new Predecessor();
	  predMSG.setPeer(getTestPeer(""));
	  byte[] predMSGBody =  predMSG.packMessage();
	  byte[] predMSGBodyAll = ByteStream.join(ByteStream.intToByteArray(predMSGBody.length), predMSGBody);
	  
	  RegisterRequest registerRequestMSG = new RegisterRequest();
	  registerRequestMSG.setPeer(getTestPeer("abcMAlak"));
	  byte[] registerRequestMSGBody =  registerRequestMSG.packMessage();
	  byte[] registerRequestMSGBodyAll = ByteStream.join(ByteStream.intToByteArray(registerRequestMSGBody.length), registerRequestMSGBody);
	  
	  Successor succMSG = new Successor();
	  succMSG.setPeer(getTestPeer("succMSG"));
	  byte[] succMSGBody =  succMSG.packMessage();
	  byte[] succMSGBodyAll = ByteStream.join(ByteStream.intToByteArray(succMSGBody.length), succMSGBody);

	  byte[] all = ByteStream.join(predMSGBodyAll,registerRequestMSGBodyAll);
	  
	  byte[] allAll = new byte[all.length+10];
	  System.arraycopy(all, 0, allAll, 0, all.length);
	  System.arraycopy(succMSGBodyAll, 0, allAll, all.length, 10);
	  
	  ByteBuffer packetBuffer = ByteBuffer.wrap(allAll);
	  
	  ArrayList<Message>  m = d.decode(packetBuffer);
	  
	  for (Message msg:m){
		  System.out.println(msg.getMessageID());
	  }
	  
	  byte[] remaining = new byte[succMSGBodyAll.length-10];
	  packetBuffer = ByteBuffer.wrap(remaining);
	  m = d.decode(packetBuffer);
	  
	  for (Message msg:m){
		  System.out.println(msg.getMessageID());
	  }
  	}
  }
