package fr.insalyon.stream;

public class Main {
    public static void main(String args[]) {
        if (args.length != 1) {
            System.out.println("Usage: java EchoServer <EchoServer port>");
            System.exit(1);
        }
        int port = Integer.parseInt(args[0]);
        ChatServer chatServer = new ChatServer(port);
        chatServer.LaunchServer();
    }

}
