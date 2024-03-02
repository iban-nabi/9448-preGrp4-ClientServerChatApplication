package chat_server.tools;

import org.w3c.dom.Node;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;

public class WriteToDOMtoFile {
    
    /**
     * This method formats and converts the node in to a xml file.
     * @param node of the document
     * @param pathName where the file will be saved
     */
    public static void writeDOMtoFile(Node node, String pathName){
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer(new StreamSource(new File("res/XMLFormatter.xslt")));
            DOMSource source = new DOMSource(node);

            StreamResult outputFile = new StreamResult(new File(pathName));
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION,"yes");
            transformer.transform(source, outputFile);
        } catch (Exception e){
            e.printStackTrace();
        }
    }
}
