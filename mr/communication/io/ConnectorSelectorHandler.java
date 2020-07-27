
package mr.communication.io;

/**
 * Interface used for establishment a connection using non-blocking
 * operations.
 * 
 * Should be implemented by classes wishing to be notified 
 * when a Socket finishes connecting to a remote point. 
 */
public interface ConnectorSelectorHandler extends SelectorHandler {
  /**
   * Called by SelectorThread when the socket associated with the 
   * class implementing this interface finishes establishing a 
   * connection.
   */
  public void handleConnect();
}
