package mr.dht.peer2peernetwork.logging;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

import mr.dht.peer2peernetwork.nodes.Setting;
import mr.dht.peer2peernetwork.util.UtilClass;
import mr.dht.peer2peernetwork.wireformates.MessageFactory;

public class FWLogger {
	private static FWLogger instance;	
	private Map<Long,Logger> loggers = new HashMap<Long,Logger>();
	
	private FWLogger() {}
	
	public  static  FWLogger getInstance() {
		if (instance == null){
			instance = new FWLogger();
		}
	    return instance;
	}
	
	 public void log(long jobID, String text){
		 loggers.get(jobID).log(Level.INFO,text);
	 }
	 public void createLogger(long jobID, String logDir, String fileName) throws SecurityException, IOException{
		fileName = logDir +fileName;
		UtilClass.createPath(logDir);
		Logger logger = Logger.getLogger(fileName);
		logger.setUseParentHandlers(false);
		FileHandler handler = new FileHandler(fileName);
		handler.setFormatter(new LogFormatter());
		logger.addHandler(handler);
		loggers.put(jobID, logger);
	}	
		public static void main(String args[]) throws SecurityException, IOException {
			 String logDir = Setting.LOCAL_DIR + "logging/";
			 FWLogger l = FWLogger.getInstance();
			 l.createLogger(3, "c:\\tmp\\t\\", "log3");
			 l.createLogger(7, "c:\\tmp\\t\\", "log7");
			l.log(3,"absce");
			l.log(7,"123mmmmmmmmmmmmm");
			l.log(3,".....");		
		}

}
