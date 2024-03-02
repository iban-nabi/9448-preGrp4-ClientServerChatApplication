package GUI.ServerGUI;

import chat_server.User;
import chat_server.UserManagerFINAL;
import chat_server.tools.MoveListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.Objects;

public class ManageUserGUI extends JFrame {
    public JPanel p1;
    public JButton addB, deleteB, blockB, unblockB, backB;
    public JLabel usersL, blockedL;
    public JList<String> usersLI, blockedLI;
    public static ArrayList<User> users;
    public static ArrayList<String> blocked;
    public static String[] usernameArr, blocklistArr;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public ManageUserGUI(){
        //Populate
        populateArray();
        //Frame
        this.setSize(700,445);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        p1 = new JPanel();
        p1.setLayout(new BorderLayout());
        p1.setBackground(new java.awt.Color(color3.getRGB()));
        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        //Buttons
        addB = new JButton("Add");
        addB.setBounds(40, 30, 70, 30);
        addB.setBackground(new java.awt.Color(color2.getRGB()));
        addB.setForeground(color1);
        addB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        addB.addActionListener(e -> {
            new AddUserGUI();
            this.dispose();
        });
        p1.add(addB);

        deleteB = new JButton("Delete");
        deleteB.setBounds(120, 30, 70, 30);
        deleteB.setBackground(new java.awt.Color(color6.getRGB()));
        deleteB.setForeground(color1);
        deleteB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        deleteB.addActionListener(e -> {
            if(usersLI.getSelectedIndex() != -1){
                String selectedUN = usersLI.getSelectedValue();
                UserManagerFINAL.deleteUser(selectedUN);
                populateArray();
                new ManageUserGUI();
                this.dispose();

            }
        });
        p1.add(deleteB);

        blockB = new JButton("Block");
        blockB.setBounds(360, 30, 70, 30);
        blockB.setBackground(new java.awt.Color(color4.getRGB()));
        blockB.setForeground(color1);
        blockB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        blockB.addActionListener(e -> {
            if(usersLI.getSelectedIndex() != -1){
                String selectedUN = usersLI.getSelectedValue();
                UserManagerFINAL.blockUser(selectedUN);
                populateArray();
                new ManageUserGUI();
                this.dispose();
            }
        });
        p1.add(blockB);

        unblockB = new JButton("Unblock");
        unblockB.setBounds(440, 30, 70, 30);
        unblockB.setBackground(new java.awt.Color(color5.getRGB()));
        unblockB.setForeground(color1);
        unblockB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        unblockB.addActionListener(e -> {
            if(blockedLI.getSelectedIndex() != -1){
                String selectedUN = blockedLI.getSelectedValue();
                UserManagerFINAL.unblockUser(selectedUN);
                users.clear();
                blocked.clear();
                populateArray();
                new ManageUserGUI();
                this.dispose();
            }
        });
        p1.add(unblockB);

        backB = new JButton("Back");
        backB.setBounds(600, 30, 70, 30);
        backB.setBackground(new java.awt.Color(color1.getRGB()));
        backB.setForeground(color5);
        backB.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backB.addActionListener(e -> {
            new ServerGUI();
            this.dispose();
        });
        p1.add(backB);

        //Labels
        usersL = new JLabel("Users");
        usersL.setForeground(color1);
        usersL.setFont(new Font("Ariel",Font.BOLD,20));
        usersL.setBounds(40,50,100,100);
        p1.add(usersL);

        blockedL = new JLabel("Blocked Users");
        blockedL.setForeground(color1);
        blockedL.setFont(new Font("Ariel",Font.BOLD,20));
        blockedL.setBounds(360,50,150,100);
        p1.add(blockedL);

        //Lists
        usersLI = new JList<>(usernameArr);
        JScrollPane scrollPane = new JScrollPane(usersLI);
        usersLI.setFixedCellHeight(20);
        usersLI.setBounds(40,120,300,300);
        usersLI.setBorder(new EmptyBorder(5, 10, 5, 10));
        usersLI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p1.add(usersLI);
        p1.add(scrollPane);

        blockedLI = new JList<>(blocklistArr);
        JScrollPane scrollPane2 = new JScrollPane(blockedLI);
        blockedLI.setFixedCellHeight(20);
        blockedLI.setBounds(360,120,300,300);
        blockedLI.setBorder(new EmptyBorder(5, 10, 5, 10));
        blockedLI.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        p1.add(blockedLI);
        p1.add(scrollPane2);

        this.pack();
        this.setVisible(true);
    }

    private static void populateArray(){
        if(users != null){
            users.clear();
            blocked.clear();
            usernameArr = new String[usernameArr.length];
            blocklistArr = new String[blocklistArr.length];
        }

        users = UserManagerFINAL.readFile();
        blocked = UserManagerFINAL.blockList;

        for(int i = 0; i < Objects.requireNonNull(users).size(); i++){
            for (String s : blocked) {
                if (users.get(i).getUsername().equals(s)) {
                    users.remove(i);
                }
            }
        }

        usernameArr = new String[Objects.requireNonNull(users).size()];
        for(int i = 0; i< Objects.requireNonNull(users).size(); i++){
            usernameArr[i] = users.get(i).getUsername();
        }

        blocklistArr = new String[Objects.requireNonNull(blocked).size()];
        for(int i = 0; i< Objects.requireNonNull(blocked).size(); i++){
            blocklistArr[i] = blocked.get(i);
        }
    }

    public static void main(String[] args) {
        new ManageUserGUI();
    }
}
