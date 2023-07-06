package org.semul.budny.account;

public record AccountInfo(int employmentCountdown) {
    @Override
    public String toString() {
        return "Employment countdown: " + this.employmentCountdown + ".";
    }
}
