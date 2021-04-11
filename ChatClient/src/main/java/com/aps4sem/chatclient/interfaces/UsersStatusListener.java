package com.aps4sem.chatclient.interfaces;

public interface UsersStatusListener
{
    void userOnline(String name);
    void userOffline(String name);
}
