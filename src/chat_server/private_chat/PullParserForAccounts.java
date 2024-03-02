package chat_server.private_chat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class PullParserForAccounts {
    static NodeList nodeListAllUsers;
    private String username;
    private String password;


    /**
     * The readXML method under PullParserForAccounts class 
     * reads the Accounts.xml under the res directory, retrieves 
     * all the usernames and passwords inside the XML file, and 
     * stores it in an ArrayList, which will be the returned data 
     * of this method.  
     * 
     * @return listOfAllUsers
     */
    public ArrayList<String> readXML() {
        try {
            File file = new File("res/Accounts.xml");
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();


            nodeListAllUsers = document.getElementsByTagName("USER");
            ArrayList<String> listOfAllUsers = new ArrayList<>();
            for (int i = 0; i < nodeListAllUsers.getLength(); i++) {
                Node node = nodeListAllUsers.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;

                    String data = element.getTextContent();
                    data = data.replaceAll(" {2}", "");
                    data = data.replaceFirst("\n", "");
                    data = data.replaceFirst("\n", ",");

                    String[] split = data.split(",");
                    username = split[0];
                    password = split[1];

                    listOfAllUsers.add(username);
                }
            }
            return listOfAllUsers;
        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }
}

