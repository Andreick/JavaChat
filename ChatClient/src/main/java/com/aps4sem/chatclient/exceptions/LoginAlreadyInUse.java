package com.aps4sem.chatclient.exceptions;

public class LoginAlreadyInUse extends Exception {

    public LoginAlreadyInUse() {
    }

    public LoginAlreadyInUse(String msg) {
        super(msg);
    }
}
