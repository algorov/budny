package org.semul.budny.controller;

public interface Controller<T> {
    public void run();

    public boolean isLive();

    public void close();
}
