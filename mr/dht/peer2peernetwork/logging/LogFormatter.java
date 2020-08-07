package mr.dht.peer2peernetwork.logging;

import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.LogRecord;

public final class LogFormatter extends Formatter {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    @Override
    public String format(LogRecord record) {
    	//return new Date(record.getMillis()) +" "+ record.getMessage()+LINE_SEPARATOR;
    	return record.getMessage() + LINE_SEPARATOR;
    }
}
