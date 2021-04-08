package com.aps4sem.chatserver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Server implements Runnable {

    private final int serverPort;
    
    //private List<ClientHandler> clientHandlers = new ArrayList<>();

    public Server(int serverPort) {
        this.serverPort = serverPort;
    }

    /*public List<ClientHandler> getClientHandlers() {
        return clientHandlers;
    }*/

    @Override
    public void run() {
        try {
            ServerSocket serverSocket = new ServerSocket(serverPort);

            while (!serverSocket.isClosed())
            {
                Socket clientSocket = serverSocket.accept();
                System.out.println("[SERVIDOR] Novo usuário conectado.");

                ClientHandler clientHandler = new ClientHandler(this, clientSocket);
                //clientHandlers.add(clientHandler);
                
                new Thread(clientHandler).start();
            }
        }
        catch (BindException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
