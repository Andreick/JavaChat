package com.aps4sem.chatclient.exceptions;

import com.aps4sem.chatlibrary.enums.ServerMessage;

public class UnknownServerMessage extends RuntimeException {

    public UnknownServerMessage(ServerMessage serverMessage) {
        super(serverMessage.toString());
    }
}
