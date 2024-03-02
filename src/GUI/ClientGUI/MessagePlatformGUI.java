package GUI.ClientGUI;

import chat_client.ChatApplicationGUI;
import chat_server.tools.MoveListener;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class MessagePlatformGUI extends JFrame {
    static Socket socket;
    ChatApplicationGUI chatApplicationGUI;
    boolean exit;
    static BufferedReader streamRdr;
    Thread thread;

    public JPanel p1, p2, p3;
    public JTextField sendMessageTF;
    public static JTextArea t2;
    public JButton b1, b2;
    public JScrollPane sp1;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public MessagePlatformGUI(ChatApplicationGUI chatApplicationGUI,String chatName, String chatType) throws IOException {

        Border thinBorder = LineBorder.createBlackLineBorder();
        this.chatApplicationGUI = chatApplicationGUI;
        this.exit = false;
        socket = chatApplicationGUI.getSocket();
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        this.setTitle(chatName);
        setResizable(false);

        //panels
        this.p1 = new JPanel(new GridLayout(2, 2));
        this.p2 = new JPanel(new GridLayout(1, 1));
        p2.setBackground(new java.awt.Color(color4.getRGB()));
        this.p3 = new JPanel(new FlowLayout());
        p3.setBackground(new java.awt.Color(color4.getRGB()));



        this.add(p1, BorderLayout.NORTH);
        p1.setMaximumSize(new java.awt.Dimension(700, 445));
        p1.setMinimumSize(new java.awt.Dimension(700, 445));
        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);


        //buttons
        this.b1 = new JButton("Send");
        this.b2 = new JButton("Back");


        if (chatType.equals("Global")) {
            p1.setBackground(new java.awt.Color(color3.getRGB()));
            b1.setBackground(new java.awt.Color(color2.getRGB()));
        }
        if (chatType.equals("Group")) {
            p1.setBackground(new java.awt.Color(color2.getRGB()));
            b1.setBackground(new java.awt.Color(color3.getRGB()));
        }

        this.p1.add(this.b1);
        b1.setBounds(100, 340, 100, 30);
        b1.setForeground(color1);
        b1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.p1.add(this.b2);
        b2.setBounds(100, 370, 100, 30);
        b2.setBackground(new java.awt.Color(color6.getRGB()));
        b2.setBackground(color1);
        b2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        b1.addActionListener(this::sendingPerformed);
        b2.addActionListener(evt -> {
            try {
                b2Performed(evt);
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        //TextField
        this.sendMessageTF = new JTextField("", 1);
        t2 = new JTextArea(35, 35);
        t2.setEditable(false);
        t2.setBorder(thinBorder);
        sp1 = new JScrollPane(t2);
        sp1.setBounds(100, 25, 510, 300);
        t2.setBackground(new java.awt.Color(color1.getRGB()));

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        this.p1.add(this.sendMessageTF);
        sendMessageTF.setBounds(220, 340, 390, 60);
        sendMessageTF.setBackground(new java.awt.Color(color1.getRGB()));
        sendMessageTF.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        sendMessageTF.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendingPerformed(evt);
            }
        });

        this.p1.add(this.sp1);


        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        this.thread = readMessage();
        thread.start();
    }

    private void sendingPerformed(java.awt.event.ActionEvent evt) {
        try {
            if (sendMessageTF.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please write some text !");
            }else  {
                t2.setText(t2.getText() + "\n"+ "You : " + sendMessageTF.getText());
                try {
                    chatApplicationGUI.sendToServer(sendMessageTF.getText());
                } catch (IOException e1) {
                    t2.setText(t2.getText() + "\n " + " Network issues");
                }
                sendMessageTF.setText("");
            }

        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }
    private void b2Performed(java.awt.event.ActionEvent evt) throws IOException, InterruptedException {
        streamRdr = null;
        exit = true;
        chatApplicationGUI.sendToServer("01100101 01111000 01101001 01110100");
        new ClientMenuGUI(chatApplicationGUI);
        this.dispose();
    }


    private Thread readMessage(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!exit) {
                    try {
                        streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                        String msg = streamRdr.readLine();
                        t2.setText(t2.getText() + "\n" + msg);
                    } catch (Exception ev) {
                        t2.setText(t2.getText()+" \n" +"Network issues ");
                        try {
                            Thread.sleep(2000);
                            System.exit(0);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        return thread;
    }
}



