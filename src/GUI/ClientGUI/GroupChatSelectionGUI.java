package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.User;
import chat_server.tools.MoveListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class GroupChatSelectionGUI extends JFrame {
    public JPanel p1;
    public JButton enterB, createB, favoriteB, backB;
    public JLabel usersL;
    public JList<String> listOfGrpChats;
    public static ArrayList<String> grpChatArray;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public GroupChatSelectionGUI(ChatApplicationGUI chatApplicationGUI) throws IOException {
        //Populate
        populateArray(chatApplicationGUI);
        //Frame
        this.setSize(700,445);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setBackground(new java.awt.Color(color2.getRGB()));
        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        //Labels
        usersL = new JLabel("Your Group Chats:");
        usersL.setForeground(color1);
        usersL.setFont(new Font("Ariel",Font.BOLD,30));
        usersL.setBounds(40,1,500,100);
        p1.add(usersL);

        //Lists
        String[] array = grpChatArray.toArray(new String[0]);
        listOfGrpChats = new JList<>(array);
        listOfGrpChats.setVisibleRowCount(5);
        listOfGrpChats.setBackground(color1);
        JScrollPane scrollPane = new JScrollPane(listOfGrpChats);
        scrollPane.setBounds(40,90,610,280);
        listOfGrpChats.setLayoutOrientation(JList.VERTICAL);
        listOfGrpChats.setFixedCellHeight(50);
        listOfGrpChats.setBorder(new EmptyBorder(5, 10, 5, 10));
        listOfGrpChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfGrpChats.setFont(new Font("Ariel",Font.BOLD,20));
        p1.add(scrollPane);


        //Buttons
        enterB = new JButton("Enter");
        enterB.setBounds(40, 380, 140, 30);
        enterB.setBackground(new java.awt.Color(color2.getRGB()));
        enterB.setForeground(color1);
        enterB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        enterB.addActionListener(e -> {
            try {
                if(!array[0].equals("No Group Chat")){
                    if(listOfGrpChats.getSelectedIndex()!=-1){
                        chatApplicationGUI.selectGrpChat(listOfGrpChats.getSelectedIndex());
                        new MessagePlatformGUI(chatApplicationGUI, listOfGrpChats.getSelectedValue(), "Group");
                        this.dispose();
                    }
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        p1.add(enterB);

        createB = new JButton("Create");
        createB.setBounds(190, 380, 150, 30);
        createB.setBackground(new java.awt.Color(color6.getRGB()));
        createB.setForeground(color1);
        createB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        createB.addActionListener(e -> {
            try {
                chatApplicationGUI.selectGrpChat(-1100);
                new CreateGroupChatGUI(chatApplicationGUI);
                this.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        p1.add(createB);

        favoriteB = new JButton("Favorite");
        favoriteB.setBounds(350, 380, 150, 30);
        favoriteB.setBackground(new java.awt.Color(color4.getRGB()));
        favoriteB.setForeground(color1);
        favoriteB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        favoriteB.addActionListener(e -> {
            try {
                chatApplicationGUI.selectGrpChat(-1101);
                if(listOfGrpChats.getSelectedIndex()!=-1){
                    String i = String.valueOf(listOfGrpChats.getSelectedIndex());
                    chatApplicationGUI.sendToServer(i);
                    chatApplicationGUI.sendToServer("2");
                    new GroupChatSelectionGUI(chatApplicationGUI);
                    this.dispose();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        p1.add(favoriteB);

        backB = new JButton("Back");
        backB.setBounds(510, 380, 140, 30);
        backB.setBackground(new java.awt.Color(color5.getRGB()));
        backB.setForeground(color1);
        backB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backB.addActionListener(e -> {
            try {
                chatApplicationGUI.selectGrpChat(-1102);
                new ClientMenuGUI(chatApplicationGUI);
                this.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        p1.add(backB);
        this.pack();
        this.setVisible(true);
        JOptionPane.showMessageDialog(null, """
                Use Commands while within a Group Chat
                @accept - accept invitation of the group chat
                @add:<username> - add a new member
                @remove:<username> - remove an existing member (admin command only).""");
    }

    private static void populateArray(ChatApplicationGUI chatApplicationGUI) throws IOException {
        grpChatArray = chatApplicationGUI.listOfGrpChats();
    }
}
