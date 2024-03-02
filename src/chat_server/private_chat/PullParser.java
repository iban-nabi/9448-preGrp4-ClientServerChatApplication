package chat_server.private_chat;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;

import java.io.*;
import java.util.ArrayList;
import java.util.Objects;


public class PullParser {
    static NodeList nodeList;
    static Node node;

    static ArrayList<String> listOfMessages = new ArrayList<>();
    static ArrayList<String> listOfUsernames = new ArrayList<>();
    DeleteSeenMessages seenMessages = new DeleteSeenMessages();

    public ArrayList<String> getSenderList(String receiver){
        try {
            File file = new File("res/OfflineMessages.xml");
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();

            NodeList receiverList = document.getElementsByTagName("receiver");

            for (int i = 0; i < receiverList.getLength(); i++) {
                Node receiverNode = receiverList.item(i);

                if (receiverNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element receiverElement = (Element) receiverNode;
                    if (receiverElement.getAttribute("receiver").equals(receiver)) {
                        NodeList senderList = receiverElement.getElementsByTagName("sender");

                        ArrayList<String> listOfSenders = new ArrayList<>();
                        for (int n = 0; n < senderList.getLength(); n++) {
                            Element senderElement = (Element) senderList.item(n);
                            listOfSenders.add(senderElement.getAttribute("sender"));
                        }
                        return listOfSenders;
                    }
                }
            }

        } catch (ParserConfigurationException | IOException | SAXException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * The readXML method under PullParser class reads the OfflineMessages.xml 
     * under the res directory, retrieves the data, and stores it in an ArrayList, 
     * which will be the returned data of this method. The deleteMessages method 
     * is also invoked using the object DeleteSeenMessages to delete all the seen 
     * messages inside the XML file. 
     * 
     * @return listOfMessages
     */
    public ArrayList<String> readXML(String youAsReceiver, String sender) {
        listOfMessages.clear();
        try {
            File file = new File("res/OfflineMessages.xml");
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(file);
            document.getDocumentElement().normalize();

            nodeList = document.getElementsByTagName("receiver");

            NodeList messages = null;
            for (int i = 0; i < nodeList.getLength(); i++) {
                node = nodeList.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element receiverElement = (Element) node;
                    if (receiverElement.getAttribute("receiver").equals(youAsReceiver)) {
                        NodeList senderList = receiverElement.getElementsByTagName("sender");
                        for (int n = 0; n < senderList.getLength(); n++) {
                            Element senderElement = (Element) senderList.item(n);
                            if (senderElement.getAttribute("sender").equals(sender)) {
                                messages = ((Element) senderList.item(n)).getElementsByTagName("message");
                            }
                        }
                        for (int o = 0; o< Objects.requireNonNull(messages).getLength(); o++) {
                            listOfMessages.add(messages.item(o).getChildNodes().item(0).getTextContent());
                            System.out.println(listOfMessages);
                        }
                    }
                }
            }

            seenMessages.deleteMessages();

           return listOfMessages;
        } catch (ParserConfigurationException | IOException | SAXException | TransformerException e) {
            e.printStackTrace();
        }
        return null;

    }
}







