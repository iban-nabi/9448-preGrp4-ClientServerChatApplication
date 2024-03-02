package GUI.ServerGUI;

import chat_server.ServerHandlerGUI;
import chat_server.StartServerGUI;
import chat_server.tools.MoveListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.net.ServerSocket;

public class ServerGUI extends JFrame{
    public JPanel p1, p2, p3;
    public JButton startB, stopB, manageB, exitB;
    public JLabel statusL;
    ServerHandlerGUI sb;
    private final Color color1 = new Color(254, 250, 224);
    private final Color color2 = new Color(221, 161, 94);
    private final Color color3 = new Color(96, 108, 56);
    private final Color color4 = new Color(188, 108, 37);
    private final Color color5 = new Color(40, 54, 24);
    private final Color color6 = new Color(138, 52, 37);

    public static void main(String[] args) {
        new ServerGUI();
    }

    public ServerGUI() {
        //Frame
        this.setSize(700, 445);
        this.setResizable(false);
        this.setLocationRelativeTo(null);
        setUndecorated(true);
        getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        //Panel
        this.p1 = new JPanel(new GridLayout(2, 2));
        p1.setBackground(new Color(color1.getRGB()));
        this.p2 = new JPanel(new GridLayout(1, 1));
        p2.setBackground(new Color(color1.getRGB()));
        this.p3 = new JPanel(new FlowLayout());
        p3.setBackground(new Color(color1.getRGB()));

        p1.setPreferredSize(new Dimension(700, 445));
        p1.setLayout(null);
        this.add(p1, BorderLayout.NORTH);

        MoveListener listener = new MoveListener(this);
        p1.addMouseListener(listener);
        p1.addMouseMotionListener(listener);

        this.statusL = new JLabel("SERVER IS TURNED OFF.");
        statusL.setForeground(color3);
        statusL.setBounds(280, 120, 310, 30);
        p1.add(statusL);

        //Start Button
        this.startB = new JButton("Start Server");
        startB.setBounds(200, 170, 310, 30);
        startB.setBackground(new Color(color5.getRGB()));
        startB.setForeground(color1);
        startB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        startB.addActionListener(t -> {
            this.sb = new ServerHandlerGUI();
            statusL.setText("SERVER IS TURNED ON.");
            stopB.setBackground(new Color(color3.getRGB()));
            startB.setBackground(new Color(114, 97, 97));
            stopB.setEnabled(true);
            startB.setEnabled(false);
            Thread thread = new Thread(sb::runServer);
            thread.start();

        });
        this.p1.add(this.startB);

        this.stopB = new JButton("Stop Server");//Stop Button
        stopB.setEnabled(false);
        stopB.setBounds(200, 210, 310, 30);
        stopB.setBackground(new Color(color2.getRGB()));
        stopB.setForeground(color1);
        stopB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        stopB.addActionListener(s -> {
            statusL.setText("SERVER IS TURNED OFF.");
            startB.setBackground(new Color(color5.getRGB()));
            stopB.setBackground(new Color(114, 97, 97));
            startB.setEnabled(true);
            stopB.setEnabled(false);
            try {
                sb.closeServerSocket();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        p1.add(stopB);

        this.manageB = new JButton("Manage Users");
        manageB.setBounds(200, 280, 310, 30);
        manageB.setBackground(new Color(color4.getRGB()));
        manageB.setForeground(color1);
        manageB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        manageB.addActionListener(e -> {
            new ManageUserGUI();
            this.dispose();
        });
        p1.add(manageB);

        this.exitB = new JButton("Exit");
        exitB.setBounds(530, 380, 100, 30);
        exitB.setBackground(color6);
        exitB.setForeground(color1);
        exitB.setBorder(BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        exitB.addActionListener(e -> {
            this.dispose();
        });
        p1.add(exitB);

        this.pack();
        this.setVisible(true);
    }
}
