package mr.dht.peer2peernetwork.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.io.DataSource;

public class TestRWriteFToFile {
	private static final int ITERATIONS 		= 5;
	private static final int RECORD_COUNT 		= (int) (8* Math.pow(10, 6));
	private static final String RECORD 		= "Help I am trapped in a fortune cookie factory\n";
	private static final int RECSIZE 		= RECORD.getBytes().length;
	
	
	public void storeUsingFileWriter(String fn) throws IOException{
		FileWriter f 				= new FileWriter(fn);
		   long start 				= System.currentTimeMillis();
		   for (int i = 0; i< RECORD_COUNT; ++i) {
		        f.write(RECORD);
		    }
		
		    f.flush();
		    f.close();
		    long end 				= System.currentTimeMillis();
		    System.out.println("One By one Using fileWriter: " + (end - start) / 1000f + " seconds");
	}

	public void storeUsingFileWriterAsOnce(String fn) throws IOException{
		FileWriter f = new FileWriter(fn);
		String txt="";
		   long start = System.currentTimeMillis();
		   for (int i=0; i< RECORD_COUNT; ++i) {
		        txt+=RECORD;
		    }
		   f.write(txt);
		   f.flush();
		   f.close();
		   long end = System.currentTimeMillis();
		   System.out.println("At Once Using fileWriter: " +(end - start) / 1000f + " seconds");
	}
	
	public void storeUsingBufferedWriter(String fn, int bufSize) throws IOException{
		FileWriter f = new FileWriter(fn);
		BufferedWriter out = new BufferedWriter(f, bufSize);

		   long start = System.currentTimeMillis();
		   for (int i=0; i< RECORD_COUNT; ++i) {
		        out.write(RECORD);
		    }
		    out.flush();
		    out.close();
		    long end = System.currentTimeMillis();
		    System.out.println(bufSize+" Using BufferedWriter: " +(end - start) / 1000f + " seconds");
	}

	public void readUsingBufferedWriter(String fn, int bufSize) throws IOException{
		BufferedReader br = new BufferedReader(new InputStreamReader(new BufferedInputStream(new FileInputStream(fn))),bufSize);
		String line;
		long start = System.currentTimeMillis();
		while ((line= br.readLine())!=null);

		br.close();
		    long end = System.currentTimeMillis();
		    System.out.println(bufSize+" Using BufferedWriter: " +(end - start) / 1000f + " seconds");
	}

	public static void main(String[] args) throws IOException{
		TestRWriteFToFile twtf = new TestRWriteFToFile();
		//twtf.storeUsingFileWriter("/tmp/f1.txt");
		//twtf.storeUsingFileWriterAsOnce("/tmp/f2.txt");
		twtf.readUsingBufferedWriter("/tmp/f3.txt", (int) (1* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f4.txt", (int) (4* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f5.txt", (int) (8* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f6.txt", (int) (16* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f7.txt", (int) (32* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f8.txt", (int) (64* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f9.txt", (int) (128* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f10.txt", (int) (256* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f11.txt", (int) (512* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/f12.txt", (int) ( Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/f13.txt", (int) (4* Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/f14.txt", (int) (64* Setting.MEGA));
	}
}
