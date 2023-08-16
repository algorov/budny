package org.semul.budny.event;

public interface Eventable {
    public void run() throws Exception;

    public void quit();
}
