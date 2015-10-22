package com.mediator.helpers;

/**
 * Created by luispablo on 22/10/15.
 */
public class MediatorException extends RuntimeException {

    public MediatorException(String message) {
        super(message);
    }

    public MediatorException(Exception cause) {
        super(cause);
    }

    public MediatorException(Exception cause, String message) {
        super(message, cause);
    }
}
