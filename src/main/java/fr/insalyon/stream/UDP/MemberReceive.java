package fr.insalyon.stream.UDP;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

/**
 * Thread qui permet de récupérer les messages du client
 */
public class MemberReceive extends Thread{

    private InetAddress groupAddr;
    private int groupPort;
    private MulticastSocket multicastSocket;
    private byte[] buf = new byte[1000];
    private boolean connected;
    private ChatMemberWindow window;

    /**
     * Constructeur de MemberReceive
     * @param PORT le port
     * @param address l'adresse du groupe
     * @param w la fenetre IHM
     */
    public MemberReceive(int PORT, InetAddress address, ChatMemberWindow w){
        groupPort = PORT;
        window = w;
        try {
            groupAddr = address;
            multicastSocket = new MulticastSocket(groupPort);
            multicastSocket.joinGroup(groupAddr);
            connected = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lance le thread et reçoit les messages pour les afficher sur l'IHM
     */
    public void run() {
        while (connected) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength());
                window.addToChat(received);
                System.out.println(received);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            multicastSocket.leaveGroup(groupAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        multicastSocket.close();
    }

    /**
     * Méthode appelée au moment de la déconnexion
     */
    public void disconnect() {
        connected = false;
    }
}
