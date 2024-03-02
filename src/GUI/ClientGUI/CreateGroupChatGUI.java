package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.tools.MoveListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class CreateGroupChatGUI extends JFrame {
    private static JButton createB;
    private static JButton exitB;
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

    public CreateGroupChatGUI(ChatApplicationGUI chatApplicationGUI){
        this.setSize(400, 205);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setBackground(new java.awt.Color(color2.getRGB()));
        p1.setPreferredSize(new java.awt.Dimension(400, 270));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);


        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        header = new JLabel("Create Group Chat");
        header.setForeground(color1);
        header.setFont(new Font("Ariel",Font.BOLD,16));
        header.setBounds(120,0,200,100);
        p1.add(header);

        nameL = new JLabel("Group Name");
        nameL.setForeground(color1);
        nameL.setFont(new Font("Ariel",Font.BOLD,13));
        nameL.setBounds(50,25,100,100);
        p1.add(nameL);


        usernameTF = new JTextField(1);

        usernameTF.setCaretColor(color1);
        usernameTF.setForeground(color1);
        usernameTF.setHorizontalAlignment(JTextField.CENTER);
        usernameTF.setBounds(50, 90, 300, 30);
        usernameTF.setBackground(new java.awt.Color(color2.getRGB()));
        usernameTF.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        p1.add(usernameTF);


        createB = new JButton("Create");
        createB.setBounds(75, 200, 100, 30);
        createB.setBackground(new java.awt.Color(color6.getRGB()));
        createB.setForeground(color1);
        createB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        createB.addActionListener(evt -> {
            String grpName = usernameTF.getText();
            try {
                if(!grpName.equals("")){
                    grpName = grpName.replaceAll(" ","_");
                    chatApplicationGUI.sendToServer(grpName);
                    JOptionPane.showMessageDialog(null,chatApplicationGUI.readMsgFromServer(),
                            "Group Chat Created",JOptionPane.INFORMATION_MESSAGE);
                    chatApplicationGUI.sendToServer("2");
                    new GroupChatSelectionGUI(chatApplicationGUI);
                    this.dispose();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        p1.add(createB);

        exitB = new JButton("Back");
        exitB.setBounds(235, 200, 100, 30);
        exitB.setBackground(new java.awt.Color(color6.getRGB()));
        exitB.setForeground(color1);
        exitB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exitB.addActionListener(evt -> {
            try {
                chatApplicationGUI.sendToServer(" ");
                JOptionPane.showMessageDialog(null,chatApplicationGUI.readMsgFromServer(),
                        "Creation Cancelled",JOptionPane.INFORMATION_MESSAGE);
                chatApplicationGUI.sendToServer("2");
                new GroupChatSelectionGUI(chatApplicationGUI);
                this.dispose();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        p1.add(exitB);
        this.pack();
        this.setVisible(true);
    }
}
