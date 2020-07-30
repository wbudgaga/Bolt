package mr.dht.peer2peernetwork.benchmark;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
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
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.resourcemanagement.io.DataSource;

public class TestReadFromFile {
	private static final int ITERATIONS 	= 5;
	private static final int RECORD_COUNT 	= 5242880; //(int) (8* Math.pow(10, 6));
//	private static final String RECORD 		= "Here is just sample line using to read and store using different available methods for testing them\n";
//	private static final int RECSIZE 		= RECORD.getBytes().length;
	
	public int[] buffSizes			= {8*Setting.KILO, 16*Setting.KILO, 32*Setting.KILO, 64*Setting.KILO, 128*Setting.KILO, 265*Setting.KILO, 512*Setting.KILO,Setting.MEGA, 8*Setting.MEGA, 16*Setting.MEGA, 32*Setting.MEGA,64*Setting.MEGA, 128*Setting.MEGA, 265*Setting.MEGA};
	public String[] labels			= {"8K"," 16K"," 32K"," 64K"," 128K"," 265K"," 512K","1M","8M","16M","32M","64M","128M","265M"};

	
	public void createRandomData(String fn, int size) throws IOException{
		byte[] data 			= new byte[size];
		Random randomGenerater 		= new Random();
		randomGenerater.nextBytes(data);
		FileChannel fChannel 		= new FileOutputStream(fn).getChannel();
		for (int i=0; i<50;++i){
			ByteBuffer bb = ByteBuffer.wrap(data);
			while (bb.hasRemaining())
				fChannel.write(bb);
		}
		fChannel.close();
	}

	public int readUsingFileInputStreamArray(String fn, byte[] bytes) throws IOException{
		FileInputStream f = new FileInputStream(fn);
		long start = System.currentTimeMillis();
		while ((f.read( bytes, 0, bytes.length )) != -1 ){
		}
	    long end = System.currentTimeMillis();
	    f.close();
	    System.out.println("  time: "+ (end - start)  + " ms");
	    return (int) (end - start);
	}
	public int readUsingBufferedIntputStream(String fn,  byte[] bytes) throws IOException{
		BufferedInputStream f = new BufferedInputStream(new FileInputStream(fn), bytes.length);
  	   long start = System.currentTimeMillis();
		int offset =0;
		int remaining;// = data.length;
		while ((f.read( bytes, 0, bytes.length )) != -1 ){
		}
		long end = System.currentTimeMillis();
		f.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}

	public int readUsingNIO(String fn, int bufSize) throws IOException{
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		ByteBuffer bb = ByteBuffer.allocateDirect( bufSize );
		long start = System.currentTimeMillis();
		while (fChannel.read(bb) != -1 ){
			bb.clear();
		}
		long end = System.currentTimeMillis();
		fChannel.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}
	public int storeUsingNIOMapped(String fn, byte[] bytes) throws IOException{
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		int numOfRegions			= (int) (fChannel.size( ) / Integer.MAX_VALUE);
		long start = System.currentTimeMillis();
		int i;
		for (i=0; i<numOfRegions; ++i){
			MappedByteBuffer mb = fChannel.map( FileChannel.MapMode.READ_ONLY, Integer.MAX_VALUE * (long)i , Integer.MAX_VALUE);
		
			while (mb.hasRemaining()){
				int len = mb.remaining()>bytes.length?bytes.length:mb.remaining();
				mb.get(bytes,0,len);			
			}
		}
		MappedByteBuffer mb = fChannel.map( FileChannel.MapMode.READ_ONLY, Integer.MAX_VALUE * (long)i ,fChannel.size( ) - Integer.MAX_VALUE * (long)i);
		
		while (mb.hasRemaining()){
			int len = mb.remaining()>bytes.length?bytes.length:mb.remaining();
			mb.get(bytes,0,len);			
		}

		long end = System.currentTimeMillis();
		fChannel.close();
		System.out.println("  time: "+ (end - start)+ " ms");
		return (int) (end - start);
	}

	
	public void testOutputStream0(String fn1, String fn2, int itr) throws IOException, InterruptedException{
		
		for (int j=0; j<buffSizes.length; ++j){
			int sum0 =0;
			int sum1 =0;
			byte[] bytes = new byte[buffSizes[j]];
			for (int i=0; i<itr; ++i){
				Thread.sleep(100);
				sum0 += readUsingFileInputStreamArray(fn1, bytes);
				sum1 += readUsingFileInputStreamArray(fn2, bytes);
			}
			System.out.println("fake: readUsingFileInputStreamArray, "+labels[j]+": "+(sum0/(float)itr));
			System.out.println("readUsingFileInputStreamArray, "+labels[j]+": "+(sum1/(float)itr));
			System.out.println("=======================================================");
		}
	}


	public void testOutputStream1(String fn1, String fn2, int itr) throws IOException, InterruptedException{
		
		for (int j=0; j<buffSizes.length; ++j){
			int sum0 =0;
			int sum1 =0;
			byte[] bytes = new byte[buffSizes[j]];
			for (int i=0; i<itr; ++i){
				Thread.sleep(100);
				sum0 += readUsingBufferedIntputStream(fn1, bytes);
				sum1 += readUsingBufferedIntputStream(fn2, bytes);
			}
			System.out.println("fake: readUsingBufferedIntputStream, "+labels[j]+": "+(sum0/(float)itr));
			System.out.println("readUsingBufferedIntputStream, "+labels[j]+": "+(sum1/(float)itr));
			System.out.println("=======================================================");
		}
	}

	public void testOutputStream2(String fn1, String fn2, int itr) throws IOException, InterruptedException{
		
		for (int j=0; j<buffSizes.length; ++j){
			int sum0 =0;
			int sum1 =0;
			byte[] bytes = new byte[buffSizes[j]];
			for (int i=0; i<itr; ++i){
				Thread.sleep(100);
				sum0 += readUsingNIO(fn1, buffSizes[j]);
				sum1 += readUsingNIO(fn2, buffSizes[j]);
			}
			System.out.println("fake: readUsingNIO, "+labels[j]+": "+(sum0/(float)itr));
			System.out.println("readUsingNIO, "+labels[j]+": "+(sum1/(float)itr));
			System.out.println("=======================================================");
		}
	}

	public void testOutputStream3(String fn1, String fn2, int itr) throws IOException, InterruptedException{
		
		for (int j=0; j<buffSizes.length; ++j){
			int sum0 =0;
			int sum1 =0;
			byte[] bytes = new byte[buffSizes[j]];
			for (int i=0; i<itr; ++i){
				Thread.sleep(100);
				sum0 += storeUsingNIOMapped(fn1, bytes);
				sum1 += storeUsingNIOMapped(fn2, bytes);
			}
			System.out.println("fake: storeUsingNIOMapped, "+labels[j]+": "+(sum0/(float)itr));
			System.out.println("storeUsingNIOMapped, "+labels[j]+": "+(sum1/(float)itr));
			System.out.println("=======================================================");
		}
	}

	public static void main(String[] args) throws IOException, InterruptedException{//input: full file name, #iterations
		String fn1=args[0];//faKE FILE
		String fn2=args[1];//ORIGINAL FILE
		int itr = Integer.parseInt(args[2]);
		TestReadFromFile twtf = new TestReadFromFile();
		//twtf.createRandomData(fn1, 512*Setting.MEGA);
		UtilClass.fileAsOutputDst("/tmp/readOpEvaluation.txt");
		twtf.testOutputStream0(fn1, fn2, itr);
		twtf.testOutputStream1(fn1, fn2, itr);
		twtf.testOutputStream2(fn1, fn2, itr);
		twtf.testOutputStream3(fn1, fn2, itr);
	}
}
