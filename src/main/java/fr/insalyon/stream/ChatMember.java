package fr.insalyon.stream;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class ChatMember {

    private InetAddress groupAddr;
    private int groupPort;
    private MulticastSocket multicastSocket;
    private MemberReceive memberReceive;
    private BufferedReader stdIn = null;
    private String username;

    public ChatMember(int PORT, String addressName){
        this.groupPort = PORT;
        try {
            groupAddr = InetAddress.getByName(addressName);
            multicastSocket = new MulticastSocket(groupPort);
            multicastSocket.joinGroup(groupAddr);
            stdIn = new BufferedReader(new InputStreamReader(System.in));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void launchChatMember() throws IOException {
        //System.out.println("Entrer votre nom utilisateur : ");
        //username = stdIn.readLine();
        ChatMemberWindow window = new ChatMemberWindow(multicastSocket, this);
        memberReceive = new MemberReceive(groupPort, groupAddr, window);
        memberReceive.start();
        sendMessage("[SERVEUR] : Connexion de " + username);
        String line;
        while (true) {
            line = stdIn.readLine();
            if (line.equals("/leave")) break;
            sendMessage("[" + username + "] : " + line);
        }
        leave();
    }

    public void leave() throws IOException {
        sendMessage("[SERVEUR] : Deconnexion de " + username);
        memberReceive.disconnect();
        multicastSocket.close();
    }

    public void sendMessage(String message) throws IOException {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), groupAddr, groupPort);
        multicastSocket.send(packet);
    }

    public String getUsername(){
        return username;
    }

    public void setUsername(String name){
        this.username = name;
    }

    public static void main(String[] args) throws IOException {
        if (args.length != 2) {
            System.out.println("Usage: java ChatMember <ChatMember host> <ChatMember addressName>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        String addressName = args[1];
        ChatMember chatMember = new ChatMember(port, addressName);
        chatMember.launchChatMember();
    }
}
