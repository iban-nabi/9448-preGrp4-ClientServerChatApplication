package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.tools.MoveListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class PrivateMessagePlatformGUI extends JFrame {
    static Socket socket;
    ChatApplicationGUI chatApplicationGUI;
    public JPanel p1, p2, p3;
    public JTextField t1;
    public static JTextArea t2;
    public JButton b1, b2;
    public JScrollPane sp1;
    boolean exit;
    static BufferedReader streamRdr;
    static BufferedWriter streamWtr;
    Thread thread;
    String personToChat;

    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public PrivateMessagePlatformGUI(ChatApplicationGUI chatApplicationGUI, String personToChat) throws IOException {
        Border thinBorder = LineBorder.createBlackLineBorder();
        this.personToChat = personToChat;
        this.chatApplicationGUI = chatApplicationGUI;
        this.exit = false;
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        socket = chatApplicationGUI.getSocket();
        this.b1 = new JButton("Send");
        this.b2 = new JButton("Back");

        this.t1 = new JTextField("", 1);
        t2 = new JTextArea(35, 35);
        t2.setEditable(false);
        sp1 = new JScrollPane(t2);
        t2.setBorder(thinBorder);
        sp1.setBounds(100, 25, 510, 300);
        t2.setBackground(new java.awt.Color(color1.getRGB()));

        this.p1 = new JPanel(new GridLayout(2, 2));
        p1.setBackground(new Color(188, 108, 37));
        this.p2 = new JPanel(new GridLayout(1, 1));
        p2.setBackground(new Color(0, 0, 50));
        this.p3 = new JPanel(new FlowLayout());
        p3.setBackground(new Color(0, 0, 50));

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);


        this.p1.add(this.t1);
        t1.setBounds(220, 340, 390, 60);
        t1.setBackground(new Color(color1.getRGB()));
        t1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        t1.addActionListener(this::SendButtonAction);

        this.p1.add(this.sp1);

        this.p1.add(this.b1);
        b1.setBounds(100, 340, 100, 30);
        b1.setBackground(new Color(color3.getRGB()));
        b1.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.p1.add(this.b2);
        b2.setBounds(100, 370, 100, 30);
        b2.setBackground(new Color(color1.getRGB()));
        b2.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.add(p1, BorderLayout.NORTH);
        p1.setMaximumSize(new Dimension(700, 445));
        p1.setMinimumSize(new Dimension(700, 445));
        p1.setPreferredSize(new Dimension(700, 445));
        p1.setLayout(null);

        this.setTitle(personToChat);
        this.setMinimumSize(700, 445);
        this.setMaximumSize(700, 445);
        setPreferredSize(700, 445);
        setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        b1.setText("Send");
        b1.addActionListener(this::SendButtonAction);
        b2.setText("Back");
        b2.addActionListener(evt -> {
            try {
                b2Performed();
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        displayOfflineMsg();
        this.thread = readMessage();
        thread.start();
    }

    private void SendButtonAction(java.awt.event.ActionEvent evt) {
        try {
            if (t1.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please write some text !");
            }else  {

                t2.setText(t2.getText() + "\n"+ "You : " + t1.getText());
                try {
                    chatApplicationGUI.sendToServer(LoginGUI.getUsername()+"•"+personToChat+"•"+t1.getText());
                } catch (IOException e1) {
                    t2.setText(t2.getText() + "\n " + " Network issues.....................");
                    JOptionPane.showMessageDialog(null, "Server Has Stopped! " );
                }
                t1.setText("");
            }

        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }
    private void b2Performed() throws IOException, InterruptedException {
        streamRdr = null;
        exit = true;
        chatApplicationGUI.sendToServer("01100101 01111000 01101001 01110100");
        new ClientMenuGUI(chatApplicationGUI);
        this.dispose();
    }

    private void displayOfflineMsg() throws IOException {
        String offlineMsg = chatApplicationGUI.readMsgFromServer();
        while(!offlineMsg.equals(" ")){
            t2.setText(t2.getText() + "\n" + offlineMsg);
            offlineMsg = chatApplicationGUI.readMsgFromServer();
        }

    }


    private Thread readMessage(){
        Thread thread = new Thread(() -> {
            while (!exit) {
                try {
                    streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    String msg = streamRdr.readLine();
                    System.out.println(msg);
                    t2.setText(t2.getText() + "\n" + msg);
                } catch (Exception ev) {
                    t2.setText(t2.getText()+" \n" +"Network issues.....................");
                    try {
                        Thread.sleep(2000);
                        System.exit(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        return thread;
    }

    private void setMinimumSize(int i, int i1) {
    }

    private void setMaximumSize(int i, int i1) {
    }

    private void setPreferredSize(int i, int i1) {

    }
}



