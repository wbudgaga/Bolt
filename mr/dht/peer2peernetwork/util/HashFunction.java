package mr.dht.peer2peernetwork.util;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class HashFunction{
	private MessageDigest md;
	
	public HashFunction() throws NoSuchAlgorithmException{
		md = MessageDigest.getInstance("MD5");//SHA1 MD5
	}
	
	public synchronized long hash(byte[]  key){
        byte[] messageDigest = md.digest(key);
        return (ByteStream.byteArrayToLong(messageDigest) & Long.MAX_VALUE);
	}
	
	public synchronized BigInteger hash1(byte[]  key){
        byte[] messageDigest = md.digest(key);
        return new BigInteger(messageDigest);
	}

	
	public long hash(String key){
        return hash(key.getBytes());
    }
	public long hash(Long key){
        return hash(ByteStream.longToByteArray(key));
    }

	
    public static void main(String[] args) throws NoSuchAlgorithmException {
    	long k =17l;
    	HashFunction f = new HashFunction();
        System.out.println(f.hash(k));
        BigInteger b = f.hash1(ByteStream.longToByteArray(k));
        System.out.println(b.longValue() );
    }
}
