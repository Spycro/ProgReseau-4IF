package fr.insalyon.stream.TCP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ChatClientWindow extends Frame implements ActionListener, KeyListener {

    private JFrame f;
    private JTextField msg;
    private JButton send;
    private JTextArea chatArea;
    private PrintStream socOut;
    private Socket chatSocket;

    /**
     * Constructeur de ChatClientWindow
     * @param chatSocket la socket
     */
    public ChatClientWindow (Socket chatSocket){

        setTitle("Chat Client");
        this.chatSocket = chatSocket;
        try {
            socOut = new PrintStream(chatSocket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

        JFrame startingFrame = new JFrame("Nom utilisateur");
        startingFrame.setDefaultCloseOperation(EXIT_ON_CLOSE);

        String username = (String)JOptionPane.showInputDialog(
                startingFrame,
                "Choisir un nom utilisateur",
                "Nom utilisateur",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "Bob"
        );
        if(username != null && !username.isEmpty()){
            socOut.println(username);
        }
        else{
            System.exit(0);
        }

        f = new JFrame();
        f.setTitle(username);
        f.setResizable(false);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);

        chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setBounds(10,10, 550, 150);

        JScrollPane scrollPane = new JScrollPane (chatArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        scrollPane.setBounds(50,10,600,220);

        JLabel l1 = new JLabel("Message");
        l1.setBounds(50,240, 100,30);

        msg=new JTextField();
        msg.setBounds(50,270, 500,25);
        msg.addKeyListener(this);

        send = new JButton("Envoyer");
        send.setBounds(550, 270, 100, 25);
        send.addActionListener(this);

        f.add(scrollPane);
        f.add(msg);
        f.add(send);
        f.add(l1);

        f.setSize(700,350);
        f.setLayout(null);
        f.setVisible(true);
    }

    /**
     * Méthode appelée après avoir cliqué sur le bouton "Envoyer"
     * @param e l'événement capturé
     */
    public void actionPerformed(ActionEvent e) {
        sendMessage(msg.getText());
    }

    /**
     * Méthode appelée pour ajouter un message au chat
     * @param message le message à ajouter
     */
    public void addToChat(String message){
        chatArea.append(message + "\n");
    }

    /**
     * Méthode appelée après avoir appuyé sur la touche entrée a partir de la zone de texte
     * @param e l'événement capturé
     */
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();

        if (key == KeyEvent.VK_ENTER) {
            sendMessage(msg.getText());
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    @Override
    public void keyReleased(KeyEvent e) {}

    /**
     * Méthode appelée pour envoyer un message
     * @param message le message à envoyer
     */
    private void sendMessage(String message){
        socOut.println(message);
        if(message.equals("/leave")){
            System.exit(0);
            try {
                chatSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }else{
            msg.setText("");
        }
    }
}
