package mr.dht.peer2peernetwork.test;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

import mr.dht.peer2peernetwork.threadpool.Task;
import mr.dht.peer2peernetwork.threadpool.ThreadPoolManager;
import mr.resourcemanagement.datatype.ReducerBuffer;

public class TestBlockingQueue extends Task{
	private static BlockingQueue<Integer>  readyBuffers	= new ArrayBlockingQueue<Integer>(1000);
	private int t; //1:producer, 2:consumer
	private int count					= 0; 
	public TestBlockingQueue(int ty){
		t=ty;
	}
	
	public static void main(String[] args) {
		ThreadPoolManager t 				= new ThreadPoolManager(4);
		t.start();
		TestBlockingQueue producer 			= new TestBlockingQueue(1);
		TestBlockingQueue consumer 			= new TestBlockingQueue(2);
		t.addTask(consumer);
		t.addTask(producer);
		
	}

	@Override
	public void execute() throws IOException, InterruptedException {
		while(true){
			if (t==1 && count<10){
				count				+= 1;
				System.out.println("putting " + count);
				readyBuffers.offer(count);
			}
				
			else
				System.out.println("before");
				System.out.println(readyBuffers.take());
				System.out.println("after");
		}
	}
}
