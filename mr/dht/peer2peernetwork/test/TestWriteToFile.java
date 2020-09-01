package mr.dht.peer2peernetwork.test;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.resourcemanagement.io.DataSource;

public class TestWriteToFile {
	private static final int ITERATIONS 	= 5;
	private static final int RECORD_COUNT 	= (int) (8* Math.pow(10, 6));
	private static final String RECORD 	= "Help I am trapped in a fortune cookie factory\n";
	private static final int RECSIZE 	= RECORD.getBytes().length;
	
	public void storeUsingFileWriter(String fn) throws IOException{
		FileWriter f 			= new FileWriter(fn);
		   long start 			= System.currentTimeMillis();
		   for (int i = 0; i < RECORD_COUNT; ++i) {
		        f.write(RECORD);
		    }
		    f.flush();
		    f.close();
		    long end 			= System.currentTimeMillis();
		    System.out.println("One By one Using fileWriter: " + (end - start) / 1000f + " seconds");
	}

	public void storeUsingFileWriterAsOnce(String fn) throws IOException{
		FileWriter f 			= new FileWriter(fn);
		String txt			= "";
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
		long nn=0;
		while ((line= br.readLine())!=null) ++nn;

		br.close();
		    long end = System.currentTimeMillis();
		    System.out.println(bufSize+" Using BufferedWriter: " +(end - start) / 1000f + " seconds #####"+nn);
	}
	
	private String getLine(ByteBuffer b, int s, int e){
		byte bt[] = new byte[e-s-1];
		int oldPos = b.position();
		b.position(s);
		b.get(bt);
		b.position(oldPos);
		return new String(bt);
	}
	
	public void readUsingNIO01(String fn, int bufSize) throws IOException{
		ByteBuffer 		buffer 		= ByteBuffer.allocateDirect(bufSize);
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		ArrayList<String> a = new ArrayList<String> ();
		long start = System.currentTimeMillis();
		int bytesRead = 10; 
		long nn=0;
		while (bytesRead != -1 && bytesRead != 0){
			bytesRead = fChannel.read(buffer);
			buffer.flip();
			int p = 0;
			while(buffer.hasRemaining()){
				char c = (char) buffer.get();
				if (c=='\n'){
					getLine(buffer, p, buffer.position() );
					p = buffer.position() ;
					++nn;
				}
			}
			if (p<buffer.limit()){
				
				++nn;
			}

			buffer.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println(bytesRead+"==="+bufSize+" Using NIOmmmmm: " +(end - start) / 1000f + " sec,   "+(end - start)+" ms===>"+nn);
	}


	public void readUsingNIO0(String fn, int bufSize) throws IOException{
		ByteBuffer 		buffer 		= ByteBuffer.allocateDirect(bufSize);
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		long start = System.currentTimeMillis();
		int bytesRead = 10; 
		long nn=0;
		while (bytesRead != -1 && bytesRead != 0){
			bytesRead = fChannel.read(buffer);
			buffer.flip();
			CharBuffer cbuf =buffer.asCharBuffer();
			
			String[] s = cbuf.toString().split(System.getProperty("line.separator"));
			System.out.println(bytesRead+"  ###  "+buffer.capacity()+" pos:"+buffer.position()+"  l:"+buffer.limit()+"#####"+s.length);
			
			//System.out.println(s[0]);
			for (String t:s) ;
			nn+=s.length;
			buffer.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println(bytesRead+"==="+bufSize+" Using NIO: " +(end - start) / 1000f + " sec,   "+(end - start)+" ms===>"+nn);
	}

	public void readUsingNIO(String fn, int bufSize) throws IOException{
		ByteBuffer 		buffer 		= ByteBuffer.allocateDirect(bufSize);
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		long start = System.currentTimeMillis();
		int bytesRead = 10; 
		long nn=0;
		while (bytesRead != -1 && bytesRead != 0){
			bytesRead = fChannel.read(buffer);
			buffer.flip();
			CharBuffer cbuf =buffer.asCharBuffer();
			String s = cbuf.toString();
			int pos = 0, end;
			
			System.out.println(bytesRead+"  ###  "+cbuf.length()+" pos:"+cbuf.position()+"  l:"+cbuf.limit()+"####"+s.indexOf(System.getProperty("line.separator"), pos));
			while ((end = s.indexOf(System.getProperty("line.separator"), pos)) >= 0) {
				String l = s.substring(pos, end);
				pos = end + 1;
				++nn;
			}
			buffer.clear();
		}
		long end = System.currentTimeMillis();
		System.out.println(bytesRead+"==="+bufSize+" Using NIO(indexOf): " +(end - start) / 1000f + " sec,   "+(end - start)+" ms===>"+nn );
	}

	public void readUsingNIO1(String fn, int bufSize) throws IOException{
		FileChannel fChannel 		= new FileInputStream(fn).getChannel();
		ByteBuffer buff = fChannel.map(FileChannel.MapMode.READ_ONLY, 0, fChannel.size());
		long start = System.currentTimeMillis();
		int bytesRead = 10; 
		Charset chars = Charset.forName("UTF-8");
        CharBuffer cbuf = buff.asCharBuffer();
        int nn =0;
        while (cbuf.hasRemaining()){
        	char c = cbuf.get();
        	if (c=='\n')
        		++nn;
        }
     
		long end = System.currentTimeMillis();
		System.out.println(buff.position()+"==="+buff.limit()+"==="+buff.capacity()+" Using NIO(indexOf): " +(end - start) / 1000f + " sec,   "+(end - start)+" ms===>"+nn );
	}

	
	public static void main(String[] args) throws IOException{
		TestWriteToFile twtf = new TestWriteToFile();
		//twtf.storeUsingFileWriter("/tmp/f1.txt");
		//twtf.storeUsingFileWriterAsOnce("/tmp/f2.txt");
/*		twtf.readUsingNIO("/tmp/tst.txt", (int) (1* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (1* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (4* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (4* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (8* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (8* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (16* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (16* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (32* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (32* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (64* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (64* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (128* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (128* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (256* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (256* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (512* Setting.KILO));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (512* Setting.KILO));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) ( Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) ( Setting.MEGA));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (4* Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (4* Setting.MEGA));
		
		twtf.readUsingNIO("/tmp/tst.txt", (int) (64* Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (64* Setting.MEGA));
*/		
		
/*		String s= "ais mnwdb\n,sn bdnwb";
		String[] s1 = s.split(System.getProperty("line.separator"));
		System.out.println(s1.length+"  "+s.indexOf(System.getProperty("line.separator"),0));
*/		
		twtf.readUsingNIO01("/tmp/tst.txt", (int) (128* Setting.MEGA));
		//twtf.readUsingNIO0("/tmp/tst1.txt", (int) (128* Setting.MEGA));
		//twtf.readUsingNIO("/tmp/tst1.txt", (int) (128* Setting.MEGA));
		//twtf.readUsingNIO1("/tmp/tst1.txt", (int) (128* Setting.MEGA));
		twtf.readUsingBufferedWriter("/tmp/tst.txt", (int) (128* Setting.MEGA));
	}
}
