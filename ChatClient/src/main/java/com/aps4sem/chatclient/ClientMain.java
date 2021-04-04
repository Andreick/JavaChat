package com.aps4sem.chatclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class ClientMain
{
    private static final String SERVER_IP = "127.0.0.1";
    private static final int SERVER_PORT = 9090;
    
    public static void main(String[] args) {
        try {
            Socket serverSocket = new Socket(SERVER_IP, SERVER_PORT);
            
            BufferedReader in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            
            out.println(" > ");
            
            System.out.println("Receiving response");
            String serverResponse = in.readLine();
            
            JOptionPane.showMessageDialog(null, serverResponse);
            
            out.println("quit");
            
            System.out.println("Receiving response");
            serverResponse = in.readLine();
            
            JOptionPane.showMessageDialog(null, serverResponse);
            
            serverSocket.close();
        } catch (IOException ex) {
            Logger.getLogger(ClientMain.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}