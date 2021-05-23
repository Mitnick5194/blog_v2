package com.ajie.exception;

public class VerifyException extends RuntimeException {
    public VerifyException() {
        super();
    }

    public VerifyException(Throwable e) {
        super(e);
    }
}
