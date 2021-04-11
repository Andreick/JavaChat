package com.aps4sem.chatserver.exceptions;

import com.aps4sem.chatlibrary.enums.ClientRequest;

public class UnknownClientRequest extends RuntimeException {

    public UnknownClientRequest(ClientRequest clientRequest) {
        super(clientRequest.toString());
    }
}
