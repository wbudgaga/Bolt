package mr.dht.peer2peernetwork.wireformates;

import java.util.HashMap;
import java.util.Map;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.UtilClass;

// this class uses reflection to load  particular class to create the required instance
public class MessageFactory {
	private static MessageFactory instance;	
	private Map<Integer,Message> messageList 	= new HashMap<Integer,Message>();
	
	private void loadMessages() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		MessageTypes.ClassName[] classIDs 	= MessageTypes.ClassName.values();
		for (int i = 0; i < classIDs.length; ++i){
			@SuppressWarnings("unchecked")
			Class<Message> messageClass = (Class<Message>) Class.forName(Setting.MESSSAGE_PACKAGE + classIDs[i].toString());
			createMessageObject(messageClass);
		}
	}
	
	protected void createMessageObject(Class<Message> messageClass) throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		Message msg = (Message) messageClass.newInstance();
		messageList.put(msg.getMessageID(), msg);
	}
	
	private MessageFactory() throws ClassNotFoundException, InstantiationException, IllegalAccessException{	
		loadMessages();
	}
	
	public  static  MessageFactory getInstance() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		if (instance == null)
			instance = new MessageFactory();
	    return instance;
	}
	public Message getMessageInstance(int messageID){
		return messageList.get(messageID);
	}
	
	public Message createMessage(byte[]  byteStream) throws InstantiationException, IllegalAccessException{
		int messageID 			= Message.unpackMessageID(byteStream);
		int messageHandlerID 	= Message.unpackHandlerID(byteStream);
		Message msg 			= getMessageInstance(messageID);
/*		msg.create(byteStream);
		return msg;
*/	//the commenented part is more efficent but causes race condition since the same object can be used by different parts at the same time ###<<==###
		//maybe I have to think to add locker in the msg that will be released after sending
		
		Message newMsg = msg.getClass().newInstance();
		newMsg.create(byteStream);
		newMsg.setHandlerID(messageHandlerID);
		return newMsg;
	}
}
