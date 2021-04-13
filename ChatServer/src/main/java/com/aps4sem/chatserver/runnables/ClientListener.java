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

public class ClientListener implements Runnable {

    private static final Map<String, ClientListener> clients = new ConcurrentHashMap<>();
    
    private final Socket clientConnection;
    
    private SocketOutputStream out;
    private SocketInputStream in;
    private String userName;
    
    public ClientListener(Socket clientSocket) {
        this.clientConnection = clientSocket;
    }
    
    private void consolePrintln(String msg) {
        System.out.println("{" + Thread.currentThread().getName() + "} " + msg);
    }
    
    private void closedConnectionMessage(Exception ex) {
        Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName() + " Conexão fechada de forma inesperada", ex);
    }
    
    private void userSocketClosedMessage(SocketException ex, String usrName) {
        Logger.getLogger(ClientListener.class.getName()).log(Level.WARNING, Thread.currentThread().getName() + " Usuário " + usrName + " offline", ex);
    }

    @Override
    public void run() {
        try {
            out = new SocketOutputStream(clientConnection);
            in = new SocketInputStream(clientConnection);
            
            if (login())
            {
                try {
                    ClientRequest request;
                    do {
                        consolePrintln("Aguardando requisição...");
                        request = (ClientRequest) in.receive();
                        consolePrintln("Requisição " + request.toString() + " recebida");

                        switch (request)
                        {
                            case CHAT:
                                chat();
                                break;
                            case LOGOUT:
                                out.send(ServerMessage.END_OF_CONNECTION);
                                break;
                            default:
                                throw new UnknownClientRequest(request);
                        }
                    } while(request != ClientRequest.LOGOUT);
                }
                catch (EOFException | SocketException ex) {
                    closedConnectionMessage(ex);
                }
                catch (IOException | ClassNotFoundException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName(), ex);
                }
                finally {
                    logout();
                }
            }
        }
        catch (EOFException | SocketException ex) {
            closedConnectionMessage(ex);
        }
        catch (IOException | ClassNotFoundException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName(), ex);
        }
        finally {
            closeSocket();
        }
    }
    
    private void closeSocket()
    {
        try {
            clientConnection.close();
            consolePrintln("Conexão encerrada: " + clientConnection.getRemoteSocketAddress());
        }
        catch (IOException ex) {
            Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName() + " Erro ao fechar o soquete", ex);
        }
    }

    // formato do array: {"usuário", "senha"}
    private synchronized boolean login() throws IOException, ClassNotFoundException
    {
        consolePrintln("Aguardando login do cliente...");
        ClientRequest request = (ClientRequest) in.receive();

        if (request != ClientRequest.LOGIN)
        {
            throw new UnknownClientRequest(request);
        }

        consolePrintln("Login recebido");

        String[] tokens = (String[]) in.receive();

        if (tokens == null || tokens.length != 2)
        {
            consolePrintln("Argumentos enviados inválidos.");
            out.send(ServerMessage.LOGIN_DENIED);
            return false;
        }

        userName = tokens[0];
        String password = tokens[1];

        if (clients.containsKey(userName))
        {
            consolePrintln("Usuário " + userName + " já está conectado.");
            out.send(ServerMessage.LOGIN_IN_USE);
            return false;
        }

        if ((userName.equals("guest") && password.equals("guest")) ||
                (userName.equals("admin") && password.equals("admin")) ||
                (userName.equals("user") && password.equals("user")) ||
                (userName.equals("root") && password.equals("root")))
        {
            consolePrintln("Login autorizado.");
            out.send(ServerMessage.LOGIN_ALLOWED);
            Thread.currentThread().setName(userName + "-Listener");

            for (ClientListener clientListener : clients.values())
            {
                try {
                    // envia aos outros usuários online o usuário atual
                    clientListener.out.send(ServerMessage.USER_ONLINE);
                    clientListener.out.send(this.userName);
                    
                    // envia ao usuário atual os outros usuários online
                    this.out.send(ServerMessage.USER_ONLINE);
                    this.out.send(clientListener.userName);
                }
                catch (SocketException ex) {
                    userSocketClosedMessage(ex, clientListener.userName);
                }
                catch (IOException ex) {
                    Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName(), ex);
                }
            }

            consolePrintln("Usuário conectado com sucesso!");
            clients.put(userName, this);
            return true;
        }

        consolePrintln("Login negado.");
        out.send(ServerMessage.LOGIN_DENIED);
        return false;
    }

    private void logout()
    {
        clients.remove(userName);
        consolePrintln("Usuário desconectado.");

        for (ClientListener clientListener : clients.values())
        {
            try {
                // envia aos outros usuários online que o usuário atual saiu
                clientListener.out.send(ServerMessage.USER_OFFLINE);
                clientListener.out.send(this.userName);
            }
            catch (SocketException ex) {
                userSocketClosedMessage(ex, clientListener.userName);
            }
            catch (IOException ex) {
                Logger.getLogger(ClientListener.class.getName()).log(Level.SEVERE, Thread.currentThread().getName(), ex);
            }
        }
    }

    // formato do array: {"usuário", "mensagem"}
    private void chat() throws IOException, ClassNotFoundException
    {
        String[] tokens = (String[]) in.receive();
        
        if (tokens != null && tokens.length == 2)
        {
            String sendTo = tokens[0];
            String message = tokens[1];
        }
    }
}
