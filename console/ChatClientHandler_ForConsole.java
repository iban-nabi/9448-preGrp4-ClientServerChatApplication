package chat_client;

import java.io.*;
import java.net.Socket;

import static java.lang.Thread.currentThread;

public class ChatClientHandler_ForConsole implements Runnable {

    private Socket socket;
    private BufferedReader streamRdr;
    static boolean stopMessaging;
    public ChatClientHandler_ForConsole(Socket socket){
        try{
            this.socket = socket;
            this.streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            stopMessaging = false;
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        String messageFromGroupChat;
        while(!currentThread().isInterrupted()){
            try{
                messageFromGroupChat = streamRdr.readLine();
                if(!messageFromGroupChat.equals("")){
                    System.out.println(messageFromGroupChat);
                }
            }catch(IOException ignore ){
            }
        }
    }


    public void threadInterrupt(){
        currentThread().interrupt();
    }
}
