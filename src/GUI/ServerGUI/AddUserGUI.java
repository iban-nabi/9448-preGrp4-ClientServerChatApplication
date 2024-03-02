package GUI.ServerGUI;

import GUI.ServerGUI.ManageUserGUI;
import chat_server.UserManagerFINAL;
import chat_server.tools.MoveListener;

import javax.swing.*;
import java.awt.*;

public class AddUserGUI extends JFrame {
    private static JButton registerB;
    private static JTextField usernameTF;
    private static JPasswordField passwordField;
    private static JLabel nameL, passL, header;
    private static JPanel p1;

    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public AddUserGUI(){
        this.setSize(400, 205);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setBackground(new java.awt.Color(color3.getRGB()));
        p1.setPreferredSize(new java.awt.Dimension(400, 270));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        header = new JLabel("Create User");
        header.setForeground(color1);
        header.setFont(new Font("Ariel",Font.BOLD,16));
        header.setBounds(150,0,100,100);
        p1.add(header);

        nameL = new JLabel("Username");
        nameL.setForeground(color1);
        nameL.setFont(new Font("Ariel",Font.BOLD,13));
        nameL.setBounds(50,25,100,100);
        p1.add(nameL);

        passL = new JLabel("Password");
        passL.setForeground(color1);
        passL.setFont(new Font("Ariel",Font.BOLD,13));
        passL.setBounds(50,85,100,100);
        p1.add(passL);


        usernameTF = new JTextField(1);
        passwordField = new JPasswordField(1);

        usernameTF.setCaretColor(color1);
        usernameTF.setForeground(color1);
        usernameTF.setHorizontalAlignment(JTextField.CENTER);
        usernameTF.setBounds(50, 90, 300, 30);
        usernameTF.setBackground(new java.awt.Color(color2.getRGB()));
        usernameTF.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        p1.add(usernameTF);


        passwordField.setCaretColor(color1);
        passwordField.setForeground(color1);
        passwordField.setHorizontalAlignment(JTextField.CENTER);
        passwordField.setBounds(50, 150, 300, 30);
        passwordField.setBackground(new java.awt.Color(color2.getRGB()));
        passwordField.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        p1.add(passwordField);

        registerB = new JButton("Create");
        registerB.setBounds(145, 200, 100, 30);
        registerB.setBackground(new java.awt.Color(color6.getRGB()));
        registerB.setForeground(color1);
        registerB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        registerB.addActionListener(evt -> {
            String un = usernameTF.getText();
            String pw = passwordField.getText();
            boolean canCreate = UserManagerFINAL.addUser(un, pw);
            if(canCreate){
                JOptionPane.showMessageDialog(null, "User Created!",
                        "Add User Success", JOptionPane.INFORMATION_MESSAGE);
            }else {
                JOptionPane.showMessageDialog(null, "Name already exists",
                        "Add User Failed", JOptionPane.ERROR_MESSAGE);
            }
            new ManageUserGUI();
            this.dispose();
        });
        p1.add(registerB);

        this.pack();
        this.setVisible(true);
    }
}
