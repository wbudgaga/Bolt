package mr.dht.peer2peernetwork.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.MurmurHash3.LongPair;
import mr.dht.peer2peernetwork.wireformates.Message;


public class UtilClass {
	private static Random randomGenerater 		= new Random();
	private static long	  keyspaceQuarter 	= Setting.RING_KEYSPACE / 4;
	private static HashFunction hFunction;
	static{
		try {
			hFunction = new HashFunction();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	public static long getPeerID(long randomNum, int peerIDX){
		return (hFunction.hash(randomNum) + (peerIDX * keyspaceQuarter)) % Setting.RING_KEYSPACE;
	}

	public static long hashMKey(long key){
		return Math.abs(MurmurHash3.hash32(key));// & Long.MAX_VALUE) ;//% Setting.RING_KEYSPACE;
//		return (MurmurHash3.hash128Low(key) & Long.MAX_VALUE) % Setting.RING_KEYSPACE;
	}
	public static long hashMKey(String key){
		return Math.abs(MurmurHash3.hash32(key)) ;/*& Long.MAX_VALUE)*/ 
		//return (MurmurHash3.hash128Low(key) & Long.MAX_VALUE) % Setting.RING_KEYSPACE;
	}
	public static long hashAKey1(long key){
		return (hFunction.hash(key)) % Setting.RING_KEYSPACE;
	}
	public static long hashAKey1(String key){
		return (hFunction.hash(key)) % Setting.RING_KEYSPACE;
	}

	public static long getPeerID1(){
		return hashAKey1(hFunction.hash(System.nanoTime()));
	}
	
	public static long GetReduceKey(String outputName, int idx){
		return hashMKey( String.format(Setting.REDUCER_BASENAME, outputName,idx));
	}

	public static void flushData(Message msg, String fileName) throws IOException{
		FileChannel outputChannel 		= new FileOutputStream(fileName).getChannel();		
		outputChannel.write(ByteBuffer.wrap(msg.packMessage()));
		outputChannel.close();
	}
	public static void loadData(Message msg, String fileName) throws IOException{
		//System.out.println("loading setting data from "+fileName);
		FileChannel inputChannel 		= new FileInputStream(fileName).getChannel();
		ByteBuffer buff = ByteBuffer.allocateDirect((int) inputChannel.size());
		inputChannel.read(buff);
		byte[] bytes    = new byte[(int) inputChannel.size()];
		buff.flip();
		buff.get(bytes);
		msg.initiate(bytes);
		inputChannel.close();
	}


	public static void storeFile(File file, String text){
		try {
			BufferedWriter out = new BufferedWriter(new FileWriter(file));
			out.write(text);
			out.flush();
			out.close();	
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
	}
	
	public static String getStopList() throws IOException{
		String stopList="";
		File file = new File(Setting.STOP_LIST); 
		BufferedReader input =  new BufferedReader(new FileReader(file));
		String line = null; 
		while (( line = input.readLine()) != null)
			stopList += "\\b" + line + "\\b" + "|";
		 
		 return stopList.substring(0, stopList.length() - 1) ;
	}

	 public static PrintStream fileAsOutputDst(String filename){
		try {
			File file = new File(filename);  
			FileOutputStream fis = new FileOutputStream(file);
			PrintStream out = new PrintStream(fis);  
			System.setOut(out);
			return out;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	 public static void storeData(String d){
			PrintStream out = UtilClass.fileAsOutputDst(d);
			System.out.println("test");
			out.close();
	 }
	public static void createFile(File dir, String fileName, String ext, String text){
		File file = new File(dir,fileName+ext);
		storeFile(file,text);
	}

	public  static long getRandomNumber(long min, long max){
		long r = (long) Math.round(randomGenerater.nextFloat() * (max-min));
		return r+ min;
	}

	public  static int getRandomNumber(int min, int max){
		int r = (int) Math.round(randomGenerater.nextFloat() * (max-min));
		return r+ min;
	}

	
	public static Map<String, Float> sortByValue(Map<String, Float> map) {
        List<Map.Entry<String, Float>> list = new LinkedList<Map.Entry<String, Float>>(map.entrySet());

        Collections.sort(list, new Comparator<Map.Entry<String, Float>>() {

            public int compare(Map.Entry<String, Float> m1, Map.Entry<String, Float> m2) {
                return (m2.getValue()).compareTo(m1.getValue());
            }
        });

        Map<String, Float> result = new LinkedHashMap<String, Float>();
        for (Map.Entry<String, Float> entry : list) {
            result.put(entry.getKey(), entry.getValue());
        }
        return result;
    }

	public static void printRowSeparator(){
		System.out.println("======================================================");
	}
	public static void printHeader(){
		printRowSeparator();
		System.out.println(String.format(" %-10s  %-20s  ",		"File Name",	"ID(int)"));
		printRowSeparator();
	}


/*	public static void listFiles(String rootDir){
		try{
			File dir = new File(rootDir);
			String[] contents 	= dir.list();
			if (contents.length==0){
				System.out.println("There is not any files yet!");
				return;
			}	
			printHeader();
			for(String fileName : contents){
				System.out.println(String.format(" %-10s  %-20s   ",	fileName,getHashCode(fileName)));
			}		
			printRowSeparator();
		}catch(NullPointerException e){}

	}
*/	
	// reads all files names existing in dataFolder and having the extension ext
	public static ArrayList<String> readFilesFromDir(String dataFolder){
		ArrayList<String> dirFiles 	= new ArrayList<String> ();
		File dir 					= new File(dataFolder);
		for (File f : dir.listFiles()) {
	        dirFiles.add(f.getAbsolutePath());
	    }
		return dirFiles;
	}

	// reads all files names existing in dataFolder and having the extension ext
	public static ArrayList<String> readFilesFromDir(String dataFolder, String ext){
		ArrayList<String> dirFiles 	= new ArrayList<String> ();
		File dir 					= new File(dataFolder);
		for (File f : dir.listFiles()) {
	        String fileName = f.getName();
	        if (fileName.endsWith(ext)) {
	        	dirFiles.add(f.getAbsolutePath());
	        }
		}
		return dirFiles;
	}

	public static boolean createPath(String dir){
		File theDir = new File(dir);
		if (theDir.exists())
			return true;
		return theDir.mkdirs();
	}

	public static void removeDir(String rootDir){
		try{
			File dir = new File(rootDir);
			for(File file : dir.listFiles()){
				file.delete();
			}	
		}catch(NullPointerException e){}
	}
	public static void main(String args[]) throws IOException {
		
			String datasetName ="file99";
			
			System.out.println(UtilClass.hashMKey(datasetName)+ "  "+Integer.MAX_VALUE+"   "+String.format(Setting.REDUCER_BASENAME, "outputName",3));
/*			String cn =  datasetName+"_%s.%04d" ;
			System.out.println(cn);
			//System.out.println(String.format(cn,"file1"));
			System.out.println(String.format(cn,"file1",11));
		
*/
/*		try {
			System.out.println(getStopList());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
*/		
/*		ArrayList<String> lst = readFilesFromDir("C:\\tmp","pdf");
		for (String f : lst) {
			System.out.println(f);
		}
*/	}
	
	public static long getUUID(){
		return UUID.randomUUID().getLeastSignificantBits();
	}

	public static String getCurrDateTimeAsString(String format){
		SimpleDateFormat sdf = new SimpleDateFormat(format);    
		Date resultdate = new Date(System.currentTimeMillis());
		return sdf.format(resultdate);
	}
}
