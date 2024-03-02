package chat_server;

import chat_server.global_chat.GlobalMessagingHandler;
import chat_server.group_chat.CreateChatRoom;
import chat_server.group_chat.GroupChatBookmark;
import chat_server.group_chat.GroupMessagingHandler;
import chat_server.private_chat.PrivateMessagingHandler;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import static chat_server.tools.XMLReader.xmlReader;

public class ChatServerHandlerGUI extends Thread {
    private String name;
    private final BufferedReader streamRdr;
    private final BufferedWriter streamWtr;
    private String inputUsername;
    private final Socket socket;
    private static final List<ChatServerHandlerGUI> onlineUsers = new ArrayList<>();
    private final List<GroupMessagingHandler>userGroupChats = new ArrayList<>();

    public ChatServerHandlerGUI(Socket socket) throws IOException {
        this.socket=socket;
        streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
    }

    /**
     * The  run method will execute the main codes in order for the server to allow the chatting
     * service for multiple clients. The server will first execute the login method in order to
     * verify the user. After that, the server will execute either the global chat server, group
     * chat service, and private chat service depending on the user. The global chat will allow
     * all users that entered the global server to communicate with each other by executing the
     * globalChatRoom.run(). While for the group chat, the user could select which group chat
     * server to enter to by executing the userGroupChats.get(selection).run(). The user can also
     * create a group chat (createChatRoom.runCreateChatRoom()) or mark a specific group chat as
     * a favorite. // dagdag nlng ung private chat chuchu
     */
    public void run() {

        try{
            int loginSuccess = 1;
            while(loginSuccess!=0){
                String username = streamRdr.readLine();
                String password = streamRdr.readLine();
                loginSuccess=login(username,password);
                streamWtr.write(String.valueOf(loginSuccess));
                streamWtr.newLine();
                streamWtr.flush();
            }

            onlineUsers.add(this);
            boolean running = true;
            while(running){
                userGroupChats.clear();
                int choice = Integer.parseInt(streamRdr.readLine());
                switch(choice){
                    case 1:
                        GlobalMessagingHandler globalChatRoom = new GlobalMessagingHandler(socket,name);
                        globalChatRoom.run();
                        streamWtr.write(" ");
                        streamWtr.newLine();
                        streamWtr.flush();
                        break;

                    case 2:
                        GroupChatBookmark groupChatBookMark = new GroupChatBookmark(name);
                        List<String>bookmarkedGroupChats = groupChatBookMark.populateUserMarkedGroupChat();
                        processUserGroupChats(bookmarkedGroupChats);
                        if(userGroupChats.size()!=0){
                            String groupChatName;
                            for(GroupMessagingHandler groupChat: userGroupChats){
                                if(groupChat.getPendingMembers().contains(name)){
                                    groupChatName=groupChat.getGroupName()+" (Accept Pending)";
                                }else{
                                    groupChatName=groupChat.getGroupName();
                                }

                                if(bookmarkedGroupChats.contains(groupChat.getGroupName())){
                                    String star = new String(Character.toChars('\u2B50'));
                                    groupChatName= groupChatName+ star;
                                }
                                streamWtr.write(groupChatName);
                                streamWtr.newLine();
                                streamWtr.flush();
                            }
                        }else{
                            streamWtr.write("No Group Chats");
                            streamWtr.newLine();
                            streamWtr.flush();
                        }
                        streamWtr.write(""); // identifier
                        streamWtr.newLine();
                        streamWtr.flush();

                        int selection = Integer.parseInt(streamRdr.readLine());

                        if(selection>=0){ // enter group chat
                            userGroupChats.get(selection).setName(name);
                            userGroupChats.get(selection).run();
                            streamWtr.write(" ");
                            streamWtr.newLine();
                            streamWtr.flush();

                        }else{
                            if(selection==-1100){ // create group chat
                                CreateChatRoom createChatRoom = new CreateChatRoom(socket,name);
                                createChatRoom.runCreateChatRoom();

                            }else if(selection == -1101) { // mark a specific group
                                int i = Integer.parseInt(streamRdr.readLine());
                                groupChatBookMark.updateGroupChatBookmark(userGroupChats.get(i).getGroupName());
                            }else{ // exit
                                break;
                            }
                        }
                        break;

                    case 3: // for private chats
                        try {
                                PrivateMessagingHandler chatRoom = new PrivateMessagingHandler(socket, name, "Private");
                                chatRoom.run();
                                streamWtr.write(" ");
                                streamWtr.newLine();
                                streamWtr.flush();
                        } catch (Exception e) {e.printStackTrace();}
                        break;

                    case 4: // logout
                        onlineUsers.remove(this);
                        running = false;
                        break;
                }
            }
        } catch (IOException | ParserConfigurationException | SAXException e) {
            e.getLocalizedMessage();
        } finally {
            try {
                onlineUsers.clear();
                streamRdr.close();
                streamWtr.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * The login method will validate the entered credentials of the current user. The method will extract the
     * contents of the Accounts.xml, process it and then compare it to the entered username and password.
     * @param inputUsername the entered username of the current user
     * @param inputPassword the entered password of the current user
     * @return an integer value depending on the success of login
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private int login(String inputUsername, String inputPassword)
            throws ParserConfigurationException, IOException, SAXException {
        //returns 0 if found, returns 1 if blocked, returns 2 if online, returns 3 if invalid
        String fileName = "res/Accounts.xml";
        Document doc = xmlReader(fileName);
        doc.getDocumentElement().normalize();

        NodeList accountXML = doc.getElementsByTagName("USER");
        for(int i = 0; i<accountXML.getLength(); i++) {
            Node node = accountXML.item(i);
            Element e = (Element) node;
            String username = e.getElementsByTagName("NAME").item(0).getTextContent();
            String password = e.getElementsByTagName("PASSWORD").item(0).getTextContent();
            if (e.getAttribute("status").equals("blocked")){
                return 1;
            }
            if(inputUsername.equalsIgnoreCase(username) && inputPassword.equals(password)){
                if(onlineUsers.stream().noneMatch(user -> user.name.equals(username))){
                    name = inputUsername;
                    return 0;
                }else{
                    return 2;
                }
            }
        }
        return 3;
    }

    /**
     * The processUserGroupChat method will extract the GroupChats.xml in order to process and create
     * the user's group chats. Each group chats will be stored in a List of GroupMessagingHandler. After
     * processing the user's group chats, the method will then order it by favorites.
     * @param bookmarkedGroupChats
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private void processUserGroupChats(List<String>bookmarkedGroupChats) throws ParserConfigurationException, IOException, SAXException {
        userGroupChats.clear();
        String fileName = "res/GroupChats.xml";
        Document doc = xmlReader(fileName);
        doc.getDocumentElement().normalize();
        NodeList grpChatXML = doc.getElementsByTagName("GRP");
        for(int i = 0;i<grpChatXML.getLength();i++) {
            GroupMessagingHandler groupChat = new GroupMessagingHandler(socket);
            List<String>memberList = new ArrayList<>();
            List<String>pendingMemberList = new ArrayList<>();
            Node node = grpChatXML.item(i);
            Element e = (Element) node;
            String grpChatName = e.getAttribute("name");
            String grpAdmin = e.getElementsByTagName("ADMIN").item(0).getTextContent();
            NodeList members = e.getElementsByTagName("MEMBER");
            memberList.add(grpAdmin);
            for(int j=0; j<members.getLength();j++){
                Node member = members.item(j);
                Element memberElement = (Element) member;
                if(memberElement.getAttribute("status").equals("Accepted")){
                    memberList.add(memberElement.getTextContent());
                }else{
                    pendingMemberList.add(memberElement.getTextContent());
                }
            }
            if(memberList.contains(name) || pendingMemberList.contains(name)){
                groupChat.setGroupName(grpChatName);
                groupChat.setAdmin(grpAdmin);
                groupChat.setPendingMembers(pendingMemberList);
                groupChat.setMembers(memberList);
                userGroupChats.add(groupChat);
            }
        }

        if(bookmarkedGroupChats.size()!=0){
            int position = 0;
            for(int i=0; i< bookmarkedGroupChats.size();i++){
                for(int j=0; j<userGroupChats.size();j++){
                    if(bookmarkedGroupChats.get(i).equals(userGroupChats.get(j).getGroupName())){
                        GroupMessagingHandler temp = userGroupChats.get(position);
                        userGroupChats.set(position,userGroupChats.get(j));
                        userGroupChats.set(j,temp);
                        position++;
                    }
                }
            }
        }
    }
}
