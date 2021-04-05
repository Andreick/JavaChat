package com.aps4sem.chatserver;

import com.aps4sem.chatlibrary.*;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final SocketObjectStreams streams;
    
    private String user = null;

    public ClientHandler(Socket clientSocket) throws IOException
    {
        this.clientSocket = clientSocket;
        streams = new SocketObjectStreams(clientSocket);
    }

    @Override
    public void run()
    {
        ClientRequest requests;
        
        try {
            
            while (!clientSocket.isClosed())
            {
                requests = (ClientRequest) streams.receive();
                        
                switch (requests)
                {
                    case LOGIN:
                        handleLogin((String[]) streams.receive());
                        break;
                    case QUIT:
                        clientSocket.close();
                        break;
                }
            }
            
            System.out.println("[ClientHandler] Conexão com o usuário " + user + " encerrada.");
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

            if ((user.equals("guest") && password.equals("guest")) || (user.equals("admin") && password.equals("admin")))
            {
                streams.send(ServerResponse.LOGIN_ALLOWED);
                System.out.println("[ClientHandler] Usuário " + user + " - conectado com sucesso!");
                return;
            }
        }
            
        streams.send(ServerResponse.LOGIN_DENIED);
        System.out.println("[ClientHandler] Usuário " + user + " - acesso negado.");
        clientSocket.close();
    }
}
