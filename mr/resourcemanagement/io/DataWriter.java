package mr.resourcemanagement.io;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.HashMap;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.datatype.TaskDataQueue;

public abstract class DataWriter <K,V> {
	protected long 	alreadyWritten 			= 0;
	protected File 	file;
	protected String outputFullName;
	public abstract void write(HashMap<K, V>  reducerBuffer) throws IOException;
	public void close() throws IOException {}
}
