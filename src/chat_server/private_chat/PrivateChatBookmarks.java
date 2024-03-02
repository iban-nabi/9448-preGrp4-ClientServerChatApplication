package chat_server.private_chat;

import chat_server.tools.WriteToDOMtoFile;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PrivateChatBookmarks {
    String user;
    String filePath = "res/PrivateChatBookmarks.xml";

    /**
     * Provide the logged in account's username to retrieve their bookmarks list.
     * @param user The username of the logged in account.
     */
    public PrivateChatBookmarks(String user){
        this.user = user;
    }

    /**
     * Add a specified name to the user's bookmarks.
     * @param name name to be added to bookmarks
     * @return Returns 1 if name is successfully added.
     * <br>Returns 0 if the name is already in the list
     * <br>Returns -1 if an error occurred.
     */
    public int addToBookmarkedList(String name){
        Document documentBookmarks;


        ArrayList<String> listOfBookmarked = getBookmarkedList();
        for (int i=0; i<listOfBookmarked.size(); i++) {
            if (listOfBookmarked.get(i).equals(name))
                return 0;
        }
        try {
            File fileBookmarks = new File(filePath);
            DocumentBuilderFactory builderFactoryBookmarks = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilderBookmarks = builderFactoryBookmarks.newDocumentBuilder();
            documentBookmarks = documentBuilderBookmarks.parse(fileBookmarks);
            documentBookmarks.getDocumentElement().normalize();

            Element root = documentBookmarks.getDocumentElement();

            Element account;
            Element marked;
            boolean existing = false;
            int index = 0;
            NodeList accountList = root.getElementsByTagName("ACCOUNT");
            for (int i = 0; i < accountList.getLength(); i++) {
                Element stored = (Element) accountList.item(i);
                if (stored.getAttribute("USER").equals(user)) {
                    existing = true;
                    index = i;
                }
            }

            if (existing) {
                account = (Element) accountList.item(index);
                marked = documentBookmarks.createElement("MARKED");
                Text txtData = documentBookmarks.createTextNode(name);
                marked.appendChild(txtData);
                account.appendChild(marked);
            } else {
                account = documentBookmarks.createElement("ACCOUNT");
                account.setAttribute("USER", user);
                root.appendChild(account);
                marked = documentBookmarks.createElement("MARKED");
                Text txtData = documentBookmarks.createTextNode(name);
                marked.appendChild(txtData);
                account.appendChild(marked);
            }
            WriteToDOMtoFile.writeDOMtoFile(documentBookmarks, filePath);
            return 1;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Remove a specified name to the user's bookmarks.
     * @param name name to be removed from bookmarks
     * @return Returns 1 if name is successfully removed.
     * <br>Returns 0 if the name is not found.
     * <br>Returns -1 if an error occurred.
     */
    public int removeFromBookmarkedList(String name){
        Document documentBookmarks;


        ArrayList<String> listOfBookmarked = getBookmarkedList();
        boolean containsName = false;
        for (int i=0; i<listOfBookmarked.size(); i++) {
            if (listOfBookmarked.get(i).equals(name)) {
                containsName = true;
                break;
            }
        }
        if (!containsName)
            return 0;
        try {
            File fileBookmarks = new File(filePath);
            DocumentBuilderFactory builderFactoryBookmarks = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilderBookmarks = builderFactoryBookmarks.newDocumentBuilder();
            documentBookmarks = documentBuilderBookmarks.parse(fileBookmarks);
            documentBookmarks.getDocumentElement().normalize();

            Element root = documentBookmarks.getDocumentElement();

            Element account;
            NodeList markedList;
            boolean existing = false;
            int index = 0;
            NodeList accountList = root.getElementsByTagName("ACCOUNT");
            for (int i = 0; i < accountList.getLength(); i++) {
                Element stored = (Element) accountList.item(i);
                if (stored.getAttribute("USER").equals(user)) {
                    existing = true;
                    index = i;
                }
            }
            Element stored;
            if (existing) {
                account = (Element) accountList.item(index);
                markedList = account.getElementsByTagName("MARKED");
                for (int i = 0; i < markedList.getLength(); i++) {
                    stored = (Element) markedList.item(i);
                    if (stored.getFirstChild().getNodeValue().equals(name)) {
                        account.removeChild(markedList.item(i));
                        WriteToDOMtoFile.writeDOMtoFile(documentBookmarks, filePath);
                        return 1;
                    }
                }
            } else
                return 0;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return -1;
    }


    /**
     * Returns an ArrayList containing all stored bookmarks of the user.
     * @return ArrayList <String> of usernames
     */
    ArrayList<String> getBookmarkedList(){
        ArrayList<String> userList = new ArrayList<>();
        Document documentBookmarks;
        try {
            File fileBookmarks = new File(filePath);
            DocumentBuilderFactory builderFactoryBookmarks = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilderBookmarks = builderFactoryBookmarks.newDocumentBuilder();
            documentBookmarks = documentBuilderBookmarks.parse(fileBookmarks);
            documentBookmarks.getDocumentElement().normalize();

            Element root = documentBookmarks.getDocumentElement();

            Element account;
            Element marked;
            boolean existing = false;
            int index = 0;
            NodeList accountList = root.getElementsByTagName("ACCOUNT");
            for (int i = 0; i < accountList.getLength(); i++) {
                Element stored = (Element) accountList.item(i);
                if (stored.getAttribute("USER").equals(user)) {
                    existing = true;
                    index = i;
                }
            }

            if (existing) {
                account = (Element) accountList.item(index);
                NodeList markedList = account.getElementsByTagName("MARKED");
                for (int i = 0; i < markedList.getLength(); i++) {
                    userList.add(markedList.item(i).getTextContent());
                }
            } else {
                account = documentBookmarks.createElement("ACCOUNT");
                account.setAttribute("USER", user);
                root.appendChild(account);
                marked = documentBookmarks.createElement("MARKED");
                account.appendChild(marked);
            }
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return userList;
    }


    /**
     * Generate a list of names with the bookmarked names taken out.
     * @param userList list of complete names
     * @return list without bookmarked names
     */
    ArrayList<String> getNonBookmarkedList(ArrayList<String> userList){
        ArrayList<String> providedList = new ArrayList<>(userList);
        ArrayList<String> listOfBookmarked = new ArrayList<>(getBookmarkedList());

        for (int i = 0; i < providedList.size(); i++) {
            for (int p = 0; p < listOfBookmarked.size(); p++){
                if (providedList.get(i).equalsIgnoreCase(listOfBookmarked.get(p))) {
                    providedList.remove(i);
                    if (i>0) i--;
                }
            }
        }
        return providedList;
    }
}
