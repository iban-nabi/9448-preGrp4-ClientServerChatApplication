package chat_server.private_chat;


import chat_server.OfflineMessagesWriter;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

public class PrivateMessagingHandler implements Runnable{
    private String yourName;
    private String sendTo;
    private Socket socket;
    private String personToChatWith;
    private BufferedReader streamRdr;
    private BufferedWriter streamWtr;
    private static List<PrivateMessagingHandler> listOfUsers = new CopyOnWriteArrayList<>();
    private static ArrayList<String> newList = new ArrayList<>();
    static  ArrayList<String> messageList = new ArrayList<>();
    static  OfflineMessagesWriter offlineMessagesWriter = new OfflineMessagesWriter();
    static  PullParserForAccounts parser = new PullParserForAccounts();
    static  PullParser pullParser = new PullParser();

    public PrivateMessagingHandler(Socket socket, String name, String sendTo) {
        try{
            this.socket = socket;
            this.sendTo = sendTo;
            this.yourName = name;
            streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
            listOfUsers.add(this);
        }catch (Exception e){
            close();
        }
    }

    public ArrayList<String> getNewList() {
        return newList;
    }

    public List<PrivateMessagingHandler> getListOfUsers() {
        return listOfUsers;
    }

    public String getYourName() {
        return yourName;
    }

    @Override
    public void run() { //root of private processes
        try {
            boolean loopBreak = false; //uses the "BREAK" keyword to break the server loop that allows updating of contact list
            while (!loopBreak) {  //this loop allows repeated clicking of "Favorite" button for client in contact list
            sendBookmarksToClient();
            loopBreak = sendActiveUsersToClient();
        }
            selectSendTo();
        } catch (IOException e) {
            close();
        }
        close();
    }

    /**
     * Sends list of Bookmarked names to client, followed by the list of Non-bookmarked
     * names based on the list of registered accounts compared to the bookmarked list.
     */
    public void sendBookmarksToClient() throws IOException {
        ArrayList<String> storedAllNames = parser.readXML();
        PrivateChatBookmarks myBookmarks = new PrivateChatBookmarks(yourName);

        //send Bookmarked list to client
        ArrayList<String> listBookmarked = myBookmarks.getBookmarkedList();
        listBookmarked.remove(yourName);
        streamWtr.write("Start of Bookmarked List:"); //use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();
        for (String bookmarked : listBookmarked) {
            streamWtr.write(bookmarked); //store these in a String array Client-side then display in JList
            streamWtr.newLine();
            streamWtr.flush();
        }
        streamWtr.write("End of Bookmarked List:"); //use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();


        //send Non Bookmarked list to client
        ArrayList<String> listNonBookmarked = myBookmarks.getNonBookmarkedList(storedAllNames);
        listNonBookmarked.remove(yourName);
        streamWtr.write("Start of Non-Bookmarked List:");//use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();
        for (String nonBookmarked : listNonBookmarked) {
            streamWtr.write(nonBookmarked); //store these in a String array Client-side then display in JList
            streamWtr.newLine();
            streamWtr.flush();
        }
        streamWtr.write("End of Non-Bookmarked List:");//use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();
        streamWtr.write("STOP"); //ends the transmission of bookmarks list
        streamWtr.newLine();
        streamWtr.flush();
    }

    /**
     *Sends a list of active users to the client.
     */
    public boolean sendActiveUsersToClient() throws IOException {
        ArrayList<String> userNames = new ArrayList<>();
        PrivateChatBookmarks myBookmarks = new PrivateChatBookmarks(yourName);
        String selected;
        for (PrivateMessagingHandler user : listOfUsers) {
            userNames.add(user.getYourName());
            streamWtr.write(user.getYourName());
            streamWtr.newLine();
            streamWtr.flush();
        }

        newList = (ArrayList<String>) userNames.stream().distinct().collect(Collectors.toList());
        System.out.println(newList);


        //send active user list to client
        streamWtr.write("Start of Active User List:");//use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();
        for (String name : newList) {
            if(!name.equals(yourName)){
                streamWtr.write(name); //store these in a String array Client-side then display in JList
                streamWtr.newLine();
                streamWtr.flush();
            }
        }
        streamWtr.write("End of Active User List:");//use boolean function to capture this at Client-side
        streamWtr.newLine();
        streamWtr.flush();
        streamWtr.write("STOP"); //ends the transmission of bookmarks list
        streamWtr.newLine();
        streamWtr.flush();

        //bookmark editing
        selected = streamRdr.readLine();
        if (selected.equals("BREAK"))//Keyword to break the server loop that allows updating of contact list
            return true;

        if (selected.startsWith("★")) {
            selected = selected.replace("★", "");
            myBookmarks.removeFromBookmarkedList(selected);
        } else
            myBookmarks.addToBookmarkedList(selected);
        return false;
    } //YES SIR OK NA RIN TO

    /**
     * The selectSendTo method, which throws IOException, allows the user to choose the account
     * in which the user wants to have a private chat. The streamRdr will then read the input of
     * the user. The readXML method will then be called using the parser object to check if the
     * username entered by the user exists from the list of accounts; if not, it will require the
     * user to enter another username. After that, if it is present, it will go through another
     * condition, and that is to check if the username is online; if it is, privateChat(1) will
     * then be invoked to chat with the online user. Otherwise, privateChat(0) will be invoked,
     * and the program will allow the user to leave a message to the offline user, and it will
     * then be sent so that if the offline user goes online, he/she will be able to read the
     * messages.
     *
     * @throws IOException
     */
    public void selectSendTo() throws IOException {

        personToChatWith = streamRdr.readLine(); //receives contact name from client

        if(!personToChatWith.equals(" ")){
            if (parser.readXML().contains(personToChatWith)) {
                if (newList.contains(personToChatWith)) {
                    //get offline messages
                    getSentMessages();

                    streamWtr.write("["+ personToChatWith + " is connected to the chat...]");
                    streamWtr.newLine();
                    streamWtr.flush();

                    streamWtr.write(" ");
                    streamWtr.newLine();
                    streamWtr.flush();
                    privateChat(1);

                } else {
                    System.out.println("pasok is"+ personToChatWith);
                    //get offline messages
                    getSentMessages();

                    streamWtr.write("["+ personToChatWith +" is offline. Type messages to be sent when they are online.]");
                    streamWtr.newLine();
                    streamWtr.flush();

                    streamWtr.write(" ");
                    streamWtr.newLine();
                    streamWtr.flush();
                    privateChat(0);
                }
            }
        }
    }

    /**
     * The sentMessages method is used to display all the 
     * offline private messages to a specific user once the 
     * offline user goes online.
     */
    public void getSentMessages() {
        System.out.println("activated");
        try {
            streamWtr.write("[You have received the following messages.]");
            streamWtr.newLine();
            streamWtr.flush();
            ArrayList<String> senderList = pullParser.getSenderList(yourName);
            ArrayList<String> messagesList = new ArrayList<>();
            System.out.println(senderList);
            if(senderList!=null){
                for (String value : senderList) {
                    if (value.equals(personToChatWith))
                        messagesList = pullParser.readXML(yourName, personToChatWith);
                }

                for (String s : messagesList) {
                    System.out.println("offline msg: "+s);
                    streamWtr.write(s);
                    streamWtr.newLine();
                    streamWtr.flush();
                }
            }
            streamWtr.write("[/End.]");
            streamWtr.newLine();
            streamWtr.flush();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The privateChat method with an integer parameter status allows the user
     * to have a private chat with a specific user. Suppose the method is invoked
     * with the parameter 1. In that case, it will go through inside the if statement,
     * which will allow the user to communicate with a specific user using the sendMessage
     * method invoked under the privateChat. However, if the user enters the exit word,
     * it will return to the main menu. However, if the parameter or status of the method
     * is 0, it will allow the user to send messages to an offline user. The fileWriter
     * method is now invoked using the offlineMessagesWriter to write all the messages in
     * an XML file. The sentMessages method is invoked to send all the messages to the
     * offline user.
     *
     * @param status
     * @throws IOException
     */
    public void privateChat(int status) throws IOException {
        messageList.clear();
        boolean flag = true;
        while (flag) {
            //online users
            if (status == 1) {
                System.out.println("pasok online");
                String textMsg = streamRdr.readLine();
                System.out.println("rpivate chat +"+textMsg);
                if (textMsg.equalsIgnoreCase("01100101 01111000 01101001 01110100")) {//client has closed the chat window
                    close();
                    break;
                }
                String[] splitText = textMsg.split("•");
                String sender = splitText[0];
                String receiver = splitText[1];
                String message = splitText[2];
                //sometimes clients get mixed up. this prevents that.
                if (sender.equals(yourName) && receiver.equals(personToChatWith))
                    sendMessage(message);
            }

            // for offline users
            else if (status == 0) {
                String sender = "";
                String receiver = "";
                while (true) {
                    String textMsg = streamRdr.readLine();
                    System.out.println("pasok offine");
                    System.out.println("Text entered by the user: "+textMsg);
                    if(textMsg.equals("01100101 01111000 01101001 01110100")){
                        flag = false;
                        break;
                    }else{
                        System.out.println("pasok nanaman");
                        String[] splitText = textMsg.split("•");
                        sender = splitText[0];
                        receiver = splitText[1];
                        String message = splitText[2];
                        messageList.add(message);
                    }
                }

                //to check that the offline message is for the right person
                if (sender.equals(yourName) && receiver.equals(personToChatWith)) {
                    offlineMessagesWriter.fileWriter(yourName, personToChatWith, messageList);
                    //sentMessages();
                }
            }
        }
    }

    /**
     * The sendMessage method is invoked under the privateChats(0),
     * which allows the user to have private chats with online users.
     *
     * @param textMsg
     */
    public void sendMessage(String textMsg){
        try{
            for (int i = 0; i < listOfUsers.size(); i++) {
                PrivateMessagingHandler user = listOfUsers.get(i);
                if (user.yourName.equals(personToChatWith) && user.personToChatWith.equals(yourName)) {
                    user.streamWtr.write(yourName + ": " + textMsg);
                    user.streamWtr.newLine();
                    user.streamWtr.flush();
                }else{
                    System.out.println("pasok sa offline mesg");
                    messageList.add(textMsg);
                    System.out.println(textMsg);
                    offlineMessagesWriter.fileWriter(yourName, personToChatWith, messageList);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * The close method is invoked if the user chooses to exit; if that's
     * the case, it will remove the current object, causing the program to
     * return to where it was before that state.
     */
    public void close() {
        for (int i = 0; i < listOfUsers.size(); i++) {
            PrivateMessagingHandler user = listOfUsers.get(i);
            user.getNewList().remove(getYourName());
            user.getListOfUsers().remove(this);
        }
        newList.remove(getYourName());
        listOfUsers.remove(this);
    }
}
