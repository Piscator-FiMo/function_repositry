package com.github.piscator.exceptions;

public class ConstructorNotFound extends RuntimeException{
    
    public ConstructorNotFound(String message) {
        super(message);
    }

}
