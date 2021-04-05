package com.aps4sem.chatserver;

import com.aps4sem.chatlibrary.ClientRequests;
import com.aps4sem.chatlibrary.SocketObjectStreams;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientHandler implements Runnable {

    private final Socket clientSocket;
    private final SocketObjectStreams streams;

    public ClientHandler(Socket clientSocket) throws IOException
    {
        this.clientSocket = clientSocket;
        streams = new SocketObjectStreams(clientSocket);
    }

    @Override
    public void run()
    {
        ClientRequests requests;
        
        try (clientSocket) {
            while ((requests = (ClientRequests) streams.receive()) != ClientRequests.QUIT)
            {
                streams.send("Ok!");
            }
            
            streams.send("Encerrando a conexão!");
        }
        catch (IOException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
