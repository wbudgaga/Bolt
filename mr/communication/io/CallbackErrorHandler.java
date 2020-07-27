
package mr.communication.io;

/**
 * Defines the callback to be used to handle errors in 
 * asynchronous method invocations.
 * 
 * When an operation is executed asynchronously, it is not
 * possible to use exceptions for error handling. Instead,
 * the caller must provide a callback to be used in case
 * of error. 
 */
public interface CallbackErrorHandler {
  /**
   * Called when an exception is raised when executing an
   * asynchronous method. 
   */
  public void handleError(Exception ex);
}
