package com.aps4sem.chatserver;

public class ServerMain {

    private static final int PORT = 9090;

    public static void main(String[] args)
    {
        Server server = new Server(PORT);
        System.out.println("O servidor está rodando...");
        
        new Thread(server).start();
    }
}
