package fr.insalyon.stream;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

public class ChatClientWindow extends Frame implements ActionListener {

    private JFrame f;
    private JTextField msg;
    private JButton send;
    private JTextArea chatArea;
    private PrintStream socOut;
    private Socket chatSocket;

    public ChatClientWindow (Socket chatSocket){

        setTitle("Chat Client");
        this.chatSocket = chatSocket;
        try {
            socOut = new PrintStream(chatSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame startingFrame = new JFrame("Nom utilisateur");
        startingFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        String username = (String)JOptionPane.showInputDialog(
                startingFrame,
                "Choisir un nom utilisateur",
                "Nom utilisateur",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "Bob"
        );
        if(!username.isEmpty()){
            socOut.println(username);
        }

        f = new JFrame();
        f.setResizable(false);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBounds(10,10, 550, 150);

        JScrollPane scrollPane = new JScrollPane (chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(50,10,600,220);

        JLabel l1 = new JLabel("Message");
        l1.setBounds(50,240, 100,30);

        msg=new JTextField();
        msg.setBounds(50,270, 500,30);

        send = new JButton("Envoyer");
        send.setBounds(550, 270, 100, 30);
        send.addActionListener(this);

        f.add(scrollPane);
        f.add(msg);
        f.add(send);
        f.add(l1);

        f.setSize(700,350);
        f.setLayout(null);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent e) {
        socOut.println(msg.getText());
        msg.setText("");
    }

    public void addToChat(String message){

        chatArea.append(message + "\n");
    }
}
