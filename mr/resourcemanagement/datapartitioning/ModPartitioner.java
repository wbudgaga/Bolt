package mr.resourcemanagement.datapartitioning;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;

public class ModPartitioner<K extends Long, V> extends Partitioner<Long, V> {	
	

	public ModPartitioner() throws NoSuchAlgorithmException {
		super();
	}

	@Override
	public int getReducerID(Long key, V value, int numOfReduces) {
		return (int) (key % numOfReduces);
	}
	
	public static void main(String[]s) throws NoSuchAlgorithmException{
		ModPartitioner<Long,String> t= new ModPartitioner<Long, String>();
		System.out.println(t.getReducerID(5l,"anyString",3));
	}


}


	