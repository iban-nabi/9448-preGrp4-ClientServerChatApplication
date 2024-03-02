package chat_server;

import chat_server.tools.WriteToDOMtoFile;
import chat_server.tools.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.crypto.SecretKey;
import javax.swing.*;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;

public class UserManagerFINAL {
    static String fileName = "res/Accounts.xml";
    static ArrayList <User> user = new ArrayList<>();
    public static ArrayList <String> blockList = new ArrayList<>();
    static String uName;

    /**
     * Creates a new user and returns true if the new user was added successfully.
     * Otherwise, it returns false if the username of the new user already exists
     * in the XML file.
     * @param username as the user's username
     * @param password as the user's password
     * @return true if the new user was added successfully, otherwise false.
     */
    public static boolean addUser(String username, String password){
        uName = username;
        Document document;
        try{
            document = XMLBuilder.xmlBuilder();
            Element root = document.createElement("Accounts");
            document.appendChild(root);
            Element newUser = document.createElement("USER");
            if (searchUser(uName, user)) {
                return false;
            }
            SecretKey passKey = Encryption.generateKeyFromPassword(password);
            password = Encryption.convertSecretKeyToString(passKey);

            createNewUserElement(newUser, document, uName, password);
            root.appendChild(newUser);

            rewriteXMLFile(document, root);
            clearList();
            WriteToDOMtoFile.writeDOMtoFile(document, fileName);
            return true;
        } catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Deletes a user by removing the user on the ArrayList of Users and creating a new XML File
     * without that specified user.
     * @param username as the user's username.
     */
    public static void deleteUser(String username){
        uName = username;
        Document document;
        try {
            document = XMLBuilder.xmlBuilder();
            Element root = document.createElement("Accounts");
            document.appendChild(root);
            for(int i = 0; i < user.size(); i++){
                if(user.get(i).getUsername().equals(uName)){
                    user.remove(i);
                    JOptionPane.showMessageDialog(null, "User is deleted",
                            "Delete Successful", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
            }
            rewriteXMLFile(document, root);
            clearList();
            WriteToDOMtoFile.writeDOMtoFile(document, fileName);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Blocks a user which makes the user unable to enter the server. Calls the setStatusAsBlocked (Document)
     * to create a new attribute with the name Status and the value Blocked to determine that a
     * specific user is blocked by the server.
     * @param username as the user's username.
     */
    public static void blockUser(String username){
        uName = username;
        Document document;
        try{
            document = XMLBuilder.xmlBuilder();
            Element root = document.createElement("Accounts");
            document.appendChild(root);

            rewriteXMLFile(document, root);
            setStatusAsBlocked(document);
            clearList();
            WriteToDOMtoFile.writeDOMtoFile(document, fileName);
            JOptionPane.showMessageDialog(null, "User is blocked",
                    "Block Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Unblocks a user by calling the removeStatusAsBlocked(Document) method to remove
     * the attribute set by the blockUser method.
     * @param username as the user's username
     */
    public static void unblockUser(String username){
        uName = username;
        Document document;
        try{
            document = XMLBuilder.xmlBuilder();
            Element root = document.createElement("Accounts");
            document.appendChild(root);

            clearList();
            readFile();
            removeStatusAsBlocked(document);
            rewriteXMLFile(document, root);
            clearList();
            WriteToDOMtoFile.writeDOMtoFile(document, fileName);
            JOptionPane.showMessageDialog(null, "User is unblocked",
                    "Unblock Successful", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * Clears the contents of the ArrayList User and ArrayList blockList.
     */
    private static void clearList(){
        user.clear();
        blockList.clear();
    }

    /**
     * Removes the attribute of a specific User.
     * @param document is the source document being referenced.
     */
    private static void removeStatusAsBlocked(Document document){
        NodeList nodes = document.getElementsByTagName("USER");
        for(int i = 0; i < nodes.getLength(); i++){
            Node node = nodes.item(i);
            Element element = (Element) node;
            String name = element
                    .getElementsByTagName("NAME")
                    .item(0)
                    .getTextContent();
            if (uName.equals(name)) {
                Element username = (Element) node;
                username.removeAttribute("status");
                break;
            }
        }
    }

    /**
     * Sets the attribute of a particular User as blocked.
     * @param document is the source document being referenced.
     */
    private static void setStatusAsBlocked(Document document){
        NodeList nodes = document.getElementsByTagName("USER");
        for(int i = 0; i < nodes.getLength(); i++){
            Node node = nodes.item(i);
            Element element = (Element) node;
            String name = element
                    .getElementsByTagName("NAME")
                    .item(0)
                    .getTextContent();
            if (uName.equals(name)) {
                Element username = (Element) node;
                username.setAttribute("status", "blocked");
                break;
            }
        }
    }

    /**
     * Creates a new user by creating elements using the createElement method and appendChild  methods
     * @param account is the element with a specified Tag Name
     * @param doc is the source document being referenced
     * @param username as the User's name
     * @param password as the User's password
     */
    private static void createNewUserElement(Element account, Document doc, String username, String password) {
        Element userAccount = doc.createElement("NAME");
        Text txtData = doc.createTextNode(username);
        userAccount.appendChild(txtData);
        Element pass = doc.createElement("PASSWORD");
        txtData = doc.createTextNode(password);
        pass.appendChild(txtData);

        account.appendChild(userAccount);
        account.appendChild(pass);
    }

    /**
     * Reads the file and stores it to the Users Arraylist and BlockList
     * @return ArrayList that stores the Users Object.
     */
    public static ArrayList<User> readFile(){
        try{
            String username;
            String password;
            User newUser;
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            Document oldDoc = docBuilder.parse(fileName);
            oldDoc.getDocumentElement().normalize();
            NodeList nodes = oldDoc.getElementsByTagName("USER");

            for (int i = 0; i < nodes.getLength(); i++){
                Node userNode = nodes.item(i);
                Element element = (Element) userNode;
                username = element
                        .getElementsByTagName("NAME")
                        .item(0)
                        .getTextContent();
                password = element
                        .getElementsByTagName("PASSWORD")
                        .item(0)
                        .getTextContent();
                newUser = new User(username, password);
                user.add(newUser);
            }
            for(int i = 0;i<nodes.getLength();i++) {
                Node node = nodes.item(i);
                Element e = (Element) node;
                if (e.getAttribute("status").equals("blocked")) {
                    blockList.add(e.getElementsByTagName("NAME").item(0).getTextContent());
                }
            }
            return user;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Rewrites the XML file by creating new elements and appending them to their specific parent nodes.
     * @param document is the source document being referenced.
     * @param root contains the root node of the NodeList
     */
    public static void rewriteXMLFile(Document document, Element root){
        for (User value : user) {
            Element newUser = document.createElement("USER");
            createNewUserElement(newUser, document, value.getUsername(), value.getPassword());
            root.appendChild(newUser);
        }
    }

    /**
     * Creates a loop that compares if a specified name is equal to a name on the XML File.
     * @param name is the user's username
     * @param list String<User> List
     * @return true if the user was found.
     */
    private static boolean searchUser(String name, ArrayList<User> list){
        for (User current : list){
            if (current.getUsername().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
