package mr.resourcemanagement.datapartitioning;

import java.security.NoSuchAlgorithmException;

import mr.dht.peer2peernetwork.util.HashFunction;

public abstract class Partitioner<K,V> {
	public abstract int getReducerID(K key, V value,int numOfReduces);		
}
