package com.aps4sem.chatserver.runnables;

import com.aps4sem.chatlibrary.enums.ClientRequest;
import com.aps4sem.chatlibrary.enums.ServerMessage;
import com.aps4sem.chatlibrary.io.SocketInputStream;
import com.aps4sem.chatlibrary.io.SocketOutputStream;
import com.aps4sem.chatserver.exceptions.UnknownClientRequest;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private static final Map<String, ClientHandler> clients = new ConcurrentHashMap<>();
    
    private final Socket clientSocket;
    
    private SocketOutputStream out;
    private SocketInputStream in;
    private String userName;

    public ClientHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run()
    {
        try {
            out = new SocketOutputStream(clientSocket);
            in = new SocketInputStream(clientSocket);
            ClientRequest request;
            
            do {
                request = (ClientRequest) in.receive();
                System.out.println("[ClientHandler] Requisição recebida.");
                        
                switch (request)
                {
                    case LOGIN:
                        login();
                        break;
                    case LOGOUT:
                        out.send(ServerMessage.END_OF_CONNECTION);
                        logout();
                        break;
                    case CHAT:
                        chat();
                        break;
                    default:
                        throw new UnknownClientRequest(request);
                }
            } while (!clientSocket.isClosed());
        }
        catch (EOFException | SocketException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, "Soquete fechado de forma inesperada", ex);
        }
        catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally {
            if (!clientSocket.isClosed()) logout();
        }
    }
    
    private void closeSocket()
    {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, "Erro ao fechar o soquete", ex);
        }
    }

    // formato do array: {"usuário", "senha"}
    private void login() throws IOException, ClassNotFoundException
    {
        String[] tokens = (String[]) in.receive();
        
        if (tokens != null && tokens.length == 2)
        {
            userName = tokens[0];
            String password = tokens[1];
            
            if (clients.containsKey(userName))
            {
                System.out.println("[ClientHandler] Usuário " + userName + " já está conectado.");
                out.send(ServerMessage.LOGIN_IN_USE);
                closeSocket();
                return;
            }

            if ((userName.equals("guest") && password.equals("guest")) || 
                    (userName.equals("admin") && password.equals("admin")) ||
                    (userName.equals("user") && password.equals("user")) ||
                    (userName.equals("root") && password.equals("root")))
            {
                System.out.println("[ClientHandler] Usuário " + userName + " conectado com sucesso!");
                out.send(ServerMessage.LOGIN_ALLOWED);

                for (ClientHandler clientHandler : clients.values())
                {
                    // envia ao usuário atual os outros usuários online
                    this.out.send(ServerMessage.USER_ONLINE);
                    this.out.send(clientHandler.userName);

                    // envia aos outros usuários online o usuário atual
                    clientHandler.out.send(ServerMessage.USER_ONLINE);
                    clientHandler.out.send(this.userName);
                }

                clients.put(userName, this);
                return;
            }
        }
            
        System.out.println("[ClientHandler] Usuário " + userName + " - acesso negado.");
        out.send(ServerMessage.LOGIN_DENIED);
        closeSocket();
    }

    private void logout()
    {
        try {
            clients.remove(userName);

            for (ClientHandler clientHandler : clients.values())
            {
                // envia aos outros usuários online que o usuário atual saiu
                clientHandler.out.send(ServerMessage.USER_OFFLINE);
                clientHandler.out.send(this.userName);
            }

            System.out.println("[ClientHandler] Conexão com o usuário " + userName + " encerrada.");
            closeSocket();
        }
        catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
