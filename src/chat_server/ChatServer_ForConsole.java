package chat_server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class ChatServer_ForConsole {
    public static void main(String[] args) throws IOException {
        Scanner kbd = new Scanner(System.in);

        ServerSocket serverSocket = null;
        int decision = 0;
        while (decision != 3) {
            System.out.println("""
                Server Tools:
                [1] Start Server
                [2] Manage Users
                [3] EXIT""");
            System.out.print("Enter choice: ");
            decision = kbd.nextInt();
            switch (decision) {
                case 1 -> {
                    System.out.println("Server is running...");
                    try{
                        serverSocket = new ServerSocket(4900);
                        while(!serverSocket.isClosed()){
                            Socket socket = serverSocket.accept();
                            ChatServerHandler_ForConsole chatServerHandler = new ChatServerHandler_ForConsole(socket);
                            chatServerHandler.start();
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }finally {
                        if(serverSocket !=null){
                            try{
                                serverSocket.close();
                            }catch (IOException e){
                                e.printStackTrace();
                            }
                        }
                    }} //start server
                case 2 -> {
                    UserManager userManager = new UserManager();
                    userManager.run();
                }
                case 3 -> {}
            }
        }
    }
}
