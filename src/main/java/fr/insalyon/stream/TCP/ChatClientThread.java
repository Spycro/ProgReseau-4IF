package fr.insalyon.stream.TCP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.SocketException;

/**
 * Thread qui permet de récupérer les messages du client
 */
public class ChatClientThread extends Thread{

    private ChatClientWindow window;
    private Socket chatSocket;
    private BufferedReader socIn;
    private boolean running = true;

    /**
     * Constructeur de ChatClientThread
     * @param socket la socket
     * @param w la fenetre IHM
     */
    public ChatClientThread(Socket socket, ChatClientWindow w){
        chatSocket = socket;
        window = w;
    }

    /**
     * Lance le thread et reçoit les messages pour les afficher sur l'IHM
     */
    public void run() {

        try {
            socIn = new BufferedReader(
                    new InputStreamReader(chatSocket.getInputStream()));
        }catch (IOException e){
            System.err.println("Exception while creating inputStream");
        }

        while (running) {
            try{
                String line = socIn.readLine();
                if (line == null) break;
                System.out.println(line);
                window.addToChat(line);
            } catch(SocketException e){
            } catch(IOException e){
                System.err.println("Error while reading from socket " + e);
            }
        }

    }

    /**
     * Méthode appelée pour arrêter le thread
     */
    public void stopThread(){
        running = false;
    }

}
