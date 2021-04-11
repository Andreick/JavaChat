package com.aps4sem.chatlibrary.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketStream
{
    /*public static void send(Socket socket, Object data) throws IOException
    {
        ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
        out.writeObject(data);
        out.flush();
    }
    
    public static Object receive(Socket socket) throws IOException, ClassNotFoundException
    {
        ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
        return in.readObject();
    }*/
}