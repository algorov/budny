package org.semul.budny.exception;

public class StartSessionException extends Exception {
    @Override
    public String getMessage() {
        return "~ {INVALID DATA} or incorrectly solved captcha!";
    }
}
