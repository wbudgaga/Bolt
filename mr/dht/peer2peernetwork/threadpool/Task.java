package mr.dht.peer2peernetwork.threadpool;


import java.io.IOException;

public abstract class Task {
	public abstract void execute() throws IOException, InterruptedException;
}
