package chat_server;

import chat_server.tools.WriteToDOMtoFile;
import chat_server.tools.XMLBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.crypto.SecretKey;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import java.util.ArrayList;
import java.util.Scanner;

public class UserManager {
    static Scanner scan = new Scanner(System.in);
    static String fileName = "res/Accounts.xml";
    static ArrayList <User> user = new ArrayList<>();
    private static ArrayList <String> blockList = new ArrayList<>();
    static String uName;

    public void run() {
        Document document = createDocument();
        WriteToDOMtoFile.writeDOMtoFile(document, fileName);
    }

    Document createDocument(){
        Document document = null;
        try {
            document = XMLBuilder.xmlBuilder();
            Element root = document.createElement("Accounts");
            document.appendChild(root);
            int uChoice = 0;
            while(uChoice != 5){
                readFile();
                System.out.println("""
                        Select an option:
                        [1] Add New User
                        [2] Remove a User
                        [3] Block a User
                        [4] Unblock a User
                        [5] EXIT
                        """);
                System.out.print("Enter your choice: ");
                uChoice = Integer.parseInt(scan.nextLine());
                switch (uChoice) {
                    case 1 -> {
                        //rewriteXMLFile(document, root);
                        String password;
                        Element newUser = document.createElement("USER");
                        do {
                            System.out.print("Enter the username: ");
                            uName = scan.nextLine();
                            if (searchUser(uName, user)) {
                                System.out.println("This name is already existing!");
                            }
                        } while (searchUser(uName, user));
                        System.out.print("Enter the password: ");
                        SecretKey passKey = Encryption.generateKeyFromPassword(scan.nextLine());
                        password = Encryption.convertSecretKeyToString(passKey);
                        createNewUserElement(newUser, document, uName, password);
                        root.appendChild(newUser);
                        rewriteXMLFile(document, root);
                        clearList();
                        return document;
                    }
                    case 2 -> {
                        do {
                            System.out.println("Enter the name of the user that you want to delete: ");
                            uName = scan.nextLine();
                            if (!searchUser(uName, user)) {
                                System.out.println(uName + " is not on the list.");
                            }
                        } while (!searchUser(uName, user));
                        for(int i = 0; i < user.size(); i++){
                            if(user.get(i).getUsername().equals(uName)){
                                user.remove(i);
                                break;
                            }
                        }
                        rewriteXMLFile(document, root);
                        clearList();
                        return document;
                    }
                    case 3 -> {
                        do {
                            System.out.println("Name of the user you want to block: ");
                            uName = scan.nextLine();
                            if (!searchUser(uName, user)) {
                                System.out.println(uName + " is not on the list.");
                            } else if (searchBlockedUser(uName, blockList)) {
                                System.out.println(uName + " is already blocked.");
                            }
                        } while (!searchUser(uName, user) && !searchBlockedUser(uName, blockList));

                        rewriteXMLFile(document, root);
                        setStatusAsBlocked(document);

                        clearList();
                        return document;
                    }
                    case 4 -> {
                        do {
                            System.out.println("Name of the user you want to unblock: ");
                            uName = scan.nextLine();
                            if (!searchUser(uName, user)) {
                                System.out.println(uName + " is not on the list.");
                            } else if (!searchBlockedUser(uName, blockList)) {
                                System.out.println(uName + " is not blocked.");
                            }
                        } while (!searchUser(uName, user) && searchBlockedUser(uName, blockList));
                        removeStatusAsBlocked(document);
                        rewriteXMLFile(document, root);

                        clearList();
                        return document;
                    }
                }
            }

        } catch (Exception e){
            e.printStackTrace();
        }
        return document;
    }

    private void clearList(){
        user.clear();
        blockList.clear();
    }

    private void removeStatusAsBlocked(Document document){
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

    private void setStatusAsBlocked(Document document){
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

    private void createNewUserElement(Element account, Document doc, String username, String password) {
        Element userAccount = doc.createElement("NAME");
        Text txtData = doc.createTextNode(username);
        userAccount.appendChild(txtData);
        Element pass = doc.createElement("PASSWORD");
        txtData = doc.createTextNode(password);
        pass.appendChild(txtData);

        account.appendChild(userAccount);
        account.appendChild(pass);
    }

    public void readFile(){
        try{
            String username = "";
            String password = "";
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
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public void rewriteXMLFile(Document document, Element root){
        for (int i = 0; i < user.size(); i++){
            Element newUser = document.createElement("USER");
            createNewUserElement(newUser, document, user.get(i).getUsername(), user.get(i).getPassword());
            root.appendChild(newUser);
        }
    }

    private boolean searchBlockedUser(String name, ArrayList<String> list){
        for (String current : list){
            if (current.equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }

    private boolean searchUser(String name, ArrayList <User> list){
        for (User current : list){
            if (current.getUsername().equalsIgnoreCase(name)){
                return true;
            }
        }
        return false;
    }
}
