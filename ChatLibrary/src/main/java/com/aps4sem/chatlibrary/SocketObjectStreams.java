package com.aps4sem.chatlibrary;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class SocketObjectStreams
{
    private final ObjectOutputStream out;
    private final ObjectInputStream in;

    public SocketObjectStreams(Socket socket) throws IOException
    {
        out = new ObjectOutputStream(socket.getOutputStream());
        in = new ObjectInputStream(socket.getInputStream());
    }
    
    public void send(Object data) throws IOException
    {
        out.writeObject(data);
        out.flush();
    }
    
    public Object receive() throws IOException, ClassNotFoundException
    {
        Object data = in.readObject();
        return data;
    }
}