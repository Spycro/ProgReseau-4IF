/***
 * ClientThread
 * Example of a TCP server
 * Date: 14/12/08
 * Authors:
 */

package fr.insalyon.stream.TCP;

import fr.insalyon.stream.TCP.ChatServer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;

/**
 * Thread qui permet de faire communiquer le client avec le serveur
 */
public class ClientThread extends Thread {

    private final ChatServer server;
    private final Socket clientSocket;
    private BufferedReader socIn;
    private PrintStream socOut;
    private String username;
    private int roomNumber;

    /**
     *
     * @param s socket passé par le thread principal
     * @param c le serveur TCP
     */
    ClientThread(Socket s, ChatServer c) {
        this.clientSocket = s;
        server = c;
        roomNumber = 0;
    }

    /**
     * methode de démarrage du thread
     * c'est ici que le serveur ecoute les messages envoyés par le client
     *
     **/
    public void run() {
        try {
            socIn = new BufferedReader(
                    new InputStreamReader(clientSocket.getInputStream()));
            socOut = new PrintStream(clientSocket.getOutputStream());

            sendMessage("Historique des messages : ");
            sendMessage(server.getRoomHistory(roomNumber));
            //socOut.println("Username : ");
            username = socIn.readLine();
            server.sendToAllExceptSender("[SERVER]: " + username +" connected.", this);
            boolean stop = false;
            String line = "";
            while (true) {
/*                if(!line.isEmpty()){
                    server.sendToAll("["+username + "]: " + line);
                }*/

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
                    if(!line.isEmpty()) {
                        server.sendToRoom("[" + username + "]: " + line, roomNumber);
                    }
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

    /**
     *
     * @param message message à envoyé
     */
    public void sendMessage(String message){
        socOut.println(message);
    }

    /**
     *
     * @return username du client actuel
     */
    public String getUsername(){
        return username;
    }

    /**
     *
     * @return numero de room du chat actuel
     */
    public int getRoomNumber(){
        return roomNumber;
    }

    /**
     * methode permettant de changer la room du client
     * @param room numero de room viser
     */
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

  