/***
 * EchoClient
 * Example of a TCP client 
 * Date: 10/01/04
 * Authors:
 */
package fr.insalyon.stream.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;


public class ChatClientTCP {

    /**
     * méthode main
     * établit la connexion avec le serveur, lance l'IHM et le thread d'écoute
     * @param args Paramètres de la ligne de commande
     **/
    public static void main(String[] args) throws IOException {

        BufferedReader stdIn = null;

        if (args.length != 2) {
            System.out.println("Usage: java EchoClient <ChatClient host> <ChatClient port>");
            System.exit(1);
        }

        String host = args[0];
        int port = Integer.parseInt(args[1]);

        try {
            // creation socket ==> connexion
            Socket chatSocket = new Socket(host, port);
            PrintStream socOut = new PrintStream(chatSocket.getOutputStream());
            ChatClientWindow window = new ChatClientWindow(chatSocket);

            ChatClientThread cct = new ChatClientThread(chatSocket, window);
            cct.start();
            stdIn = new BufferedReader(new InputStreamReader(System.in));
            String line;
            while (!socOut.checkError()) {
                line = stdIn.readLine();
                socOut.println(line);
                if (line.equals("/leave")) break;
            }
            System.out.println("Closing connection");
            chatSocket.close();
            cct.stopThread();
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host:" + host);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for "
                    + "the connection to:" + host);
            System.exit(1);
        }
    }
}


