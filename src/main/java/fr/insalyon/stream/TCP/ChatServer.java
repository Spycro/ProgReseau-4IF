/***
 * EchoServer
 * Example of a TCP server
 * Date: 10/01/04
 * Authors:
 */

package fr.insalyon.stream.TCP;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.net.*;
import java.util.HashSet;
import java.util.Scanner;
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
	private final String historyPath = "history.txt";

	public ChatServer(){
		PORT = 8000;
		clients = new HashSet<>();
	}

	public ChatServer(int PORT) {
		this.PORT = PORT;
		clients = new HashSet<>();
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
		appendToHistory(message + "\n");
		for(ClientThread client : clients){
			client.sendMessage("[ALL]" + message);
		}
	}

	public void sendToRoom(String message, int roomNumber){
		appendToHistory(roomNumber + ";" + message + "\n");
		for(ClientThread client : clients){
			if(client.getRoomNumber() == roomNumber){
				client.sendMessage( "[" + roomNumber +"]" +message);
			}
		}
	}

	public void sendToAllExceptSender(String message, ClientThread sent) throws IOException {
		for(ClientThread client : clients){
			if(client != sent)
				client.sendMessage("[ALL]"+message);
		}
	}

	public void removeThread(ClientThread t){
		System.out.println("Removing a client");
		clients.remove(t);
		sendToAll("[SERVER]: User "+ t.getUsername() +" disconnected");
	}

	public String getHistory(){

		StringBuilder history = new StringBuilder();

		try {
			File historyFile = new File (historyPath);
			if(historyFile.exists()){
				Scanner reader = new Scanner(historyFile);
				while (reader.hasNextLine()){
					String data = reader.nextLine();
					history.append(data + "\n");
				}
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return history.toString();
	}

	public String getRoomHistory(int roomNumber){
		StringBuilder history = new StringBuilder();

		try {
			File historyFile = new File (historyPath);
			if(historyFile.exists()){
				Scanner reader = new Scanner(historyFile);
				while (reader.hasNextLine()){
					String data = reader.nextLine();
					String [] dataArray = data.split(";");
					if(dataArray.length == 2){
						int room = Integer.parseInt(dataArray[0]);
						if(room == roomNumber)
							history.append(dataArray[1]).append("\n");
					}

				}
				reader.close();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		return history.toString();
	}

	private void appendToHistory(String msg){

		try {
			FileWriter out = new FileWriter(historyPath, true);
			out.write(msg);
			out.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		if (args.length != 1) {
			System.out.println("Usage: java ChatServer <ChatServer port>");
			System.exit(1);
		}
		int port = Integer.parseInt(args[0]);
		ChatServer chatServer = new ChatServer(port);
		chatServer.LaunchServer();
	}
}

  