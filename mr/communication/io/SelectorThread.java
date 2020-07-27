package mr.communication.io;

import java.io.IOException;
import java.nio.channels.*;
import java.util.*;

final public class SelectorThread implements Runnable {
  private Selector selector;
  private Thread selectorThread;
  private boolean closeRequested 	= false;
  private final List pendingInvocations = new ArrayList(32);
  
  public SelectorThread() throws IOException {
	  selector 			= Selector.open();
	  selectorThread 		= new Thread(this);
	  selectorThread.start();
  }
  
  public void requestClose() {
	  closeRequested 		= true;
	  selector.wakeup();
  }
  
  public void addChannelInterestNow(SelectableChannel channel, int interest) throws IOException {
	  SelectionKey sk = channel.keyFor(selector);
	  changeKeyInterest(sk, sk.interestOps() | interest);
  }

  public void addChannelInterestLater(final SelectableChannel channel, final int interest, final CallbackErrorHandler errorHandler) {
	  invokeLater(new Runnable() {
		  public void run() {
			  try {
				  addChannelInterestNow(channel, interest);
			  } catch (IOException e) {
				  errorHandler.handleError(e);
			  }
		  }
	  });
  }
  
  public void removeChannelInterestNow(SelectableChannel channel, int interest) throws IOException {
    	if (Thread.currentThread() != selectorThread) {
      		throw new IOException("Method can only be called from selector thread");
    	}
    	SelectionKey sk 		= channel.keyFor(selector);
    	changeKeyInterest(sk, sk.interestOps() & ~interest);
  }
  
  public void removeChannelInterestLater(final SelectableChannel channel, final int interest, final CallbackErrorHandler errorHandler)  {
    	invokeLater(new Runnable() {
      		public void run() {
        		try {
          			removeChannelInterestNow(channel, interest);
        		} catch (IOException e) {
          			errorHandler.handleError(e);
        		}
      		}
    	});
  }

  private void changeKeyInterest(SelectionKey sk, int newInterest) throws IOException {
    	try {
      		sk.interestOps(newInterest);
    	} catch (CancelledKeyException cke) {
      		IOException ioe = new IOException("Failed to change channel interest.");
      		ioe.initCause(cke);
      		throw ioe;
    	}
  }
  public void registerChannelLater(final SelectableChannel channel, final int selectionKeys, final SelectorHandler handlerInfo, final CallbackErrorHandler errorHandler) {
	invokeLater(new Runnable() {
		public void run() {
			try {
				registerChannelNow(channel, selectionKeys, handlerInfo);
			} catch (IOException e) {
				errorHandler.handleError(e);
        		}
      		}
    	});
  }  
  public void registerChannelNow(SelectableChannel channel, int selectionKeys, SelectorHandler selHandler) throws IOException {
	if (Thread.currentThread() != selectorThread) 
		  throw new IOException("Method can only be called from selector thread");
    
    	if (!channel.isOpen()) 
    		throw new IOException("Channel is not open.");
    
    	try {
    		if (channel.isRegistered()) {
    			SelectionKey sk 	= channel.keyFor(selector);
    			assert sk 		!= null : "Channel is already registered with other selector";        
    			sk.interestOps(selectionKeys);
    			Object previousAttach 	= sk.attach(selHandler);
    			assert previousAttach 	!= null;
    		} else {  
    			channel.configureBlocking(false);
    			channel.register(selector, selectionKeys, selHandler);      
    		}  
    	} catch (Exception e) {
    		IOException ioe 		= new IOException("Error registering channel.");
    		ioe.initCause(e);
    		throw ioe;      
    	}
  }  
  
  public void invokeLater(Runnable run) {
    	synchronized (pendingInvocations) {
      		pendingInvocations.add(run);
    	}
    	selector.wakeup();
  }
  
  public void invokeAndWait(final Runnable task) throws InterruptedException{
    if (Thread.currentThread() == selectorThread) {
    	task.run();      
    } else {
      // Used to deliver the notification that the task is executed    
      final Object latch 		= new Object();
      synchronized (latch) {
        // Uses the invokeLater method with a newly created task 
        this.invokeLater(new Runnable() {
          public void run() {
            task.run();
            // Notifies
            latch.notify();
          }
        });
        // Wait for the task to complete.
        latch.wait();
      }
      // Ok, we are done, the task was executed. Proceed.
    }
  }
  
  private void doInvocations() {
    synchronized (pendingInvocations) {
      for (int i = 0; i < pendingInvocations.size(); i++) {
        Runnable task 			= (Runnable) pendingInvocations.get(i);
        task.run();
      }
      pendingInvocations.clear();
    }
  }

  public void run() {
    // Here's where everything happens. The select method will
    // return when any operations registered above have occurred, the
    // thread has been interrupted, etc.    
    while (true) {   
      // Execute all the pending tasks.
      doInvocations();
      
      // Time to terminate? 
      if (closeRequested) {
        return;
      }
      
      int selectedKeys = 0;
      try {
        selectedKeys 		= selector.select();
      } catch (IOException ioe) {
        // Select should never throw an exception under normal 
        // operation. If this happens, print the error and try to 
        // continue working.
        ioe.printStackTrace();
        continue;
      }
      
      if (selectedKeys == 0) 
    	  continue;
      
      // Someone is ready for IO, get the ready keys
      Iterator it = selector.selectedKeys().iterator();
      // Walk through the collection of ready keys and dispatch
      // any active event.
      while (it.hasNext()) {
        SelectionKey sk = (SelectionKey)it.next();
        it.remove();
        try {
          // Obtain the interest of the key
          int readyOps = sk.readyOps();
          // Disable the interest for the operation that is ready.
          // This prevents the same event from being raised multiple 
          // times.
          sk.interestOps(sk.interestOps() & ~readyOps);
          SelectorHandler handler =  (SelectorHandler) sk.attachment();          
          
          // Some of the operations set in the selection key
          // might no longer be valid when the handler is executed. 
          // So handlers should take precautions against this 
          // possibility.
          
          // Check what are the interests that are active and
          // dispatch the event to the appropriate method.
          if (sk.isAcceptable()) {
            // A connection is ready to be completed
            ((AcceptSelectorHandler)handler).handleAccept();
            
          } else if (sk.isConnectable()) {
            // A connection is ready to be accepted            
            ((ConnectorSelectorHandler)handler).handleConnect();            
            
          } else {
            ReadWriteSelectorHandler rwHandler = (ReadWriteSelectorHandler)handler; 
            // Readable or writable              
            if (sk.isReadable()) {                
              // It is possible to read
              rwHandler.handleRead();              
            }
            
            // Check if the key is still valid, since it might 
            // have been invalidated in the read handler 
            // (for instance, the socket might have been closed)
            if (sk.isValid() && sk.isWritable()) {
              // It is read to write
              rwHandler.handleWrite();                              
            }
          }
        } catch (Throwable t) {
          // No exceptions should be thrown in the previous block!
          // So kill everything if one is detected. 
          // Makes debugging easier.
          closeSelectorAndChannels();
          t.printStackTrace();
          return;
        }
      }
    }
  }
    
  private void closeSelectorAndChannels() {
    Set keys = selector.keys();
    for (Iterator iter 		= keys.iterator(); iter.hasNext();) {
      SelectionKey key 		= (SelectionKey)iter.next();
      try {
        key.channel().close();
      } catch (IOException e) {
        // Ignore
      }
    }
    try {
      selector.close();
    } catch (IOException e) {
      // Ignore
    }
  }

}
