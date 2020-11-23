package fr.insalyon.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;

public class ChatClientThread extends Thread{

    private ChatClientWindow window;
    private Socket chatSocket;
    private BufferedReader socIn;
    private boolean running = true;

    public ChatClientThread(Socket socket, ChatClientWindow w){
        chatSocket = socket;
        window = w;
    }

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

    public void stopThread(){
        running = false;
    }

}
