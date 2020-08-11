package mr.dht.peer2peernetwork.handlers;

import mr.dht.peer2peernetwork.nodes.LNode;
import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.handlers.HandlerTypes.ClientMSGHandlerClasses;

public class ClientMessageHandler extends LocalMessageHandler{
	public ClientMessageHandler(LNode node) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		super(node);
	}

	protected void loadMessageHandlers() throws ClassNotFoundException, InstantiationException, IllegalAccessException{
		ClientMSGHandlerClasses[] classIDs 			= ClientMSGHandlerClasses.values();
		for (int i = 0; i < classIDs.length; ++i){
			@SuppressWarnings("unchecked")
			Class<MessageHandler> handlerClass 		= (Class<MessageHandler>) Class.forName(Setting.CLIENT_HANDLER_PACKAGE + classIDs[i].toString());
			createMessageHandlerObject(handlerClass);
		}
	}
}
