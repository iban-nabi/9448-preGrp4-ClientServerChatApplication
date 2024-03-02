package chat_server.group_chat;

import chat_server.tools.WriteToDOMtoFile;
import chat_server.tools.XMLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.net.Socket;

public class CreateChatRoom {
    Socket socket;
    String name;
    private final BufferedReader streamRdr;
    private final BufferedWriter streamWtr;

    /**
     * Constructor for the CreateChatRoom. Setups the current user, the stream reader and writer.
     * @param socket socket to allow connection
     * @param name name of the current user
     * @throws IOException
     */
    public CreateChatRoom(Socket socket, String name) throws IOException {
        this.socket = socket;
        this.name = name;
        streamRdr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        streamWtr = new BufferedWriter(new PrintWriter(socket.getOutputStream(),true));
    }

    /**
     * This method will mainly run the process of creating a new group chat by calling methods within this class as well
     * as the classes which handel XML processing.
     * @throws ParserConfigurationException throws ParserConfigurationException
     * @throws IOException throws IOException
     * @throws SAXException throws SAXException
     */
    public void runCreateChatRoom() throws ParserConfigurationException, IOException, SAXException {
        String grpName = streamRdr.readLine();
        if(!grpName.equals(" ")){
            String fileName = "res/GroupChats.xml";
            Document document = XMLReader.xmlReader(fileName);
            Document newDocument = writeNewGroupChat(document,grpName);
            WriteToDOMtoFile.writeDOMtoFile(newDocument,fileName);
            streamWtr.write("Group Chat Created, Add members within the Chat Server");
            streamWtr.newLine();
            streamWtr.flush();
        }
        else{
            streamWtr.write("No Group Chat Created");
            streamWtr.newLine();
            streamWtr.flush();
        }
    }

    /**
     * Extracts the root of the document and calls the createNewGroupChat method to append the new group chat
     * @param document currently processed xml file
     * @param grpName the name of the group chat to be created
     * @return document
     */
    private Document writeNewGroupChat(Document document,String grpName){
        Element root = document.getDocumentElement();
        createNewGroupChat(document,root,grpName);
        return document;
    }

    /**
     * Appends the data need for the group chat
     * @param doc currently processed xml file
     * @param root root of the XML file
     * @param grpName the name of the group chat to be created
     */
    private void createNewGroupChat(Document doc, Element root, String grpName) {
        Element grp = doc.createElement("GRP");
        grp.setAttribute("name",grpName);

        Element admin = doc.createElement("ADMIN");
        Text txtAdmin = doc.createTextNode(name);
        admin.appendChild(txtAdmin);
        grp.appendChild(admin);
        root.appendChild(grp);
    }
}
