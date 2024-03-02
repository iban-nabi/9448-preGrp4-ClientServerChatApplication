package chat_server.global_chat;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GlobalMessagingHandler implements Runnable{
    private String name;
    private BufferedReader streamRdr;
    private BufferedWriter streamWtr;
    private static List<GlobalMessagingHandler> listOfUsers = new CopyOnWriteArrayList<>();

    /**
     * Constructor for the GlobalMessagingHandler. Setups the current user, the stream reader and writer.
     * @param socket socket used for communication
     * @param name name of the current user
     * @throws IOException throws IOException
     */
    public GlobalMessagingHandler(Socket socket, String name) throws IOException {
        try{
            this.name = name;
            streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
            listOfUsers.add(this);
        }catch (Exception e){
            close();
        }
    }

    /**
     * Execute the messaging for global chat
     */
    @Override
    public void run() {
        try {
            shareMessage();
        } catch (IOException e) {
            close();
        }
    }

    /**
     * Method that receives the message entered by the user, and then send it to the other users within the global chat
     * by calling the sendMessage method. If the server receives a certain string, it will stop the current user activity
     * within the global chat by calling the close method.
     * @throws IOException throws IOException
     */
    private void shareMessage() throws IOException {
        while(true){
            String textMsg = streamRdr.readLine();
            if(!textMsg.equals("")){
                if(textMsg.equals("01100101 01111000 01101001 01110100")){
                    close();
                    break;
                }else{
                    sendMessage(textMsg);
                }
            }
        }
    }

    /**
     * Method that delivers the message to every user within the global chat
     * @param textMsg users message to be sent to the other users in the global chat
     */
    private void sendMessage(String textMsg){
        try{
            for(GlobalMessagingHandler user: listOfUsers){
                if(user!=this){
                    user.streamWtr.write(name+": "+textMsg);
                    user.streamWtr.newLine();
                    user.streamWtr.flush();
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * if the user exits, this method will be called in order to remove the user in the list of current users within
     * the global chat
     */
    private void close() {
        listOfUsers.remove(this);
    }
}
