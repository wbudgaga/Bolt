package mr.dht.peer2peernetwork.handlers;

import mr.dht.peer2peernetwork.handlers.HandlerTypes.DiscoveryMSGHandlerClasses;
import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.Setting;

public class DiscoveryMessageHandler extends LocalMessageHandler{

	public DiscoveryMessageHandler(LNode node) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(node);
	}			   
				
	protected void loadMessageHandlers() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		DiscoveryMSGHandlerClasses[] classIDs = DiscoveryMSGHandlerClasses.values();
		for (int i=0;i<classIDs.length;++i){
			@SuppressWarnings("unchecked")
			Class<MessageHandler> handlerClass = (Class<MessageHandler>) Class.forName(Setting.HANDLER_PACKAGE + classIDs[i].toString());
			createMessageHandlerObject(handlerClass);		}
	}
}
