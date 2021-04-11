package com.aps4sem.chatclient.exceptions;

public class LoginDenied extends Exception {

    public LoginDenied() {
    }
    
    public LoginDenied(String msg) {
        super(msg);
    }
}
