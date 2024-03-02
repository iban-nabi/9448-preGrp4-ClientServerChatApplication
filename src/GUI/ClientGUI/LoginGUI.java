package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.Encryption;

import chat_server.tools.MoveListener;
import org.xml.sax.SAXException;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.*;
import java.awt.event.ActionEvent;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

public class LoginGUI extends JFrame {
    private static String address;
    private static String username;
    public JPanel p1, p2, p3;
    public JTextField nameTF, hostAddressTF;
    public JButton loginB;
    public JLabel usernameL, passwordL, hostAddressL;
    public JPasswordField passwordField;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    ChatApplicationGUI chatApplication = new ChatApplicationGUI(getAddress());

    public LoginGUI() {
        //Frame
        this.setSize(700, 445);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        //Panel
        this.p1 = new JPanel(new GridLayout(2, 2));
        p1.setBackground(new java.awt.Color(color1.getRGB()));
        this.p2 = new JPanel(new GridLayout(1, 1));
        p2.setBackground(new java.awt.Color(color1.getRGB()));
        this.p3 = new JPanel(new FlowLayout());
        p3.setBackground(new java.awt.Color(color1.getRGB()));

        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        //Button
        this.loginB = new JButton("Login");
        loginB.setBounds(195, 270, 310, 30);
        loginB.setBackground(new java.awt.Color(color3.getRGB()));
        loginB.setForeground(color1);
        loginB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loginB.addActionListener(evt -> {
            try {
                b1Performed(evt);
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        });
        this.p1.add(this.loginB);

        //Text Area and Labels
        this.nameTF = new JTextField(1);
        this.passwordField = new JPasswordField(1);
        this.hostAddressTF = new JTextField(1);
        this.usernameL = new JLabel("Username");
        this.passwordL = new JLabel("Password");
        this.hostAddressL = new JLabel("Host Address");

        this.p1.add(this.usernameL);
        usernameL.setBounds(200, 90, 80, 25);
        usernameL.setForeground(color3);

        this.p1.add(this.passwordL);
        passwordL.setBounds(200, 150, 80, 25);
        passwordL.setForeground(color3);

        this.p1.add(this.hostAddressL);
        hostAddressL.setBounds(200, 210, 80, 25);
        hostAddressL.setForeground(color3);

        this.p1.add(this.nameTF);
        nameTF.setCaretColor(color1);
        nameTF.setForeground(color1);
        nameTF.setHorizontalAlignment(JTextField.CENTER);
        nameTF.setBounds(195, 110, 310, 30);
        nameTF.setBackground(new java.awt.Color(color2.getRGB()));
        nameTF.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.p1.add(this.passwordField);
        passwordField.setCaretColor(color1);
        passwordField.setForeground(color1);
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBounds(195, 170, 310, 30);
        passwordField.setBackground(new java.awt.Color(color2.getRGB()));
        passwordField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.p1.add(this.hostAddressTF);
        hostAddressTF.setCaretColor(color1);
        hostAddressTF.setForeground(color1);
        hostAddressTF.setHorizontalAlignment(JTextField.CENTER);
        hostAddressTF.setBounds(195, 230, 310, 30);
        hostAddressTF.setBackground(new java.awt.Color(color2.getRGB()));
        hostAddressTF.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.pack();
        this.setVisible(true);
    }

    private void b1Performed(ActionEvent evt) throws ParserConfigurationException, IOException, SAXException {
        int condition;
        setUsername(nameTF.getText());
        setAddress(hostAddressTF.getText());

        String pass = "";
        try {
            SecretKey passKey = Encryption.generateKeyFromPassword(passwordField.getText());
            pass = Encryption.convertSecretKeyToString(passKey);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        if (!chatApplication.hostAddressChecker(getAddress())) {
            JOptionPane.showMessageDialog(null, "Invalid Host Address",
                    "Login Failed", JOptionPane.ERROR_MESSAGE);
        } else {
            condition = chatApplication.loginUser(getUsername(), pass);
            switch (condition) { //returns 0 if found, returns 1 if blocked, returns 2 if online, returns 3 if invalid
                case 0 -> {
                    new ClientMenuGUI(chatApplication);
                    this.dispose();
                }
                case 1 -> JOptionPane.showMessageDialog(null, "User is Blocked by the Admin",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                case 2 -> JOptionPane.showMessageDialog(null, "User is Already Online",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
                case 3 -> JOptionPane.showMessageDialog(null, "Username/Password is Invalid",
                        "Login Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    public static String getAddress() {
        return address;
    }

    public static void setAddress(String address) {
        LoginGUI.address = address;
    }


    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        LoginGUI.username = username;
    }
}
