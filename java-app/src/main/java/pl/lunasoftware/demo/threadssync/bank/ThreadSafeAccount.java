package pl.lunasoftware.demo.threadssync.bank;

import java.util.concurrent.locks.ReentrantLock;

public class ThreadSafeAccount extends Account {
    public final ReentrantLock lock;

    public ThreadSafeAccount(Account account) {
        super(account.getAccountNumber(), account.getBalance());
        this.lock = new ReentrantLock();
    }
}
