package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.User;
import chat_server.tools.MoveListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.IOException;
import java.util.ArrayList;

public class PrivateChatSelectionGUI extends JFrame {
    public JPanel p1;
    public JButton enterB, createB, favoriteB, backB;
    public JLabel usersL, blockedL;
    public JList<String> listOfPrivateChats, blockedLI;
    static ArrayList<String> toDisplay;
    public static ArrayList<User> users;
    public static ArrayList<String> blocked;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public PrivateChatSelectionGUI(ChatApplicationGUI chatApplicationGUI) throws IOException {
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
        p1.setBackground(new Color(color4.getRGB()));
        p1.setPreferredSize(new Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);


        //Labels
        usersL = new JLabel("Your Private Chats:");
        usersL.setForeground(color1);
        usersL.setFont(new Font("Ariel",Font.BOLD,30));
        usersL.setBounds(40,1,500,100);
        p1.add(usersL);

        //Lists
        String[] array = toDisplay.toArray(new String[0]);
        listOfPrivateChats = new JList<>(array);
        listOfPrivateChats.setVisibleRowCount(5);
        listOfPrivateChats.setBackground(color1);
        JScrollPane scrollPane = new JScrollPane(listOfPrivateChats);
        listOfPrivateChats.setLayoutOrientation(JList.VERTICAL);
        listOfPrivateChats.setFixedCellHeight(20);
        listOfPrivateChats.setBounds(40,90,610,280);
        listOfPrivateChats.setBorder(new EmptyBorder(5, 10, 5, 10));
        listOfPrivateChats.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listOfPrivateChats.setFont(new Font("Ariel",Font.BOLD,12));
        p1.add(scrollPane);
        p1.add(listOfPrivateChats);

        //Buttons
        enterB = new JButton("Enter");
        enterB.setBounds(40, 380, 140, 30);
        enterB.setEnabled(true);
        enterB.setBackground(new Color(color2.getRGB()));
        enterB.setForeground(color1);
        enterB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        enterB.addActionListener(e -> {
            if (!listOfPrivateChats.isSelectionEmpty()) {
                try {
                    chatApplicationGUI.sendToServer("BREAK");//Keyword to break the server loop that allows updating of contact list
                    String selected = listOfPrivateChats.getSelectedValue();
                    selected = selected.replace("★","");
                    selected = selected.replace(" [ONLINE]", "");
                    chatApplicationGUI.sendToServer(selected); //sends contact name to server
                    new PrivateMessagePlatformGUI(chatApplicationGUI,selected); //starts the actual messaging app
                    this.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Choose a contact first!", "No Contact Chosen", JOptionPane.ERROR_MESSAGE);
        });
        p1.add(enterB);


        favoriteB = new JButton("Favorite");
        favoriteB.setBounds(350, 380, 150, 30);
        favoriteB.setBackground(new Color(color4.getRGB()));
        favoriteB.setForeground(color1);
        favoriteB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        favoriteB.addActionListener(e -> {
            if (!listOfPrivateChats.isSelectionEmpty()) {
                try {
                    String selected = listOfPrivateChats.getSelectedValue();
                    selected = selected.replace(" [ONLINE]", "");
                    chatApplicationGUI.sendToServer(selected);
                    new PrivateChatSelectionGUI(chatApplicationGUI);
                    this.dispose();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            else
                JOptionPane.showMessageDialog(null, "Choose a contact first!", "No Contact Chosen", JOptionPane.ERROR_MESSAGE);
            });
        p1.add(favoriteB);

        backB = new JButton("Back");
        backB.setBounds(510, 380, 140, 30);
        backB.setBackground(new Color(color5.getRGB()));
        backB.setForeground(color1);
        backB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        backB.addActionListener(e -> {
            try {
                chatApplicationGUI.sendToServer("BREAK"); //Keyword to break the server loop that allows updating of contact list
                chatApplicationGUI.sendToServer(" ");
                new ClientMenuGUI(chatApplicationGUI);
                this.dispose();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        p1.add(backB);
        this.pack();
        this.setVisible(true);
    }

    private static void populateArray(ChatApplicationGUI chatApplicationGUI) throws IOException {
        ArrayList<String> receivedNames = new ArrayList<>(chatApplicationGUI.receiveBookmarksFromServer());
        toDisplay = new ArrayList<>();
        String startMarked, endMarked, startUnmarked, endUnmarked;
        startMarked = "Start of Bookmarked List:";
        endMarked = "End of Bookmarked List:";
        startUnmarked = "Start of Non-Bookmarked List:";
        endUnmarked = "End of Non-Bookmarked List:";

        //add bookmarked
        int start, end;
        if (receivedNames.contains(startMarked)){
            start = receivedNames.indexOf(startMarked);
            end = receivedNames.indexOf(endMarked);
            for (int i=start+1; i<end; i++){
                toDisplay.add("★"+receivedNames.get(i));
            }
        }

        //add not bookmarked
        if (receivedNames.contains(startUnmarked)){
            start = receivedNames.indexOf(startUnmarked);
            end = receivedNames.indexOf(endUnmarked);
            for (int i=start+1; i<end; i++){
                toDisplay.add(receivedNames.get(i));
            }
        }

        //add Online status
        ArrayList<String> activeNames = new ArrayList<>(chatApplicationGUI.receiveActiveUsersFromServer());
        for (String activeName : activeNames) {
            if (toDisplay.contains("★" + activeName)) {
                int indexOfActive = toDisplay.indexOf("★" + activeName);
                toDisplay.set(indexOfActive, "★" + activeName + " [ONLINE]");
            }
        }
        for (String activeName : activeNames) {
            if (toDisplay.contains(activeName)) {
                int indexOfActive = toDisplay.indexOf(activeName);
                toDisplay.set(indexOfActive, activeName + " [ONLINE]");
            }
        }
    }
}
