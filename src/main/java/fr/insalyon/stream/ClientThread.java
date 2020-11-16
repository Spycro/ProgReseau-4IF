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

    ClientThread(Socket s, ChatServer c) {
        this.clientSocket = s;
        server = c;
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
            sendMessage(server.getHistory());
            socOut.println("Username : ");
            username = socIn.readLine();

            String line = "";
            while (!line.equals(".")) {
                line = socIn.readLine();
                server.sendToAll("["+username + "]: " + line, this);
                //socOut.println(line);

            }
            clientSocket.close();
            server.removeThread(this);
        } catch (Exception e) {
            System.err.println("Error in EchoServer:" + e);
        }
    }

    public void sendMessage(String message){
        socOut.println(message);
    }


}

  