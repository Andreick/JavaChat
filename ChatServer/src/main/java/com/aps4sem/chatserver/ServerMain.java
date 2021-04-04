package com.aps4sem.chatserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    private static final int PORT = 9090;

    public static void main(String[] args) {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);

            System.out.println("[SERVIDOR] Esperando conexão com o cliente...");
            Socket clientSocket = serverSocket.accept();
            System.out.println("[SERVIDOR] Conectado ao cliente!");

            ClientHandler clientHandler = new ClientHandler(clientSocket);
            Thread thread = new Thread(clientHandler);
            
            thread.start();

            try {
                thread.join();
            }
            catch (InterruptedException ex) {
                Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            clientSocket.close();
            serverSocket.close();
        }
        catch (IOException ex) {
            Logger.getLogger(ServerMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}