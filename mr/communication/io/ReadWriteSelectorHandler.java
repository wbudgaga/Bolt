
package mr.communication.io;

/**
 * Interface used for reading and writing from a socket using 
 * non-blocking operations.
 * 
 * Classes wishing to be notified when a socket is ready to be written
 * or read should implement this interface in order to receive 
 * notifications.
 */ 
public interface ReadWriteSelectorHandler extends SelectorHandler {  
  
  /**
   * Called when the associated socket is ready to be read.
   */
  public void handleRead();
  /**
   * Called when the associated socket is ready to be written.
   */  
  public void handleWrite();  
}
