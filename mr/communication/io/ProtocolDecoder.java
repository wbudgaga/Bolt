/*
  (c) 2004, Nuno Santos, nfsantos@sapo.pt
  relased under terms of the GNU public license 
  http://www.gnu.org/licenses/licenses.html#TOCGPL
*/
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