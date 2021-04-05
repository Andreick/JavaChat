package com.aps4sem.chatclient;

import com.aps4sem.chatlibrary.ClientRequests;
import com.aps4sem.chatlibrary.SocketObjectStreams;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ClientMain {
    
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    
    public static void main(String[] args)
    {
        try (Socket serverSocket = new Socket(SERVER_IP, SERVER_PORT)) {
            
            SocketObjectStreams streams = new SocketObjectStreams(serverSocket);
            
            System.out.println("Sending request");
            
            streams.send(ClientRequests.QUIT);
            
            System.out.println("Receiving response");
            
            String response = (String) streams.receive();
            
            JOptionPane.showMessageDialog(null, response);
        }
        catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassNotFoundException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
        catch (ClassCastException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}