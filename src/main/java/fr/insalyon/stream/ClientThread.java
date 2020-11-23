/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package fr.insalyon.stream;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

public class ClientThread extends Thread {

    private final ChatServer server;
    private final Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;
    private String username;
    private int roomNumber;

    ClientThread(Socket s, ChatServer c) {
        this.clientSocket = s;
        server = c;
        roomNumber = 0;
    }

    /**
     *
     *
     *
     **/
    public void run() {
        try {
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            socOut = new PrintStream(clientSocket.getOutputStream());

            sendMessage("Historique des messages : ");
            sendMessage(server.getRoomHistory(roomNumber));
            socOut.println("Username : ");
            username = socIn.readLine();
            server.sendToAllExceptSender("[SERVER]: " + username +" connected.", this);
            boolean stop = false;
            String line = "";
            while (true) {
                if(!line.isEmpty()){
                    server.sendToAll("["+username + "]: " + line);
                }

                line = socIn.readLine();
                if(line.startsWith("/")){
                    if(line.equals("/leave")) {
                        stop = true;
                    }
                    else if(line.startsWith("/join")){
                        String roomNumberStr = line.substring(6);
                        try {
                            changeRoom(Integer.parseInt(roomNumberStr));
                        }catch(NumberFormatException e){
                            sendMessage("[SERVER]: please provide a valid room number");
                        }
                    }
                    else if(line.equals("/help")){
                        StringBuilder b = new StringBuilder();
                        b.append("[SERVER]: Command list :\n");
                        b.append("[SERVER]: /leave : leave chat\n");
                        b.append("[SERVER]: /join {room_number} : join room number room_number (only number)\n");
                        sendMessage(b.toString());
                    }
                    else{
                        sendMessage("[SERVER]: I don't know about this command");

                    }

                }
                else{
                    server.sendToRoom("["+username + "]: " + line, roomNumber);
                }
                if(stop) break;

            }
            clientSocket.close();
            server.removeThread(this);
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
            server.removeThread(this);
        }
    }

    public void sendMessage(String message){
        socOut.println(message);
    }

    public String getUsername(){
        return username;
    }

    public int getRoomNumber(){
        return roomNumber;
    }

    public void changeRoom(int room){
        int oldRoom = roomNumber;
        String roomHistory = server.getRoomHistory(room);
        roomNumber = room;
        sendMessage("Room " + roomNumber + " History");
        sendMessage(roomHistory);
        server.sendToRoom("[SERVER]: " + username + " joined room " + roomNumber, roomNumber);
        server.sendToRoom("[SERVER]: " + username + " left room.", oldRoom);
    }
}

  