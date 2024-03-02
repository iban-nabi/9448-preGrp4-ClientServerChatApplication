package chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerHandlerGUI {
    ServerSocket serverSocket;
    Socket socket;

    public ServerHandlerGUI(){
    }

    boolean turnOffServer = false;
    public void runServer() {
        try{
            serverSocket = new ServerSocket(6900);
            while(true){
                socket = serverSocket.accept();
                ChatServerHandlerGUI chatServerHandler = new ChatServerHandlerGUI(socket);
                chatServerHandler.start();
                if (turnOffServer) {
                    socket.close();
                    break;
                }
            }
        }catch(Exception e){
            System.exit(0);
            e.printStackTrace();
        }finally {
            if(serverSocket !=null){
                try{
                    serverSocket.close();
                }catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }

    public void closeServerSocket() throws IOException {
        serverSocket.close();
        socket.close();

    }
}
