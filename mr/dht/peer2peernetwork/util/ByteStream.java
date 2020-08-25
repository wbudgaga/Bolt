package mr.dht.peer2peernetwork.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

// this class contains static methods (helps methods) that are used by packing and unpacking between primitive types and byte stream
public class ByteStream {
	public static final byte[] getBytes(byte[]  byteStream,int start, int length){
		byte[] bytes 		= new byte[length];
		int end 		= start+length;
		for(int i = start; i< end; ++i){
			bytes[i-start] 	= byteStream[i];
		}
		return bytes;
	}

	public static byte[] toByteArray(long value, int n){  
	        byte[] ret 		= new byte[n];  
	        for(int i = 0; i < n; ++i){
	        	int idx 	= n-(i+1);
	        	ret[i] 		= (byte) ((value >>> (idx*8))); 
	        }
	        return ret;  
	}
				
	public static final long byteArrayToLong(byte [] b) {
		long ret 		= 0;			
		for(int i = 0; i < b.length; ++i){
			ret 		<<= 8;
			ret 		^= b[i] & 0xFF;
		}
		return ret;
	}
		
	public static final byte[] longToByteArray(long value) {
		return toByteArray(value,8);
	}

	public static final byte[] intToByteArray(int value) {
		return toByteArray(value,4);
	}

	public static final int byteArrayToInt(byte [] b) {
		return (int) byteArrayToLong(b);
	}								
		
	public static final byte[] StringToByteArray(String value) {
		return value.getBytes();
	}
		
	public static final String  byteArrayToString(byte[] value) {
		return new String(value);
	}

	private static final byte[] joinTwoArrays(byte[] array1, byte[] array2) {
		byte [] resultArray 		= new byte[array1.length + array2.length];
		System.arraycopy(array1,0,resultArray,0         ,array1.length);
		System.arraycopy(array2, 0, resultArray, array1.length, array2.length);
		return resultArray;
	}
	
	public static final byte[] join(byte[] array1, byte[] array2) {
		if (array1 == null && array2 == null)
			return null;
		if (array1 == null && array2 != null)
			return array2;
		if (array1 != null && array2 == null)
			return array1;
		return joinTwoArrays(array1,array2);
	}
		
	public static byte[] readFileBytes(File file){
		try {
			InputStream is = new FileInputStream(file);
			long length = file.length();
			if (length > Integer.MAX_VALUE) {
				System.out.println("File is too large. It can not be transfered!");
			        return null;
			}
				
			byte[] bytes = new byte[(int)length];
			// Read in the bytes
			int offset = 0;
			int numRead = 0;
			while (offset < bytes.length && (numRead=is.read(bytes, offset, bytes.length-offset)) >= 0) {
				offset += numRead;
			}
			if (offset < bytes.length) {
				throw new IOException("Could not completely read file "+file.getName());
			}

			// Close the input stream and send the msg
			is.close();
			return bytes; 
		} catch (FileNotFoundException e) {
			System.out.println("The file: "+file.getName()+ " could not be found in: "+file.getPath());
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static byte[] readFileBytes(String sourceDir, String fileName){
		File file = new File(sourceDir,fileName); 
		return readFileBytes(file);
	}
		
	/**
	* This method converts a set of bytes into a Hexadecimal representation.
	*/
	public static String convertBytesToHex(byte[] buf) {
		StringBuffer strBuf = new StringBuffer();
		for (int i = 0; i < buf.length; i++) {
			int byteValue = (int) buf[i] & 0xff;
			if (byteValue <= 15) {
				strBuf.append("0");
			}
			strBuf.append(Integer.toString(byteValue, 16));
		}
		return strBuf.toString();
	}
	
	/**
	* This method converts a specified hexadecimal String into a set of bytes.
	*
	* @param hexString
	* @return
	*/
	public static byte[] convertHexToBytes(String hexString) {
		int size = hexString.length();
		byte[] buf = new byte[size / 2];
		int j = 0;
		for (int i = 0; i < size; i++) {
			String a = hexString.substring(i, i + 2);
			int valA = Integer.parseInt(a, 16);
			i++;
			buf[j] = (byte) valA;
			j++;
		}
		return buf;
	}
		
	public static final byte[] packString(String value) {
		byte[] bytes = StringToByteArray(value);
		return join(intToByteArray(bytes.length), bytes);
	}
	
	public static final byte[] packLongArray(Long[] value) {
		byte[] bytes = intToByteArray(value.length);
		for (Long v:value){
			bytes = join(bytes,longToByteArray(v));
		}
		return bytes;
	}

	public static final byte[] packLongArrayList(ArrayList<Long> value) {
		byte[] bytes = intToByteArray(value.size());
		for (Long v:value){
			bytes = join(bytes,longToByteArray(v));
		}
		return bytes;
	}

	public static final byte[] packStringArrayList(ArrayList<String> value) {
		byte[] bytes = intToByteArray(value.size());
		for (String v:value){
			bytes = join(bytes,packString(v));
		}
		return bytes;
	}

		public static byte[] addPacketHeader(byte [] packetBody){
			int packetLegth = packetBody.length;
			byte[] packetLegthInBytes = intToByteArray(packetLegth);
			return join(packetLegthInBytes, packetBody);
		}
	
		// remove the header which is 4 bytes at the beginning of the packet
		public static byte[] removePacketHeader(byte[] byteStream){
			return Arrays.copyOfRange(byteStream, 4, byteStream.length); 
		}

		public static void main(String[] s){
			int l= Integer.MAX_VALUE-1;
			
			byte[] b= intToByteArray(l);
			int l1 = byteArrayToInt(b);
			long lll =12;
			System.out.println(StringToByteArray("abc").length+"    " +StringToByteArray("123").length+"====>"+longToByteArray(lll).length);
		}
}
