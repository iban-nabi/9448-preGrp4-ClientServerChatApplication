package chat_server.private_chat;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
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
import java.io.*;

public class DeleteSeenMessages {
    static NodeList nodeList;
    static Node node;

    /**
     * The deleteMessages method is created under the DeleteSeenMessages class and will be 
     * invoked to delete all the seen offline messages. The deleteMessages reads the XML file 
     * OfflineMessages.xml under the res directory and deletes all the child nodes of the 
     * parent node. Once all have been deleted, the Transformer object and used in the DOM to 
     * construct a source object.
     * 
     * @throws TransformerException
     * @throws ParserConfigurationException
     * @throws IOException
     * @throws SAXException
     */
    public void deleteMessages() throws TransformerException, ParserConfigurationException, IOException, SAXException {
        File file = new File("res/OfflineMessages.xml");
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
        Document document = documentBuilder.parse(file);
        document.getDocumentElement().normalize();

        nodeList = document.getElementsByTagName("receiver");

        for (int i = 0; i < nodeList.getLength(); i++) {
            node = nodeList.item(i);

            Node parent = node.getParentNode();
            if (parent != null) {
                parent.removeChild(node);
            }
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("res/XMLFormatter.xslt")));
        DOMSource domSource = new DOMSource(document);
        StreamResult streamResult = new StreamResult(new File("res/OfflineMessages.xml"));
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        transformer.transform(domSource, streamResult);
    }
}






