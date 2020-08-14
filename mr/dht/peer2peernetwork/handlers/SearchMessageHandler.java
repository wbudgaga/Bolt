package cs555.a3.chordpeer2peernetwork.handlers;

import java.io.File;
import java.io.IOException;
import java.net.Socket;

import javax.annotation.processing.Messager;

import cs555.a3.chordpeer2peernetwork.communiction.ConnectionManager;
import cs555.a3.chordpeer2peernetwork.communiction.ReceivingTask;
import cs555.a3.chordpeer2peernetwork.nodes.Node;
import cs555.a3.chordpeer2peernetwork.nodes.Search;
import cs555.a3.chordpeer2peernetwork.nodes.Setting;
import cs555.a3.chordpeer2peernetwork.util.ByteStream;
import cs555.a3.chordpeer2peernetwork.wireformates.Get;
import cs555.a3.chordpeer2peernetwork.wireformates.GetResponse;
import cs555.a3.chordpeer2peernetwork.wireformates.MessageFactory;
import cs555.a3.chordpeer2peernetwork.wireformates.QueryResult;

public class SearchMessageHandler extends MessageHandler{
	public String	sourceDir;
	
	public SearchMessageHandler(Node node) {
		super(node);
		sourceDir 					= new File(Setting.SOURCE_DIR).listFiles()[0].getAbsolutePath();
		sourceDir 					= new File(sourceDir,"search").getAbsolutePath();
	}
	
	public void addLinks(String linkScore){
		String[] linksList 				= linkScore.split(System.getProperty("line.separator")) ;
		for (int i = 0; i < linksList.length; ++i ){
			String[] linkScr = linksList[i].split(",");
			((Search)node).addResult(linkScr[0], Float.parseFloat(linkScr[1]));
		}
	}

	public void handleQueryResult(Socket link, QueryResult0 queryResult) {
		if (queryResult.getPeer() != null){
			System.out.println(" search response received !");
			Get msg = new Get();
			msg.setFileName(((Search)node).word);
			try {
				ConnectionManager.sendData(link.getOutputStream(), msg);
				byte[] msgBytes = ReceivingTask.receiveMessageFrom(link.getInputStream());
				GetResponse response = (GetResponse) MessageFactory.getInstance().createMessage(msgBytes);
				System.out.println(((Search)node).word+"##  ressponse received "+response.getStatus());
				if (response.getStatus() != -1)
					addLinks(ByteStream.byteArrayToString(response.getFileBytes()));
				((Search)node).setResultCount(1);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
	}	

}
