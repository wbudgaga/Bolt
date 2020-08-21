package mr.dht.peer2peernetwork.nodes;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import mr.communication.handlers.Acceptor;
import mr.communication.handlers.AcceptorListener;
import mr.communication.handlers.Connector;
import mr.communication.handlers.ConnectorListener;
import mr.communication.handlers.PacketChannel;
import mr.communication.io.SelectorThread;
import mr.dht.peer2peernetwork.handlers.ClientMessageHandler;
import mr.dht.peer2peernetwork.handlers.DiscoveryMessageHandler;
import mr.dht.peer2peernetwork.handlers.LocalMessageHandler;
import mr.dht.peer2peernetwork.handlers.PeerMessageHandler;

public abstract class LNode extends Node implements AcceptorListener{
	private LocalMessageHandler	messageHandler;
	protected SelectorThread 	selector;
	
	//This constructor will be called by discovery
	public LNode(int listeningPort) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException{
		super(Setting.DISCOVER_ID, Setting.DISCOVER_NAME);
		setHostData(listeningPort);	
		messageHandler 				= new DiscoveryMessageHandler(this);
		startListining(listeningPort);
	}

	//This constructor will be called by client
	public LNode(String  name, int listeningPort) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException{
		super(Setting.DISCOVER_ID, Setting.DISCOVER_NAME);
		setHostData(listeningPort);	
		messageHandler 				= new ClientMessageHandler(this);
		//startListining(listeningPort);
	}

	//This constructor will be called by peer
	public LNode(String  name,long id, int listeningPort) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException{
		super(id, name);
		setHostData(listeningPort);
		messageHandler 				= new PeerMessageHandler(this);
		startListining(listeningPort);
	}
		
	public void initiateConnectionManager(String host, int port, ConnectorListener listener) throws IOException{
		InetSocketAddress remotePoint 		= new InetSocketAddress(host, port);
	    	Connector connector 			= new Connector(selector, remotePoint, listener);
	    	connector.connect();
	}

	protected boolean startListining(int port) throws IOException{
		selector 				= new SelectorThread();
		Acceptor acceptor 			= new Acceptor(port,selector, this);
	    	acceptor.openServerSocket();
	    	System.out.println("Listening on port: " + port);
		return true;
	}
	
	protected boolean stopListining(int port) throws IOException{
		return true;
	}	
		
	public SelectorThread getSelector() {
		return selector;
	}

	public void setSelector(SelectorThread selector) {
		this.selector 				= selector;
	}
	protected void setHostData(int port){
		try {
			nodeData.setHost(InetAddress.getLocalHost().getHostName());
		} catch (UnknownHostException e) {
			nodeData.setHost("UnknownHost");
		}
		nodeData.setPortNum(port);
	}
	public LocalMessageHandler getMessageHandler() {
		return messageHandler;
	}

	public void setMessageHandler(LocalMessageHandler handler) {
		this.messageHandler 			= handler;
	}
	public abstract void socketException(PacketChannel pc, Exception ex);
	public abstract void socketDisconnected(PacketChannel pc);
}
