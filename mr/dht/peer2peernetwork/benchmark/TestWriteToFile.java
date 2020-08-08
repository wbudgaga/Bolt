package mr.dht.peer2peernetwork.benchmark;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.io.DataSource;

public class TestWriteToFile {
	private static final int ITERATIONS 		= 5;
	private static final int RECORD_COUNT 		= 5242880; //(int) (8* Math.pow(10, 6));
//	private static final String RECORD 		= "Here is just sample line using to read and store using different available methods for testing them\n";
//	private static final int RECSIZE 		= RECORD.getBytes().length;
	private byte[] data;//		= new String[RECORD_COUNT];
	
	public int[] buffSizes={8*Setting.KILO, 16*Setting.KILO, 32*Setting.KILO, 64*Setting.KILO, 128*Setting.KILO, 265*Setting.KILO, 512*Setting.KILO,Setting.MEGA, 8*Setting.MEGA, 16*Setting.MEGA, 32*Setting.MEGA,64*Setting.MEGA, 128*Setting.MEGA, 265*Setting.MEGA};
	public String[] labels				= {"8K"," 16K"," 32K"," 64K"," 128K"," 265K"," 512K","1M","8M","16M","32M","64M","128M","265M"};

	
	public void createRandomData(int size){
		data 					= new byte[size];
		Random randomGenerater 			= new Random();
		randomGenerater.nextBytes(data);
	}
	
	public int storeUsingFileOutputStreamArray(String fn, int arraySize) throws IOException{
		FileOutputStream f 			= new FileOutputStream(fn);
		long start 				= System.currentTimeMillis();
		int offset 				= 0;
		int remaining;// = data.length;
		while (offset < data.length){
			remaining =  data.length -  offset;
			int len = remaining>arraySize?arraySize:remaining;
			f.write(data,offset,len);
			offset += len;
		}
	    f.flush();
	    long end = System.currentTimeMillis();
	    f.close();
	    System.out.println("  time: "+ (end - start)  + " ms");
	    return (int) (end - start);
	}
	public int storeUsingBufferedOutputStream(String fn, int bufSize) throws IOException{
		BufferedOutputStream f = new BufferedOutputStream(new FileOutputStream(fn), bufSize);
  	   long start = System.currentTimeMillis();
		int offset =0;
		int remaining;// = data.length;
		while (offset < data.length){
			remaining =  data.length -  offset;
			int len = remaining>bufSize?bufSize:remaining;
			f.write(data,offset,len);
			offset += len;
		}

	    f.flush();
		long end = System.currentTimeMillis();
		f.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}

	public int storeUsingNIO(String fn, int bufSize) throws IOException{
		FileChannel fChannel 		= new FileOutputStream(fn).getChannel();
		long start = System.currentTimeMillis();
		int offset =0;
		int readBytes;
		int remaining;// = data.length;
		while (offset < data.length){
			remaining =  data.length -  offset;
			int len = remaining>bufSize?bufSize:remaining;
			readBytes = fChannel.write(ByteBuffer.wrap(data, 0, len));
			offset += readBytes;
		}
		long end = System.currentTimeMillis();
		fChannel.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}
	public int storeUsingNIOMapped(String fn, int bufSize) throws IOException{
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		MappedByteBuffer mb = fChannel.map( FileChannel.MapMode.READ_ONLY, 0L, fChannel.size( ) );
		long start = System.currentTimeMillis();
		int offset =0;
		int readBytes;
		int remaining;// = data.length;
		while (offset < data.length){
			remaining =  data.length -  offset;
			int len = remaining>bufSize?bufSize:remaining;
			mb.put(data, 0, len);			
			offset += len;
		}
		long end = System.currentTimeMillis();
		fChannel.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}

	
	public void testOutputStream(String fn, int itr) throws IOException{
		for (int j=0; j<buffSizes.length; ++j){
			int sum =0;
			for (int i=0; i<itr; ++i){
				sum += storeUsingNIOMapped(fn, buffSizes[j]);
			}
			System.out.println("storeUsingNIOMapped, "+labels[j]+": "+(sum/(float)itr));
			System.out.println("=======================================================");
		}
	}
	
	public static void main(String[] args) throws IOException{//input: full file name, #iterations
		String fn=args[0];
		int itr = Integer.parseInt(args[1]);
		TestWriteToFile twtf = new TestWriteToFile();
		twtf.createRandomData(512*Setting.MEGA);
		twtf.testOutputStream(fn, itr);
	}
}
