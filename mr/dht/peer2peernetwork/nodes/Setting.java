package mr.dht.peer2peernetwork.nodes;

import java.net.InetAddress;
import java.net.UnknownHostException;

import mr.dht.peer2peernetwork.util.UtilClass;

public abstract class Setting {
	public static String	BOLT_DIR		= "/tmp/MRlocalDir/"; //used by peers
	public static String 	HOSTNAME		= "";
	public static String 	LOCAL_DIR		= "";
	public static String 	LOG_DIR			= "";
	public static String	MRJOBS_DIR		= ""; //used by peers
	public static String	DATA_DIR		= "";

    	static{
		try {
			HOSTNAME 			= java.net.InetAddress.getLocalHost().getHostName();
			int pointLoc 			= HOSTNAME.indexOf('.');
			if (pointLoc > 0 )
				HOSTNAME 		= HOSTNAME.substring(0, pointLoc);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	}
	
    	public static final void setPeerFolder(String dir){
    		LOCAL_DIR   				= "/s/"+HOSTNAME+"/"+dir+BOLT_DIR;
    		LOG_DIR 				= LOCAL_DIR+"logging/";
    		MRJOBS_DIR				= LOCAL_DIR+"jobs/";
    		DATA_DIR				= LOCAL_DIR+"data/";
    		for (int i = 0; i < Setting.REPLICATION_FACTOR; ++i){
    			UtilClass.createPath(DATA_DIR + "d"+i);
    			UtilClass.createPath(DATA_DIR + "datasetsMeta"+i);
    			UtilClass.createPath(DATA_DIR + "filesMeta"+i);
    		}
    	}
    
	public static int NUMBER_OF_FT_ENTRIES		= 32;
	public static long RING_KEYSPACE		= (long) Math.pow(2, NUMBER_OF_FT_ENTRIES);
	public static String DISCOVER_NAME		= "discovery";
	public static final String STOP_LIST		= "/s/chopin/b/grad/budgaga/noaa/mrWorkdir/benchmark/english.stop";
	public static int DISCOVER_ID 			= 0;
	public static String DISCOVER_HOST		= "lion";
	public static int DISCOVER_PORT			= 5000;
	public static int FT_UPDATE_TIME		= 30 * 1000; //millisecond : the needed time in FTFixerThread to update finger table
	public static int DISCOVERY_UPDATE_TIME		= 1000; //millisecond : the needed time in updaterThread to update finger table
	public static int MOVE_FILE_CHECK_TIME		= 45 * 1000;
	public static String INPUT_FILE_EXT		= "";
	public static String SOURCE_DIR			= "/tmp/budgaga/pages";//used by StoreData
	public static String MESSSAGE_PACKAGE		= "mr.dht.peer2peernetwork.wireformates.";//used by StoreData
	public static String HANDLER_PACKAGE		= "mr.dht.peer2peernetwork.handlers.";//used by StoreData
	public static String CLIENT_HANDLER_PACKAGE	= "mr.dht.peer2peernetwork.handlers.client_handlers.";//used by resouceManger
	public static String MR_HANDLER_PACKAGE		= "mr.dht.peer2peernetwork.handlers.mr_handlers.";//used by resouceManger	
		
	public static String TASK_PACKAGE		= "mr.resourcemanagement.execution.mrtasks.tasks.";//used to hold mr tasks

	public static String REDUCER_BASENAME		= "%s_r.%04d";
	
	public static boolean CLR_FILES_ON_START	= false;
	public static boolean PRINT_FT_ON_UPDATE	= false;
	public static boolean PRINT_QUERY_MESSAGES	= false;
	public static boolean FLUSH_PEER_DATA		= false;
	public static boolean LOAD_METADATA		= true;
	
	//sizes
	public static int REPLICATION_FACTOR		= 3;
	public static int PEER_CACHE_SIZE			= 10;
	public static int DISCOVER_THREADPOOL_SIZE= 10;
	public static int THREADPOOL_SIZE 		= 10;
	
	public static int 		INPUT_QUEUESIZE 		= 50;
	public static int 		NUM_MAPBUFFERS	 		= 50;
	public static int 		NUM_REDUCEBUFFERS		= 50;
	
	public static int 	KILO					 	= 1024;
	public static int 	MEGA					 	= (int) Math.pow(1024,2);
	public static int 	CHUNK_SIZE		 			= 128 * MEGA;
	public static int 	RECEIVEBUFF_SIZE		 	= 1 * MEGA;
	public static int 	SENDBUFF_SIZE		 		= 800 * KILO;
//	public static int 	REDUCER_BUFSIZE1		 	= 200 * MEGA;
}
