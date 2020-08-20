package mr.dht.peer2peernetwork.test;

import java.util.concurrent.ConcurrentHashMap;

import mr.resourcemanagement.datatype.ReducerBuffer;
import mr.resourcemanagement.execution.mrtasks.MapTaskInfo;

public class TestDiffTypesHashMap {
	private static ConcurrentHashMap <Long,ReducerBuffer> mapTaskInfoList 	= new ConcurrentHashMap<Long,ReducerBuffer>();
	
	public static void store(Long id, ReducerBuffer rb){
		mapTaskInfoList.put(id, rb);
	}

	public static ReducerBuffer get(Long id){
		return mapTaskInfoList.get(id);
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TestDiffTypesHashMap tst 					= new TestDiffTypesHashMap();
		ReducerBuffer b1 = new ReducerBuffer<Integer,String>(0, null);
		ReducerBuffer b2 = new ReducerBuffer<Integer,Integer>(0,null);
		
		tst.store(1l, b1);
		tst.store(10l, b2);
		int i =0;
		System.out.println("@@@@@  "+i);
		ReducerBuffer<Integer,String> r= tst.get(1l);
		ReducerBuffer<Integer,Integer> r2= tst.get(10l);
	}

}
