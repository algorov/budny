package org.semul.budny.account;

import org.semul.budny.connection.Session;
import org.semul.budny.exception.FailEmployException;
import org.semul.budny.exception.StartSessionException;
import org.semul.budny.manager.Manager;

import java.util.LinkedList;
import java.util.Queue;

public class Account extends Thread {
    private Manager manager;
    private final String username;
    private final String password;
    private volatile AccountInfo info;
    private volatile boolean status;
    private volatile boolean completionStatus;
    private Session session;
    private final Queue<Intention> taskQueue;

    public enum Intention {
        DISABLE, GET_INFO, EMPLOY
    }

    public Account(Manager manager, String username, String password) {
        System.out.println(">>> [Account] Init...");
        this.manager = manager;
        this.username = username;
        this.password = password;
        this.status = false;
        this.taskQueue = new LinkedList<>();
        this.completionStatus = false;
        this.session = null;
        System.out.println(">>> [Account] Done.");
    }

    @Override
    public void run() {
        launch();

        while (this.status) {
            if (this.taskQueue.size() > 0) {
                switch (this.taskQueue.poll()) {
                    case DISABLE -> disable();
                    case GET_INFO -> requestForInfo();
                    case EMPLOY -> employ();
                }
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addTask(Intention intention) {
        System.out.println(">>> [Account] Adding a task " + intention);
        this.taskQueue.add(intention);
        System.out.println(">>> [Account] Done.");
    }

    // *** Intentions ***
    private void launch() {
        System.out.println(">>> [Account] Launch...");
        this.session = new Session(this.username, this.password);

        try {
            this.session.start();
            this.status = true;
            System.out.println(">>> [Account] Successfully.");
        } catch (StartSessionException e) {
            System.out.println(">>> [Account] Fail.");
            System.out.println(e.getMessage());
            disable();
        } finally {
            this.completionStatus = true;
        }
    }

    private void disable() {
        System.out.println(">>> [Account] Disable...");
        if (this.session != null) {
            this.session.interrupt();
            this.session = null;
        }

        this.status = false;
        this.completionStatus = true;

        System.out.println(">>> [Account] Successfully.");
    }

    private void requestForInfo() {
        System.out.println(">>> [Account] Request for information...");
        this.info = this.session.getAccountInfo();
        this.completionStatus = true;

        System.out.println(">>> [Account] Successfully.");
    }

    private void employ() {
        try {
            this.session.employ();
            this.manager.createWave(this, Intention.EMPLOY, 60 * 60);
        } catch (FailEmployException e) {
            System.out.println(e.getMessage());
            this.manager.createWave(this, Intention.EMPLOY, 0);
        }
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public AccountInfo getInfo() {
        System.out.println(">>> [Account] Get info.");
        return this.info;
    }

    public boolean getStatus() {
        System.out.println(">>> [Account] Get status.");
        return this.status;
    }

    public boolean getCompletionStatus() {
        System.out.println(">>> [Account] Get completion status.");
        return this.completionStatus;
    }

    public void changeCompletionStatus() {
        System.out.println(">>> [Account] Change completion status.");
        this.completionStatus = !this.completionStatus;
    }

    @Override
    public String toString() {
        return "\n● Account:\n▬▬ username: " + this.username + ";\n" + "▬▬ password: " + this.password + ";\n"
                + "▬▬ status: " + (this.status ? "launched" : "stopped") + ";\n";
    }
}
