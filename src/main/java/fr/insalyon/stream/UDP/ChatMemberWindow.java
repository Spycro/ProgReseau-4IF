package fr.insalyon.stream.UDP;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.net.MulticastSocket;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class ChatMemberWindow extends Frame implements ActionListener, KeyListener, WindowListener {

    private JFrame f;
    private JTextField msg;
    private JButton send;
    private JTextArea chatArea;
    private MulticastSocket mSocket;
    private ChatMember chatMember;


    /**
     *
     * @param chatSocket Mutlicast socket utlisiser pour envoyer des messages
     * @param chatMember Client utilisé
     */
    public ChatMemberWindow (MulticastSocket chatSocket, ChatMember chatMember){

        setTitle("Chat Client");
        this.mSocket = chatSocket;
        this.chatMember = chatMember;

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
            chatMember.setUsername(username);
        }
        else{
            System.exit(0);
        }

        f = new JFrame();
        f.setTitle(username);
        f.setResizable(false);
        f.setDefaultCloseOperation(EXIT_ON_CLOSE);
        f.setLocationRelativeTo(null);
        f.addWindowListener(this);

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

    public void actionPerformed(ActionEvent e) {
        sendMessage(msg.getText());
    }

    /**
     *
     * @param message message a ajouter au JPanel
     */
    public void addToChat(String message){
        chatArea.append(message + "\n");
    }

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
     * permet l'envoi d'un message au server
     * @param message message a envoyer
     */
    private void sendMessage(String message) {
        if(message.equals("/leave")){
            try {
                chatMember.leave();
            } catch (IOException e) {
                e.printStackTrace();
            }
            System.exit(0);
        }else{
            try {
                chatMember.sendMessage("[" + chatMember.getUsername() + "] : " + message);
            } catch (IOException e) {
                e.printStackTrace();
            }
            msg.setText("");
        }
    }

    @Override
    public void windowOpened(WindowEvent e) {}

    @Override
    public void windowClosing(WindowEvent e) {
        try {
            chatMember.leave();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    @Override
    public void windowClosed(WindowEvent e) {}

    @Override
    public void windowIconified(WindowEvent e) {}

    @Override
    public void windowDeiconified(WindowEvent e) {}

    @Override
    public void windowActivated(WindowEvent e) {}

    @Override
    public void windowDeactivated(WindowEvent e) {}
}
