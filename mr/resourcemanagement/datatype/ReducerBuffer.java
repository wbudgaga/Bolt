package mr.resourcemanagement.datatype;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

public class ReducerBuffer<K,V>{
	private int reducerIDX; 
	private volatile int tmpCounter 		= 0;//////////////////////////tmep
	protected HashMap<K, ArrayList<V>> outputBuf	= new HashMap<K, ArrayList<V>>();
	
	public ReducerBuffer(int reducerID){
		try {
			setReducerIDX(reducerID);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public int size(){
		return outputBuf.size();
	}
	
	//also combines result (string,long)
	public synchronized int add(K key, V data) throws InterruptedException{
		Long v 					= (long)data;
		ArrayList<V> valuesList;
		if (outputBuf.containsKey(key)){
			valuesList		 	= outputBuf.get(key);
		    	v 				+= ((long)valuesList.remove(0));
		}else{  //  new key
			tmpCounter 			+= 8;
			tmpCounter 			+= (((String)key).length()*2);
			valuesList 			= new ArrayList<V>();
			outputBuf.put(key, valuesList );
		}
		valuesList.add((V)v);
		return tmpCounter;
	}

	public void resetCounter(){
		tmpCounter				= 0;
	}

	public int getCounter(){
		return tmpCounter;
	}

	public void clear(){
		resetCounter();
		outputBuf.clear();
	}

	public HashMap<K, ArrayList<V>>  getOutputBuf(){
		return outputBuf;
	}
	
	public int getReducerIDX() {
		return reducerIDX;
	}
	public void setReducerIDX(int reducerIDX) {
		this.reducerIDX = reducerIDX;
	}
}
