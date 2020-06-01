package mr.dht.peer2peernetwork.exceptions;

public class InvalidFingerTableEntry extends Exception {
	public InvalidFingerTableEntry() { 
			  super("InvalidFingerTableEntry"); 
	}
	public InvalidFingerTableEntry(String message) { 
		super(message); 
	}
}
