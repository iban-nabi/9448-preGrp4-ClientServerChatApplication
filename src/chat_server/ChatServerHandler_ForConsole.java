package chat_server;

import chat_server.global_chat.GlobalMessagingHandler;
import chat_server.group_chat.GroupMessagingHandler;
import chat_server.group_chat.CreateChatRoom;
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

public class ChatServerHandler_ForConsole extends Thread{


    private String name;
    private final BufferedReader streamRdr;
    private final BufferedWriter streamWtr;
    private String inputUsername;
    private final Socket socket;
    private static final List<ChatServerHandler_ForConsole> onlineUsers = new ArrayList<>();
    private final List<GroupMessagingHandler> listOfGroupChats = new ArrayList<>();
    private final List<GroupMessagingHandler>userGroupChats = new ArrayList<>();

    public ChatServerHandler_ForConsole(Socket socket) throws IOException {
        this.socket=socket;
        streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
    }

    @Override
    public void run() {
        try{
            boolean loginSuccess = false;
            while(!loginSuccess){
                loginSuccess=login();
            }
            onlineUsers.add(this);
            boolean running = true;
            while(running){
                processGroupChat();
                processUserGroupChat();
                int choice = Integer.parseInt(streamRdr.readLine());
                switch(choice){
                    case 1:
                        GlobalMessagingHandler globalChatRoom = new GlobalMessagingHandler(socket,name);
                        globalChatRoom.run();
                        streamWtr.newLine();
                        streamWtr.flush();
                        break;

                    case 2: // group chats
                        if(userGroupChats.size()!=0){
                            String groupChatName;
                            for(GroupMessagingHandler groupChat: userGroupChats){
                                System.out.println("size:"+groupChat.getPendingMembers().size());
                                System.out.println("member:"+groupChat.getPendingMembers());
                                if(groupChat.getPendingMembers().contains(name)){
                                    groupChatName=groupChat.getGroupName()+" (Accept Pending)";
                                }else{
                                    groupChatName=groupChat.getGroupName();
                                }
                                streamWtr.write(groupChatName);
                                streamWtr.newLine();
                                streamWtr.flush();
                            }
                            streamWtr.write("");
                            streamWtr.newLine();
                            streamWtr.flush();
                        }else{
                            streamWtr.write("No Group Chats");
                            streamWtr.newLine();
                            streamWtr.flush();
                        }
                        streamWtr.write("0 - Create New Group Chat");
                        streamWtr.newLine();
                        streamWtr.flush();

                        int selectedGroupChat = Integer.parseInt(streamRdr.readLine());

                        if(selectedGroupChat!=0){
                            userGroupChats.get(selectedGroupChat-1).setName(name);
                            userGroupChats.get(selectedGroupChat-1).run();
                        }else{
                            CreateChatRoom createChatRoom = new CreateChatRoom(socket,name);
                            createChatRoom.runCreateChatRoom();
                        }

                        streamWtr.newLine();
                        streamWtr.flush();
                        break;

                    case 3: // for private chats
                        try {
                            while (true) {
                                PrivateMessagingHandler chatRoom = new PrivateMessagingHandler(socket, name, "Private");
                                chatRoom.run();
                            }
                        } catch (Exception e) {e.printStackTrace();}
                        streamWtr.newLine();
                        streamWtr.flush();
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
                streamRdr.close();
                streamWtr.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean login() throws ParserConfigurationException, IOException, SAXException {
        inputUsername = streamRdr.readLine();
        String inputPassword = streamRdr.readLine();

        String fileName = "res/Accounts.xml";
        Document doc = xmlReader(fileName);
        doc.getDocumentElement().normalize();

        NodeList accountXML = doc.getElementsByTagName("USER");
        for(int i = 0;i<accountXML.getLength();i++) {
            Node node = accountXML.item(i);
            Element e = (Element) node;
            String username = e.getElementsByTagName("NAME").item(0).getTextContent();
            String password = e.getElementsByTagName("PASSWORD").item(0).getTextContent();
            if (e.getAttribute("status").equals("blocked")){
                streamWtr.write("User is blocked.");
                streamWtr.newLine();
                streamWtr.flush();
                return false;
            }
            if(inputUsername.equalsIgnoreCase(username) && inputPassword.equals(password)){
                if(onlineUsers.stream().noneMatch(user -> user.name.equals(inputUsername))){
                    name=username;
                    streamWtr.newLine();
                    streamWtr.flush();
                    streamWtr.write("");
                    streamWtr.flush();
                    return true;
                }else{
                    streamWtr.write("User is already login");
                    streamWtr.newLine();
                    streamWtr.flush();
                    return false;
                }
            }
        }
        streamWtr.write("Invalid Username or Password");
        streamWtr.newLine();
        streamWtr.flush();
        return false;
    }

    public void processGroupChat() throws ParserConfigurationException, IOException, SAXException {
        listOfGroupChats.clear();
        String fileName = "res/GroupChats.xml";
        Document doc = xmlReader(fileName);
        doc.getDocumentElement().normalize();

        NodeList grpChatXML = doc.getElementsByTagName("GRP");
        for(int i = 0;i<grpChatXML.getLength();i++) {
            GroupMessagingHandler groupChat = new GroupMessagingHandler(socket);
            Node node = grpChatXML.item(i);
            Element e = (Element) node;
            groupChat.setGroupName(e.getAttribute("name"));
            groupChat.setAdmin(e.getElementsByTagName("ADMIN").item(0).getTextContent());
            groupChat.addMember(e.getElementsByTagName("ADMIN").item(0).getTextContent()); // add the admin to the list of members

            NodeList members = e.getElementsByTagName("MEMBER");
            for(int j=0; j<members.getLength();j++){
                Node member = members.item(j);
                Element memberElement = (Element) member;
                if(memberElement.getAttribute("status").equals("Accepted")){
                    groupChat.addMember(member.getTextContent());
                }else{
                    groupChat.addPendingMember(member.getTextContent());
                }
            }
            listOfGroupChats.add(groupChat);
        }
    }

    public void processUserGroupChat(){
        userGroupChats.clear();
        for(GroupMessagingHandler groupChat : listOfGroupChats){
            for(String member: groupChat.getMembers()){
                if(name.equals(member)){
                    userGroupChats.add(groupChat);
                    break;
                }
            }
            for(String member: groupChat.getPendingMembers()) {
                if (name.equals(member)) {
                    userGroupChats.add(groupChat);
                    break;
                }
            }
        }
    }
}
