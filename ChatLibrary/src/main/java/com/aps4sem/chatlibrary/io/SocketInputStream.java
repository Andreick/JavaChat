package com.aps4sem.chatlibrary.io;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;

public class SocketInputStream
{
    private final ObjectInputStream in;

    public SocketInputStream(Socket socket) throws IOException
    {
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public Object receive() throws IOException, ClassNotFoundException
    {
        return in.readObject();
    }
}