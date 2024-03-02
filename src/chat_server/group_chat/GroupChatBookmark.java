package chat_server.group_chat;

import chat_server.tools.WriteToDOMtoFile;
import chat_server.tools.XMLReader;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GroupChatBookmark {
    String user;
    /**
     * Constructor for the GroupChatBookMark.
     * @param user current user.
     */
    public GroupChatBookmark(String user){
        this.user = user;
    }

    /**
     * The updateGroupChatBookmark method processes the bookmarks of the user's group chat. The method will read the
     * GroupBookmarks.xml and process it to a document. The method will then identify its contents until the it reads
     * the data of the current user. If the current user in not within the XML, it will append a new data regarding the
     * bookmark of the user. If the user is existing the XML file, the user could either append the group chat or remove
     * the group chat from the users bookmarks.
     * @param grpName name of the group chat the user wants to bookmark
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    public void updateGroupChatBookmark(String grpName) throws ParserConfigurationException, IOException, SAXException {
        NodeList grpChatBookMark = null;
        Node parent = null;
        boolean available = false;
        String fileName = "res/GroupBookmarks.xml";
        Document document = XMLReader.xmlReader(fileName);
        NodeList nodes = document.getElementsByTagName("ACCOUNT");
        for(int i = 0; i< nodes.getLength();i++){
            Node currentNode = nodes.item(i);
            Element element = (Element) currentNode;
            String user = element.getAttribute("USER");
            if(user.equals(this.user)){
                parent = currentNode;
                grpChatBookMark = element.getElementsByTagName("GROUP_CHAT");
                available = true;
            }
        }

        if(!available){ // create bookmark list for user
            document = createGroupBookmark(document,grpName);
        }else{ // remove from bookmark
            boolean marked = false;
            for(int j = 0; j< grpChatBookMark.getLength();j++){
                Node currentNode = grpChatBookMark.item(j);
                parent = currentNode.getParentNode();
                Element element = (Element) currentNode;
                String grpChatMarked = element.getTextContent();
                if(grpChatMarked.equals(grpName)){
                    marked=true;
                    parent.removeChild(currentNode);
                }
            }
            if(!marked){ // add bookmark
                Node root = document.getDocumentElement();
                Element grpChatName = document.createElement("GROUP_CHAT");
                Text grpChatText = document.createTextNode(grpName);
                grpChatName.appendChild(grpChatText);
                parent.appendChild(grpChatName);
                root.appendChild(parent);
            }
        }
        WriteToDOMtoFile.writeDOMtoFile(document,fileName);
    }

    /**
     * Method which creates the data of the user with no bookmarks yet and append it to the root of the document.
     * @param document the processed xml file
     * @param grpName the group chat's name to be included in the bookmark of the user
     * @return document
     */
    public Document createGroupBookmark(Document document,String grpName){
        Element root = document.getDocumentElement();
        Element account = document.createElement("ACCOUNT");
        account.setAttribute("USER", user);
        root.appendChild(account);
        Element grpChatName = document.createElement("GROUP_CHAT");
        Text grpChatText = document.createTextNode(grpName);
        grpChatName.appendChild(grpChatText);
        account.appendChild(grpChatName);
        root.appendChild(account);
        return document;
    }

    /**
     * Reads the xml file for the group chat bookmarks and process its data. The data will be stored in a String list,
     * and this list holds the names of the currently bookmarked group chats of the current user.
     * @return list of marked group chats depending on the current user
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public List<String> populateUserMarkedGroupChat() throws ParserConfigurationException, IOException, SAXException {
        List<String> listOfMarkedGrpChats = new ArrayList<>();
        String fileName = "res/GroupBookmarks.xml";
        Document document = XMLReader.xmlReader(fileName);
        NodeList nodes = document.getElementsByTagName("ACCOUNT");
        for(int i = 0; i< nodes.getLength();i++){
            Node currentNode = nodes.item(i);
            Element element = (Element) currentNode;
            String user = element.getAttribute("USER");
            if(user.equals(this.user)){
                NodeList markedGrpChats = element.getElementsByTagName("GROUP_CHAT");
                for(int j = 0; j< markedGrpChats.getLength(); j++){
                    Node grpChat = markedGrpChats.item(j);
                    listOfMarkedGrpChats.add(grpChat.getTextContent());
                }
            }
        }
        return listOfMarkedGrpChats;
    }
}
