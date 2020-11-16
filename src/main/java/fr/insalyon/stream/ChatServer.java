/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package fr.insalyon.stream;

import java.net.*;
import java.util.HashSet;
import java.util.Set;

public class ChatServer  {
  
	/**
	* main method
	* @param EchoServer port
	* 
	**/

	private int PORT;
	private Set<ClientThread> clients;
	private ServerSocket listenSocket;
	private ChatHistory chatHistory;

	public ChatServer(){
		PORT = 8000;
		clients = new HashSet<>();
	}

	public ChatServer(int PORT){
		this.PORT = PORT;
		clients = new HashSet<>();
		chatHistory = new ChatHistory();
	}

	public void LaunchServer(){
		try{
			listenSocket = new ServerSocket(PORT);
			System.out.println("[+]Server Ready");
			while(true){
				Socket clientSocket = listenSocket.accept();
				System.out.println("Connexion from : " + clientSocket.getInetAddress());
				ClientThread ct = new ClientThread(clientSocket, this);
				clients.add(ct);
				ct.start();
			}
		} catch(Exception e){
			System.err.println("Error in ChatServer" + e);
		}
	}

	public void sendToAll(String message){
		for(ClientThread client : clients){
			client.sendMessage(message);
		}
	}

	public void sendToAll(String message, ClientThread sent){
		chatHistory.addMessageToHistory(message);
		for(ClientThread client : clients){
			if(client != sent)
				client.sendMessage(message);
		}
	}

	public void removeThread(ClientThread t){
		System.out.println("Removing a client");
		clients.remove(t);
		sendToAll("[SERVER]: A user disconnected");
	}

	public String getHistory(){
		return chatHistory.getHistoryAsString();
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

  