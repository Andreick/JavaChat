package com.aps4sem.chatlibrary.io;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketOutputStream
{
    private final ObjectOutputStream out;

    public SocketOutputStream(Socket socket) throws IOException
    {
        out = new ObjectOutputStream(socket.getOutputStream());
    }
    
    public void send(Object data) throws IOException
    {
        out.writeObject(data);
        out.flush();
    }
}