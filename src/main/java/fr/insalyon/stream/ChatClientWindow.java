package fr.insalyon.stream;

import javax.swing.*;

public class ChatClientWindow {

    public ChatClientWindow (){
        JFrame f = new JFrame();

        JLabel l1 = new JLabel("Username");
        l1.setBounds(130,50, 100,30);

        JButton b = new JButton("Se connecter");
        b.setBounds(130, 100, 100, 40);

        f.add(b);
        f.add(l1);

        f.setSize(400,500);
        f.setLayout(null);
        f.setVisible(true);
    }
}
