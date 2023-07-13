package org.semul.budny.manager;

import org.semul.budny.account.Account;
import org.semul.budny.helper.Countdown;

import java.util.Objects;

public class Wave extends Thread {
    public static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(Wave.class);
    public static volatile int countWave = 0;
    private Manager manager;
    private Account account;
    private Account.Intention intent;
    private int countdown;

    public static Wave getInstance(Manager manager, Account account, Account.Intention intent, int countdown) {
        return new Wave(manager, account, intent, countdown);
    }

    private Wave(Manager manager, Account account, Account.Intention intent, int countdown) {
        logger.info("Initialization...");

        this.manager = manager;
        this.account = account;
        this.intent = intent;
        this.countdown = countdown;

        logger.info("Done.");
    }

    @Override
    public void run() {
        this.countdown = Countdown.getCorrectTime(this.countdown, 0.05F);
        logger.info("Signal to the manager about '" + intent + "' in " + this.countdown / 1000 + " seconds.");
        countWave++;

        try {
            Thread.sleep(this.countdown);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        if (account.getStatus()) {
            logger.info("Signal to the manager about '" + intent + '.');

            if (Objects.requireNonNull(intent) == Account.Intention.EMPLOY) {
                manager.getJob(account);
            }
        }

        countWave--;
        clear();
    }

    private void clear() {
        logger.info("Interrupt wave...");

        this.manager = null;
        this.account = null;
        this.intent = null;

        logger.info("Done.");
    }
}
