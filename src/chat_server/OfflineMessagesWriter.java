package chat_server;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class OfflineMessagesWriter {
    public void fileWriter(String sender, String receiver, ArrayList<String> list) {
        try {
            DocumentBuilderFactory documentFactory = DocumentBuilderFactory.newInstance();

            DocumentBuilder documentBuilder = documentFactory.newDocumentBuilder();

            Document document = documentBuilder.newDocument();
            // root element
            Element roots = document.createElement("offline_messages");
            document.appendChild(roots);

            // recipient
            Element recipientName = document.createElement("receiver");
            recipientName.setAttribute("receiver",receiver);
            roots.appendChild(recipientName);


            Element senderName = document.createElement("sender");
            senderName.setAttribute("sender", sender);
            recipientName.appendChild(senderName);

            for (int i = 0; i < list.size(); i++) {
                Element offlineMessage = document.createElement("message");
                offlineMessage.appendChild(document.createTextNode(String.valueOf(list.get(i))));
                senderName.appendChild(offlineMessage);
            }


            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("res/XMLFormatter.xslt")));
            DOMSource domSource = new DOMSource(document);
            StreamResult streamResult = new StreamResult(new File("res/OfflineMessages.xml"));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.transform(domSource, streamResult);

        } catch (ParserConfigurationException | TransformerException pce) {
            pce.printStackTrace();
        }
    }
}