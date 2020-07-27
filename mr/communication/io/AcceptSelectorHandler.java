
package mr.communication.io;

/**
 * Interface used for accepting incoming connections using non-blocking
 * operations.
 * 
 * Classes wishing to be notified when a ServerSocket receives incoming 
 * connections should implement this interface.
 */
public interface AcceptSelectorHandler extends SelectorHandler {
  /**
   * Called by SelectorThread when the server socket associated
   * with the class implementing this interface receives a request
   * for establishing a connection.
   */
  public void handleAccept();
}
