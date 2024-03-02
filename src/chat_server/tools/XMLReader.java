package chat_server.tools;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XMLReader {

    /**
     * The method processes the located xml file into a document object
     * @param fileName to be processed
     * @return document of the processed xml file
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    public static Document xmlReader(String fileName) throws ParserConfigurationException, IOException, SAXException {
        File xmlFile = new File(fileName);
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        return builder.parse(xmlFile);
    }
}
