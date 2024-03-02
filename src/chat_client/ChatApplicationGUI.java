package chat_client;

import GUI.ClientGUI.LoginGUI;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChatApplicationGUI {

    private static Socket socket;
    private static BufferedWriter streamWtr;
    private static BufferedReader streamRdr;
    private static int port = 6900;
    String hostAddress;

    /**
     * Constructor for ChatApplicationGUI that set the hostAddress value.
     * @param hostAddress
     */
    public ChatApplicationGUI(String hostAddress){
        this.hostAddress = hostAddress;
    }

    public String getHostAddress() {
        return hostAddress;
    }

    public Socket getSocket(){
        return socket;
    }

    public static void main(String[] args) throws IOException {
        new LoginGUI();
    }

    /**
     * Method that initialized the socket, reader and writer.
     * @param ipAddress of the server
     * @return true if connection success, otherwise false
     */
    public boolean hostAddressChecker(String ipAddress){
        try {
            socket = new Socket(ipAddress, port);
            streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
            streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            return true;
        } catch (IOException e) {
            closeEverything();
            return false;
        }
    }

    /**
     * Method that sends a msg to the server
     * @param string message to send
     * @throws IOException throws IOException
     */
    public void sendToServer(String string) throws IOException {
        streamWtr.write(string);
        streamWtr.newLine();
        streamWtr.flush();
    }

    /**
     * Method that reads the response of the server
     * @return response of the server
     * @throws IOException throws IOException
     */
    public String readMsgFromServer() throws IOException {
        return streamRdr.readLine();
    }


    /**
     * Method that sends the username and password of the current user to the server
     * @param username entered by the current user
     * @param password entered by the current user
     * @return int value which is an indicator: returns 0 if found, returns 1 if blocked, returns 2 if online, returns 3 if invalid
     * @throws IOException throws IOException
     */
    public int loginUser(String username, String password) throws IOException {
        streamWtr.write(username);
        streamWtr.newLine();
        streamWtr.flush();

        streamWtr.write(password);
        streamWtr.newLine();
        streamWtr.flush();

        return Integer.parseInt(streamRdr.readLine());
    }


    /**
     * Method use to send a response during the menu selection
     * @param number selected by the user during menu selection
     * @throws IOException
     */
    public void selectChatType(int number) throws IOException {
        streamWtr.write(String.valueOf(number));
        streamWtr.newLine();
        streamWtr.flush();
    }


    /**
     * Method use to send a response during the grp chat selection
     * @param number selected by the user during grp chat selection
     * @throws IOException
     */
    public void selectGrpChat(int number) throws IOException {
        streamWtr.write(String.valueOf(number));
        streamWtr.newLine();
        streamWtr.flush();
    }

    /**
     * Method use to populate the list of group chat the user is included
     * @return list that contains the group chats of the current user
     * @throws IOException throws IOException
     */
    public ArrayList<String> listOfGrpChats() throws IOException {
        ArrayList<String> grpList = new ArrayList<>();
        String grpName = streamRdr.readLine();
        while(!grpName.equals("")){
            grpList.add("Group Chat "+grpName);
            grpName = streamRdr.readLine();
        }
        return grpList;
    }


    /**
     * Method use to populate the list of bookmarks of the current user
     * @return list of bookmarks
     * @throws IOException throws IOException
     */
    public ArrayList<String> receiveBookmarksFromServer() throws IOException {
        //confirmed that bookmarked data is incoming
        ArrayList<String> bookmarkList = new ArrayList<>();
        String bookmarkData = streamRdr.readLine();
        while(!bookmarkData.equals("STOP")){ //signals the end of bookmark list
            bookmarkList.add(bookmarkData);
            bookmarkData = streamRdr.readLine();
        }
        return bookmarkList;
    }


    /**
     * Method use to populate the list of online users
     * @return list of active users
     * @throws IOException throws IOException
     */
    public List<String> receiveActiveUsersFromServer() throws IOException {
        ArrayList<String> activeList = new ArrayList<>();
        String activeData = streamRdr.readLine();
        while (!activeData.equals("STOP")){
            activeList.add(activeData);
            activeData = streamRdr.readLine();
        }
        return activeList.stream().distinct().collect(Collectors.toList());
    }

    /**
     * Method for closing the socket, reader and writer
     */
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
