package org.semul.budny.helper;

public class Wait {
    // Returns the time in milliseconds.
    public static synchronized int getCorrectTime(int second) {
        float maxRandomDispersion = 1F;
        int random = 1000 + (int) (Math.random() * 1000F * maxRandomDispersion);

        return random * second;
    }

    /*
     maxRandomDispersion - determines the maximum spread.
     For example, with a value of 0.5, the maximum number of seconds can increase up to 50%
     of the original value of seconds. Returns the time in milliseconds.
     */
    public static synchronized int getCorrectTime(int second, float maxRandomDispersion) {
        int random = 1000 + (int) (Math.random() * 1000F * maxRandomDispersion);

        return random * second;
    }

}
