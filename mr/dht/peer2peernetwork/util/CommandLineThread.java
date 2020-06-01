package mr.dht.peer2peernetwork.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import mr.dht.peer2peernetwork.nodes.CommandLineInterface;

public class CommandLineThread extends Thread{
	private BufferedReader 	bufferedReader;
	private CommandLineInterface 	userCommand;
	
	public CommandLineThread(CommandLineInterface userInterface){
		this.userCommand =  userInterface;
		bufferedReader 	= new BufferedReader(new InputStreamReader(System.in));
	}

	private void sendData(String command){
		String[] para  = command.split(" ");
		if (para.length==2)
			userCommand.sendData(para[1]);
		else
			System.err.println("Invalid File Name!");
	}
	
	private void search(String command){
		String[] para  = command.split(" ");
		if (para.length > 1)
			userCommand.search(para[1],para.length==3?para[2]:null);
		else
			System.err.println("Invalid usageof search command!");
	}
	
	private String readCommand(){
		try {
			return bufferedReader.readLine();
		} catch (IOException e) {}
		return "quit";
	}
	
	public void run(){
		String command;
		while (true){
			System.out.println("enter your command (print-FT, print, list-files, send-file, send, createSearch, search, quit");
			command = readCommand();
			if (command == null)							continue;
			if(command.compareTo	("print-FT")	==0	)	{userCommand.printFT();				continue;}
			if(command.compareTo	("print")		==0	)	{userCommand.print();				continue;}
			if(command.compareTo	("list-files")	==0	)	{userCommand.listFiles();			continue;}
			if(command.startsWith	("send-file")		)	{sendData(command);					continue;}
			if(command.compareTo	("send")== 0		)	{userCommand.send();				continue;}
			if(command.compareTo	("createSearch")== 0)	{userCommand.createSearch();		continue;}
			if(command.startsWith	("search")			)	{search(command);					continue;}
			if(command.compareTo	("quit")		==0	)	 break;
			System.err.println("Invalid Command!");
		}
	}

}
