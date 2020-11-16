/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package fr.insalyon.stream;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

public class ChatServer  {
  
	/**
	* main method
	* @param EchoServer port
	* 
	**/

	private int PORT;
	private List<ClientThread> clients;
	private ServerSocket listenSocket;


	public ChatServer(){
		PORT = 8000;
		clients = new ArrayList<>();
	}

	public ChatServer(int PORT){
		this.PORT = PORT;
		clients = new ArrayList<>();
	}

	public void LaunchServer(){
		try{
			listenSocket = new ServerSocket(PORT);
			System.out.println("[+]Server Ready");
			while(true){
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from : " + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket);
				clients.add(ct);
				ct.start();
			}


		} catch(Exception e){
			System.err.println("Error in ChatServer" + e);
		}
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java EchoServer <EchoServer port>");
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		ChatServer chatServer = new ChatServer(port);
		chatServer.LaunchServer();
	}
}

  