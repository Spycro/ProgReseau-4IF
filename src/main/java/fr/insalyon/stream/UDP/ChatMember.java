package fr.insalyon.stream.UDP;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class ChatMember {

    private InetAddress groupAddr;
    private int groupPort;
    private MulticastSocket multicastSocket;
    private MemberReceive memberReceive;
    private BufferedReader stdIn = null;
    private String username;

    /**
     * Constructeur de ChatClientWindow
     * @param PORT le port
     * @param addressName l'adresse du groupe
     */
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

    /**
     * Méthode appelée pour lancer l'IHM, le thread d'écoute ; tourne jusqu'a ce que l'utilisateur indique son départ
     */
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

    /**
     * Méthode appelée au moment de quitter le chat
     */
    public void leave() throws IOException {
        sendMessage("[SERVEUR] : Deconnexion de " + username);
        memberReceive.disconnect();
        multicastSocket.close();
    }

    /**
     * Méthode appelée pour envoyer un message
     * @param message le message à envoyer
     */
    public void sendMessage(String message) throws IOException {
        DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), groupAddr, groupPort);
        multicastSocket.send(packet);
    }

    /**
     * Méthode qui retourne le nom de l'utilisateur
     */
    public String getUsername(){
        return username;
    }

    /**
     * Méthode qui met à jour le nom de l'utilisateur
     */
    public void setUsername(String name){
        this.username = name;
    }

    /**
     * méthode main
     * lance le chat
     * @param args Paramètres de la ligne de commande
     **/
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
