package com.aps4sem.chatserver;

import com.aps4sem.chatlibrary.*;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final List<ClientHandler> clientHandlers = new ArrayList<>();
    
    private final Server server;
    private final Socket clientSocket;
    
    private String user = null;

    public ClientHandler(Server server, Socket clientSocket)
    {
        this.server = server;
        this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
        try {
            ClientRequest requests;
            
            do {
                requests = (ClientRequest) SocketStream.receive(clientSocket);
                System.out.println("[ClientHandler] Requisição recebida.");
                        
                switch (requests)
                {
                    case LOGIN:
                        handleLogin((String[]) SocketStream.receive(clientSocket));
                        break;
                }
            } while (requests != ClientRequest.LOGOUT);
            
            handleLogout();
        }
        catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void handleLogin(String[] tokens) throws IOException
    {
        if (tokens != null && tokens.length == 2)
        {
            user = tokens[0];
            String password = tokens[1];

            if ((user.equals("guest") && password.equals("guest")) || 
                    (user.equals("admin") && password.equals("admin")) ||
                    (user.equals("user") && password.equals("user")) ||
                    (user.equals("random") && password.equals("random")))
            {
                SocketStream.send(clientSocket, ServerMessage.LOGIN_ALLOWED);
                System.out.println("[ClientHandler] Usuário " + user + " conectado com sucesso!");
                
                for (ClientHandler clientHandler : clientHandlers)
                {
                    // envia ao usuário atual os outros usuários online
                    SocketStream.send(this.clientSocket, ServerMessage.USER_LOGON);
                    SocketStream.send(this.clientSocket, clientHandler.user);
                    
                    // envia aos outros usuários online o usuário atual
                    SocketStream.send(clientHandler.clientSocket, ServerMessage.USER_LOGON);
                    SocketStream.send(clientHandler.clientSocket, this.user);
                }
                
                clientHandlers.add(this);
                
                return;
            }
        }
        
        SocketStream.send(clientSocket, ServerMessage.LOGIN_DENIED);
        System.out.println("[ClientHandler] Usuário " + user + " - acesso negado.");
        clientSocket.close();
    }

    private void handleLogout() throws IOException
    {
        clientHandlers.remove(this);
        
        for (ClientHandler clientHandler : clientHandlers)
        {
            // envia aos outros usuários online que o usuário atual saiu
            SocketStream.send(clientHandler.clientSocket, ServerMessage.USER_LOGOFF);
            SocketStream.send(clientHandler.clientSocket, this.user);
        }
        
        System.out.println("[ClientHandler] Conexão com o usuário " + user + " encerrada.");
    }
}
