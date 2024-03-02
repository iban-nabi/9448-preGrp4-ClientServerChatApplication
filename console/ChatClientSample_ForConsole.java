package chat_client;

import chat_server.Encryption;

import javax.crypto.SecretKey;
import java.io.*;
import java.net.Socket;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.Scanner;

public class ChatClientSample_ForConsole {

    private Socket socket;
    private BufferedWriter streamWtr;
    private BufferedReader streamRdr;
    private Thread thread;
    ChatClientHandler_ForConsole messageThread;


    public ChatClientSample_ForConsole(Socket socket){
        try{
            this.socket = socket;
            this.streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
            this.streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 5900);
        ChatClientSample_ForConsole c = new ChatClientSample_ForConsole(socket);
        try {
            c.login();
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }
        c.start(c);
    }

    public void start(ChatClientSample_ForConsole c) throws IOException {
        while(true){
            c.chatSelection(c);
            c.listenForMessage();
            c.sendMessage();
        }
    }

    public void login() throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        while(true){
            Scanner scanner = new Scanner(System.in);
            System.out.print("Username: ");
            String username = scanner.nextLine();
            streamWtr.write(username);
            streamWtr.newLine();
            streamWtr.flush();
            System.out.print("Password: ");
            SecretKey passKey = Encryption.generateKeyFromPassword(scanner.nextLine());
            String password = Encryption.convertSecretKeyToString(passKey);
            streamWtr.write(password);
            streamWtr.newLine();
            streamWtr.flush();
            String receive = streamRdr.readLine();
            if(receive.equals("")){
                break;
            }else{
                System.out.println(receive);
            }
        }
    }

    public void chatSelection(ChatClientSample_ForConsole c) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Select option");
        System.out.println("1. Global Chat");
        System.out.println("2. Group Chat");
        System.out.println("3. Private Chat");
        System.out.println("4.Log Out");
        System.out.print("Choose:");
        String choice = scanner.nextLine();
        streamWtr.write(choice);
        streamWtr.newLine();
        streamWtr.flush();

        int i=1;
        if(Integer.parseInt(choice)==2){
            String grpName = streamRdr.readLine();
            if(!grpName.equals("No Group Chats")){
                while(!grpName.equals("")){
                    System.out.println(i+" - Group: "+grpName);
                    grpName = streamRdr.readLine();
                    i++;
                }
            }else{
                System.out.println(grpName);
            }

            System.out.println(streamRdr.readLine());
            System.out.print("Choose: ");
            choice = scanner.nextLine();
            streamWtr.write(choice);
            streamWtr.newLine();
            streamWtr.flush();

            if(choice.equals("0")){
                c.createGroupChat(c);
                c.start(c);
            }
        }

        if(Integer.parseInt(choice)==4){
            closeEverything();
            System.exit(0);
        }
    }

    public void createGroupChat(ChatClientSample_ForConsole c) throws IOException {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter group chat name: ");
        String grpName = scanner.nextLine();
        streamWtr.write(grpName);
        streamWtr.newLine();
        streamWtr.flush();
        System.out.println(streamRdr.readLine());
    }

    public void sendMessage(){
        try{
            Scanner scanner = new Scanner(System.in);
            while(!thread.isInterrupted()) {
                String messageToSend = scanner.nextLine();
                if (messageToSend.equals("exit")) {
                    messageThread.threadInterrupt();
                    thread.interrupt();
                }
                streamWtr.write(messageToSend);
                streamWtr.newLine();
                streamWtr.flush();
            }
        }catch (IOException e){
            closeEverything();
        }
    }

    public void listenForMessage(){
        messageThread = new ChatClientHandler_ForConsole(socket);
        thread = new Thread(messageThread);
        thread.start();
    }

    public void closeEverything(){
        try{
            if(streamRdr != null){
                streamRdr.close();
            }
            if(streamWtr != null){
                streamWtr.close();
            }
            if(socket != null){
                socket.close();
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
