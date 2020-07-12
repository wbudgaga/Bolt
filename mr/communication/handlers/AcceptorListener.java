
package mr.communication.handlers;

import java.nio.channels.SocketChannel;

public interface AcceptorListener {
  /**
   * Called when a connection is established.
   * @param acceptor The acceptor that originated this event. 
   * @param sc The newly connected socket.
   */
  public void socketConnected(Acceptor acceptor, SocketChannel sc);
  /**
   * Called when an error occurs on the Acceptor.
   * @param acceptor The acceptor where the error occured.
   * @param ex The exception representing the error.
   */
  public void socketError(Acceptor acceptor, Exception ex);
}
