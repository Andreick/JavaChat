package com.aps4sem.chatserver;

import java.io.IOException;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerMain {

    private static final int PORT = 9090;

    public static void main(String[] args)
    {
        Server server = new Server(PORT);
        
        
    }
}
