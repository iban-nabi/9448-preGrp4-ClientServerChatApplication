package chat_server.group_chat;

import chat_server.tools.WriteToDOMtoFile;
import chat_server.tools.XMLReader;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import static chat_server.tools.XMLReader.xmlReader;

public class GroupMessagingHandler implements Runnable{
    private Socket socket;
    private BufferedReader streamRdr;
    private BufferedWriter streamWtr;
    private List<String> pendingMembers = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    private String admin;
    private List<String> members = Collections.synchronizedList(new CopyOnWriteArrayList<>());
    private String name;
    private String groupName;
    private static List<GroupMessagingHandler> listOfOnlineUsers = new CopyOnWriteArrayList<>();
    private final List<String>listOfUsers = new ArrayList<>();



    public GroupMessagingHandler(Socket socket) throws IOException {
        try{
            this.socket = socket;
            streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
        }catch (Exception e){
            close();
        }
    }

    public void setGroupName(String groupName){
        this.groupName = groupName;
    }

    public void setAdmin(String admin){
        this.admin=admin;
    }

    public void setName(String name){
        this.name=name;
        listOfOnlineUsers.add(this);
    }

    public void setMembers(List<String> members) {
        this.members = members;
    }

    public void setPendingMembers(List<String> pendingMembers) {
        this.pendingMembers = pendingMembers;
    }

    public void addMember(String member){
        members.add(member);
    }

    public void addPendingMember(String pendingMember){
        pendingMembers.add(pendingMember);
    }

    public String getGroupName(){
        return groupName;
    }

    public List<String> getMembers(){
        return members;
    }

    public List<String> getPendingMembers() {
        return pendingMembers;
    }


    /**
     * Execute the messaging for group chat
     */
    @Override
    public void run() {
        try {
            shareMessage();
        } catch (IOException | ParserConfigurationException | SAXException e) {
            close();
        }
    }

    /**
     * Method that receives the message entered by the user, and then send it to the other users within a specific group chat
     * by calling the sendMessage method. If the server receives a certain string, it will stop the current user activity
     * within the group chat by calling the close method.
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public void shareMessage() throws IOException, ParserConfigurationException, SAXException {
        while(true){
            String textMsg = streamRdr.readLine();
            if(textMsg.charAt(0)=='@'){
                executeCommand(textMsg);
            }else{
                if (textMsg.equals("01100101 01111000 01101001 01110100")) {
                    close();
                    break;
                } else {
                    sendMessage(textMsg);
                }
            }
        }
    }

    /**
     * The method is called when a command was entered by the user. If the command is @accept, the method will execute
     * the codes and methods regarding in accepting the invitation. This will update the list of pending members
     * (remove the current user), list of members (adding the current user) and the list of GroupMessagingHandler (including
     * the current user within the group chat messaging service). The updateGroupStatus will also be called in order to
     * change the status of the current user from Pending to Accepted. If the command is @add, the method will execute
     * the method in adding a specific user in the group chat. If the command is used @remove, the method will
     * execute the method in removing a specific user in the group, the command will only execute if the current
     * user is the admin of the group. If the command is invalid, display a message "Invalid Command".
     * @param command command entered by the user
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    public void executeCommand(String command) throws ParserConfigurationException, IOException, SAXException {
        verifyUser();
        if(command.equals("@accept")){
            pendingMembers.remove(name);
            members.add(name);
            for(GroupMessagingHandler user: listOfOnlineUsers){
                if(!user.name.equals(name) && user.groupName.equals(groupName) && !pendingMembers.contains(user.name)){
                    user.pendingMembers.remove(name);
                    user.members.add(name);
                }
            }
            updateGroupStatus();

        }else{
            String[] commandArray = command.split(":");

            if(commandArray[0].equals("@add")){
                updateGroupChat(commandArray[0],commandArray[1]);

            }else if(commandArray[0].equals("@remove") && name.equals(admin)){
                updateGroupChat(commandArray[0],commandArray[1]);

            }else if(commandArray[0].equals("@remove") && !name.equals(admin)){
                streamWtr.write("Command for admin only");
                streamWtr.newLine();
                streamWtr.flush();
            }else{
                streamWtr.write("Invalid Command");
                streamWtr.newLine();
                streamWtr.flush();
            }
        }
    }


    /**
     * Method that delivers the message to every user within a specifc group chat
     * @param textMsg users message to be sent to the other users in the group chat
     */
    public void sendMessage(String textMsg){
        try{
            for(GroupMessagingHandler user: listOfOnlineUsers){
                if(!user.name.equals(name) && user.groupName.equals(groupName) && !pendingMembers.contains(name)
                        && !pendingMembers.contains(user.name)){
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
    public void close() {
        listOfOnlineUsers.remove(this);
    }


    /**
     * This method reads the xml file, process it and updates the status of the newly accepted member by calling the
     * statusChange method.
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private void updateGroupStatus() throws ParserConfigurationException, IOException, SAXException {
        String fileName = "res/GroupChats.xml";
        Document document = XMLReader.xmlReader(fileName);
        Document newDocument = statusChange(document);
        WriteToDOMtoFile.writeDOMtoFile(newDocument,fileName);
    }


    /**
     * This method will change the status of the newly accepted user from "Pending to "Accepted
     * @param document processed xml file for Group Chat
     * @return updated document
     */
    private Document statusChange(Document document){
        NodeList grpChats = document.getElementsByTagName("GRP");
        for(int i = 0;i<grpChats.getLength();i++) {
            Node node = grpChats.item(i);
            Element e = (Element) node;
            if(e.getAttribute("name").equals(groupName)){
                NodeList members = e.getElementsByTagName("MEMBER");
                for(int j=0; j<members.getLength();j++){
                    Node member = members.item(j);
                    Element memberElement = (Element) member;
                    if(member.getTextContent().equals(name)){
                        memberElement.setAttribute("status","Accepted");
                    }
                }
            }
        }
        return document;
    }

    /**
     * This method will execute a certain code and methods depending on the command entered. If the command is @add, it
     * will check if the user to added is not a member of the group chat. If the added is user is not a member, then the
     * addUser method will be executed to update the XML file that stores the data for the group chat. Same with the
     * remove command. If the user is in the group chat, the method removeUser will be executed to update the file for
     * the group chat.
     * @param update command entered by the user
     * @param user name of the user to be added or removed
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private void updateGroupChat(String update,String user) throws ParserConfigurationException, IOException, SAXException {
        if(update.equals("@add")){ // add codes to valdate if the user exist or that he/she is already in the grp chat
            if(listOfUsers.contains(user)){
                if(members.contains(user) || pendingMembers.contains(user)){
                    streamWtr.write("User is already in the group chat");
                    streamWtr.newLine();
                    streamWtr.flush();
                }else{
                    addUser(user);
                }
            }else{
                streamWtr.write("User does not exist");
                streamWtr.newLine();
                streamWtr.flush();
            }
        }else{
            if(members.contains(user) || pendingMembers.contains(user)){
                removeUser(user);
            }else{
                streamWtr.write("Member not found");
                streamWtr.newLine();
                streamWtr.flush();
            }

        }
    }


    /**
     * This method will call the methods in handling XML processing and manipulation (adding)
     * @param user to be added in the group chat
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private void addUser(String user) throws ParserConfigurationException, IOException, SAXException {
        String fileName = "res/GroupChats.xml";
        Document document = XMLReader.xmlReader(fileName);
        Document newDocument = addData(document,user);
        WriteToDOMtoFile.writeDOMtoFile(newDocument,fileName);
    }


    /**
     * This method will access the data of the document in order to identify the nodes with an attribute of the group chat
     * where a member will be added. Once the method accessed that specific group chat, the member will be appended to the
     * document, set its status to pending and add that member to the pending member list.
     * @param document processed xml file
     * @param user to be added in the group chat
     * @return updated document
     */
    private Document addData(Document document, String user){
        NodeList nodes = document.getElementsByTagName("GRP");

        for(int i=0; i < nodes.getLength(); i++){
            Node currentNode = nodes.item(i);
            Element e = (Element) currentNode;
            String grpName = e.getAttribute("name");
            if(grpName.equals(groupName)){
                Element username = document.createElement("MEMBER");
                username.setAttribute("status","Pending");
                Text usernameText = document.createTextNode(user);
                username.appendChild(usernameText);
                e.appendChild(username);
                for(GroupMessagingHandler u: listOfOnlineUsers){
                    if(u.groupName.equals(grpName)){
                        u.pendingMembers.add(user);
                    }
                }
            }
        }
        return document;
    }


    /**
     * This method will remove the specific user in the list of pending/accepted members as well as the list of
     * GroupMessagingHandler so that user is not able to receive and send messages if he/she is currently in the group
     * chat messaging gui. The removeUserFromGrpData method will also be executed to update the xml file for group chats.
     * @param user to be removed in the group chat
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    private void removeUser(String user) throws IOException, ParserConfigurationException, SAXException {
        for(GroupMessagingHandler member : listOfOnlineUsers){
            if(member.name.equals(user)){
                member.streamWtr.write("You have been removed from the group chat, your messages won't be sent in the group");
                member.streamWtr.newLine();
                member.streamWtr.flush();
                member.listOfOnlineUsers.clear();
                listOfOnlineUsers.remove(member);
                break;
            }
        }

        for(GroupMessagingHandler u: listOfOnlineUsers){
            if(u.groupName.equals(groupName)){
                members.remove(user);
                pendingMembers.remove(user);
            }
        }
        removeUserFromGrpData(user);
    }

    /**
     * This method will call the methods in handling XML processing and manipulation (removing)
     * @param user to removed from the group chat
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    private void removeUserFromGrpData(String user) throws ParserConfigurationException, IOException, SAXException {
        String fileName = "res/GroupChats.xml";
        Document document = XMLReader.xmlReader(fileName);
        Document newDocument = removeData(document,user);
        WriteToDOMtoFile.writeDOMtoFile(newDocument,fileName);
    }

    /**
     * This method will access the data of the document in order to identify the members of all the group chat. 
     * Once the method accessed that specific member, it will access its parent node attribute to check the group
     * chat name. If the group chat name is correct, then the member will be removed from the document.
     * @param document processed xml file
     * @param user to be removed from the group chat
     * @return updated document
     */
    private Document removeData(Document document, String user){
        NodeList nodes = document.getElementsByTagName("MEMBER");

        for(int i=0; i < nodes.getLength(); i++){
            Node currentNode = nodes.item(i);
            String username = currentNode.getTextContent();
            if(username.equals(user)){
                Node grp = currentNode.getParentNode();
                Element e = (Element) grp;
                if(e.getAttribute("name").equals(groupName)){
                    grp.removeChild(currentNode);
                    break;
                }
            }
        }
        return document;
    }


    /**
     * The method populates a list of string that holds all the username of chatting application. The list will be used
     * to verify if the to be added member in the group chat is an existing account.
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    public void verifyUser() throws ParserConfigurationException, IOException, SAXException {
        listOfUsers.clear();
        String fileName = "res/Accounts.xml";
        Document doc = xmlReader(fileName);
        doc.getDocumentElement().normalize();

        NodeList accountXML = doc.getElementsByTagName("USER");
        for(int i = 0;i<accountXML.getLength();i++) {
            Node node = accountXML.item(i);
            Element e = (Element) node;
            String username = e.getElementsByTagName("NAME").item(0).getTextContent();
            listOfUsers.add(username);
        }
    }
}
