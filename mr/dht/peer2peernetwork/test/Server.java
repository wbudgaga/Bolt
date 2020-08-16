package mr.dht.peer2peernetwork.test;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import mr.communication.handlers.Acceptor;
import mr.communication.handlers.AcceptorListener;
import mr.communication.handlers.PacketChannel;
import mr.communication.handlers.SimpleProtocolDecoder;
import mr.communication.io.SelectorThread;
import mr.dht.peer2peernetwork.handlers.PeerMessageHandler;

public class Server implements AcceptorListener {
  private final SelectorThread st;  
  
  public Server(int listenPort) throws Exception {
    st 					= new SelectorThread();    
    Acceptor acceptor 			= new Acceptor(listenPort, st, this);
    acceptor.openServerSocket();
    System.out.println("Listening on port: " + listenPort);
  }  
    
  public void socketConnected(Acceptor acceptor, SocketChannel sc) {    
    System.out.println("[" + acceptor + "] Socket connected: " + sc.socket().getInetAddress());
    try {
      sc.socket().setReceiveBufferSize(2*1024);
      sc.socket().setSendBufferSize(2*1024);
      PeerMessageHandler h 		= new PeerMessageHandler(null);
      // The contructor enables reading automatically.
      PacketChannel pc 			= new PacketChannel(sc, st, new SimpleProtocolDecoder(), h);
      pc.resumeReading();
    } catch (IOException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
      e.printStackTrace();
    }
  }
  
  public void socketError(Acceptor acceptor, Exception ex) {
    System.out.println("[" + acceptor + "] Error: " + ex.getMessage());
  }

  public static void main(String[] args) throws Exception {    
	  int listenPort 		= Integer.parseInt(args[0]);
	  new Server(listenPort);
  }
}
