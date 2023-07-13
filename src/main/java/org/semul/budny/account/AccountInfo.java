package org.semul.budny.account;

public record AccountInfo(int workEndCountdown) {
    @Override
    public String toString() {
        return "\n▬▬ Work end countdown: " + this.workEndCountdown + " sec;\n";
    }
}
