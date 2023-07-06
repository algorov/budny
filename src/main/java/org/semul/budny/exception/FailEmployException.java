package org.semul.budny.exception;

public class FailEmployException extends Exception {
    public FailEmployException(String message) {
        super(message);
        ExeptionCount.count++;
    }
}
