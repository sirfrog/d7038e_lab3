/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package mygame;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import mygame.Lab3;



/**
 *
 * @author sirfrog
 */
public class Startup implements ActionListener{
    
    private JFrame frame;
    private JButton button;
    private JTextField nick;
    private JTextField address;
    private String serverAddress;
    private String nickname;
    private Lab3 app;
    
    public Startup() {
        frame = new JFrame("Cannon-Can Game");
        JPanel panel = new JPanel();
        nick = new JTextField(8);
        address = new JTextField(20);
        JLabel namelabel = new JLabel("Please enter nickname (max 8 chars).");
        JLabel namelabel2 = new JLabel("Longer names will be cut to size.");
        JLabel serverlabel = new JLabel("Please enter server address.");
        panel.setLayout(new FlowLayout());
        button = new JButton();
        button.setText("Play!");
        button.addActionListener(this);
        panel.add(namelabel);
        panel.add(namelabel2);
        panel.add(nick);
        panel.add(serverlabel);
        panel.add(address);
        panel.add(button);
        frame.add(panel);
        frame.setSize(300, 300);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent evt) {
        serverAddress = address.getText();
        nickname = nick.getText().substring(0, Math.min(nick.getText().length(), 
        Constants.MAX_CHARS));
        frame.dispose();
        app = new Lab3(nickname, serverAddress);
        new Thread(new Runnable(){
            public void run(){
                app.start();
            }
        }).start();
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
    public static void main(String[] args) {
        Startup gui = new Startup();
        
    }
}
