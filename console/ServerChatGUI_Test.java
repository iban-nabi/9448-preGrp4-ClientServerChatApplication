package GUI.ServerGUI;

import chat_server.tools.MoveListener;

import java.awt.*;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import javax.swing.*;

public class ServerChatGUI_Test extends JFrame {

    static ServerSocket serverSocket;
    static Socket socket;
    static DataInputStream input;
    static DataOutputStream output;

    public ServerChatGUI_Test() {
        this.b1 = new JButton("Send");
        this.b2 = new JButton("Back");

        this.t1 = new JTextArea(1, 1);
        this.t2 = new JTextArea(35, 35);

        this.p1 = new JPanel(new GridLayout(2, 2));
        p1.setBackground(new java.awt.Color(0, 0, 50));
        this.p2 = new JPanel(new GridLayout(1, 1));
        p2.setBackground(new java.awt.Color(0, 0, 50));
        this.p3 = new JPanel(new FlowLayout());
        p3.setBackground(new java.awt.Color(0, 0, 50));

        this.p1.add(this.t1);
        t1.setBounds(220, 340, 390, 60);
        t1.setBackground(new java.awt.Color(200, 255, 255));
        t1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.p1.add(this.t2);
        t2.setBounds(100, 25, 510, 300);
        t2.setBackground(new java.awt.Color(200, 255, 255));
        t2.setEditable(false);
        t2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.p1.add(this.b1);
        b1.setBounds(100, 340, 100, 30);
        b1.setBackground(new java.awt.Color(0, 255, 255));
        b1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        this.p1.add(this.b2);
        b2.setBounds(100, 370, 100, 30);
        b2.setBackground(new java.awt.Color(0, 255, 255));
        b2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        this.add(p1, BorderLayout.NORTH);
        p1.setMaximumSize(new java.awt.Dimension(700, 445));
        p1.setMinimumSize(new java.awt.Dimension(700, 445));
        p1.setPreferredSize(new java.awt.Dimension(700, 445));
        p1.setLayout(null);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);
        this.setMinimumSize(700, 445);
        this.setMaximumSize(700, 445);
        setPreferredSize(700, 445);
        setResizable(false);
        this.pack();
        this.setVisible(true);
        this.setLocationRelativeTo(null);

        b1.setText("Send");
        b1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b1Performed(evt);
            }
        });
        b2.setText("Back");
        b2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                b2Performed(evt);
            }
        });
    }


    private void b1Performed(java.awt.event.ActionEvent evt) {
        try {
            if (t1.getText().equals("")) {
                JOptionPane.showMessageDialog(null, "Please write some text !");
            } else {
                t2.setText(t2.getText() + "\n" + "Client : " + t1.getText());
                try {
                    DataOutputStream output = new DataOutputStream(socket.getOutputStream());
                    output.writeUTF(t1.getText());
                } catch (IOException e1) {
                    t2.setText(t2.getText() + "\n " + " Network issues");
                    try {
                        Thread.sleep(2000);
                        System.exit(0);
                    } catch (InterruptedException e2) {
                        e2.printStackTrace();
                    }
                }
                t1.setText("");
            }


        } catch (HeadlessException e) {
            throw new RuntimeException(e);
        }
    }

        private void b2Performed (java.awt.event.ActionEvent evt){
      //  new MenuGUI();
        }

        private void setMinimumSize ( int i, int i1){
        }

        private void setMaximumSize ( int i, int i1){
        }

        private void setPreferredSize ( int i, int i1){

        }

        public JPanel p1, p2, p3;
        public JTextArea t1;
        public static JTextArea t2;
        public JButton b1, b2;


    public static void main(String[] args) throws IOException {

        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    new ServerChatGUI_Test();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        serverConnection();
    }
        private static void serverConnection() throws IOException {
            serverSocket = new ServerSocket(4000);

            socket = serverSocket.accept();
            while (true) {
                try {

                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    String string = input.readUTF();
                    t2.setText(t2.getText() + "\n " + "Client: " + string);
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
    }


