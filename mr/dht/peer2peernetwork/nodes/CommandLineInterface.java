package mr.dht.peer2peernetwork.nodes;

public interface CommandLineInterface {

	public void printFT();
	public void print();
	public void sendData(String srcFile);
	public void send();
	public void createSearch();
	public void listFiles();
	public void search(String word1, String word2);
}
