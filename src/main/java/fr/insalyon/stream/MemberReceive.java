package fr.insalyon.stream;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

public class MemberReceive extends Thread{

    private InetAddress groupAddr;
    private int groupPort;
    private MulticastSocket multicastSocket;
    private byte[] buf = new byte[1000];
    private boolean connected;

    public MemberReceive(int PORT, InetAddress address){
        groupPort = PORT;
        try {
            groupAddr = address;
            multicastSocket = new MulticastSocket(groupPort);
            multicastSocket.joinGroup(groupAddr);
            connected = true;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (connected) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                multicastSocket.receive(packet);
                String received = new String(packet.getData(), 0, packet.getLength()+1);
                System.out.println(received);
            } catch (IOException e) {
            }
        }

        try {
            multicastSocket.leaveGroup(groupAddr);
        } catch (IOException e) {
            e.printStackTrace();
        }
        multicastSocket.close();
    }

    public void disconnect() throws IOException {
        connected = false;
    }
}
