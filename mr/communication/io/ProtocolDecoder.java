
package mr.communication.io;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

import mr.dht.peer2peernetwork.wireformates.Message;

public interface ProtocolDecoder {
	public ArrayList<Message> decode(ByteBuffer bBuffer) throws IOException;
	public Message decodeMSG(ByteBuffer bBuffer) throws IOException;
	public boolean stillNeedData();
}
