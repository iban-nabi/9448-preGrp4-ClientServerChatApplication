package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.tools.MoveListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.Socket;

public class ClientMenuGUI extends JFrame {
    private final Socket socket;
    public JPanel p1;
    public JButton globalB, groupB, privateB, logoutB;
    public JLabel userL;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public ClientMenuGUI(ChatApplicationGUI chatApplication){
        this.socket = chatApplication.getSocket();
        //Frame
        this.setSize(700, 445);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        //Panel
        this.p1 = new JPanel(new GridLayout(2, 2));
        p1.setBackground(new java.awt.Color(color1.getRGB()));

        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        //Label
        this.userL = new JLabel("Hello, " + LoginGUI.getUsername() +"!", SwingConstants.CENTER);
        userL.setBounds(270, 70, 150,25);
        userL.setFont(new Font("SansSerif", Font.BOLD,15 ));
        userL.setForeground (color3);
        this.p1.add(userL);

        //Button
        this.globalB = new JButton("Global Chat");
        globalB.setBounds(100, 120, 150, 150);
        globalB.setBackground(new java.awt.Color(color3.getRGB()));
        globalB.setForeground(color1);
        globalB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        globalB.addActionListener(e -> {
            try {
                chatApplication.selectChatType(1); // sends 1 to ChatServerHandlerGUI for global chat
                new MessagePlatformGUI(chatApplication,"Global Chat", "Global");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.dispose();
        });
        this.p1.add(globalB);

        this.groupB = new JButton("Group Chat");
        groupB.setBounds(270, 120, 150, 150);
        groupB.setBackground(new java.awt.Color(color2.getRGB()));
        groupB.setForeground(color1);
        groupB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        groupB.addActionListener(e -> {
            try {
                chatApplication.selectChatType(2); // sends 2 to ChatServerHandlerGUI for group chat
                new GroupChatSelectionGUI(chatApplication);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.dispose();
        });
        this.p1.add(groupB);

        this.privateB = new JButton("Private Chat");
        privateB.setBounds(440, 120, 150, 150);
        privateB.setBackground(new java.awt.Color(color4.getRGB()));
        privateB.setForeground(color1);
        privateB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        privateB.addActionListener(e -> {
            try {
                chatApplication.selectChatType(3); // sends 3 to ChatServerHandlerGUI for private chat
                new PrivateChatSelectionGUI(chatApplication);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            this.dispose();
        });
        this.p1.add(privateB);

        this.logoutB = new JButton("Logout");
        logoutB.setBounds(440, 300, 150,50);
        logoutB.setBackground(color6);
        logoutB.setForeground(color1);
        logoutB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        logoutB.addActionListener(e ->{
            try {
                chatApplication.selectChatType(4); // sends 4 to ChatServerHandlerGUI for logout
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Thank you for using this Program.",
                    "Logging Out", JOptionPane.INFORMATION_MESSAGE);
            this.dispose();
        });
        this.p1.add(logoutB);

        this.setVisible(true);


    }
}
