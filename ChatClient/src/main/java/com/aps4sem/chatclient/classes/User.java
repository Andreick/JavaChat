package com.aps4sem.chatclient.classes;

import com.aps4sem.chatclient.exceptions.LoginAlreadyInUse;
import com.aps4sem.chatclient.exceptions.LoginDenied;
import com.aps4sem.chatclient.exceptions.UnknownServerMessage;
import com.aps4sem.chatlibrary.enums.ClientRequest;
import com.aps4sem.chatlibrary.enums.ServerMessage;
import com.aps4sem.chatlibrary.io.SocketInputStream;
import com.aps4sem.chatlibrary.io.SocketOutputStream;
import java.io.IOException;
import java.net.Socket;
import com.aps4sem.chatclient.interfaces.UsersStatusListener;
import java.net.ConnectException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class User {
    
    private Socket clientSocket;
    private SocketOutputStream out;
    private SocketInputStream in;
    
    public User(String serverIP, int serverPort, String userName, String password) throws LoginDenied, LoginAlreadyInUse, ConnectException
    {
        try {
            clientSocket = new Socket(serverIP, serverPort);
            System.out.println("Porta do cliente: " + clientSocket.getLocalPort());
            
            out = new SocketOutputStream(clientSocket);
            in = new SocketInputStream(clientSocket);
            
            out.send(ClientRequest.LOGIN);
            out.send(new String[] {userName, password});
            
            ServerMessage response = (ServerMessage) in.receive();
            
            if (response == ServerMessage.LOGIN_DENIED) throw new LoginDenied("Usuário ou senha inválidos");
            if (response == ServerMessage.LOGIN_IN_USE) throw new LoginAlreadyInUse();
            if (response != ServerMessage.LOGIN_ALLOWED) throw new UnknownServerMessage(response);
        }
        catch (ConnectException ex) {
            throw ex;
        }
        catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            closeSocket();
        }
    }
    
    private void closeSocket()
    {
        try {
            clientSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, "Erro ao fechar o soquete", ex);
        }
    }

    public void listenToServer(UsersStatusListener serverListener)
    {
        try {
            ServerMessage serverMessage;

            do {
                System.out.println("Aguardando mensagem do servidor...");
                serverMessage = (ServerMessage) in.receive();
                System.out.println("Mensagem recebida");

                switch (serverMessage)
                {
                    case USER_ONLINE:
                        serverListener.userOnline((String) in.receive());
                        break;
                    case USER_OFFLINE:
                        serverListener.userOffline((String) in.receive());
                        break;
                    case END_OF_CONNECTION:
                        closeSocket();
                        break;
                    default:
                        throw new UnknownServerMessage(serverMessage);
                }
            } while (!clientSocket.isClosed());
        }
        catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            closeSocket();
        }
    }
    
    public void logout()
    {
        try {
            out.send(ClientRequest.LOGOUT);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            closeSocket();
        }
    }
}
