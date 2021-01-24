package top.dpdaidai.architect.multithreading;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * 构造一个死锁的例子，然后用中断来处理死锁
 * lock的中断示例, 转载自 https://www.jianshu.com/p/155260c8af6c
 *
 * @Author chenpantao
 * @Date 1/24/21 12:35 AM
 * @Version 1.0
 */
public class T23_LockInterrupt extends Thread {

    public static ReentrantLock lock1 = new ReentrantLock();
    public static ReentrantLock lock2 = new ReentrantLock();

    int lock;

    public T23_LockInterrupt(int lock, String name) {

        super(name);
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            if (lock == 1) {
                lock1.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                lock2.lockInterruptibly();
            } else {
                lock2.lockInterruptibly();
                try {
                    Thread.sleep(500);
                } catch (Exception e) {
                    // TODO: handle exception
                    e.printStackTrace();
                }
                lock1.lockInterruptibly();
            }
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        } finally {
            if (lock1.isHeldByCurrentThread()) {
                lock1.unlock();
            }
            if (lock2.isHeldByCurrentThread()) {
                lock2.unlock();
            }
            System.out.println(Thread.currentThread().getId() + ":线程退出");
        }
    }

    public static void main(String[] args) throws InterruptedException {
        T23_LockInterrupt t1 = new T23_LockInterrupt(1, "thread 1");
        T23_LockInterrupt t2 = new T23_LockInterrupt(2, "thread 2");
        t1.start();
        t2.start();
        Thread.sleep(1000);

        DeadlockChecker.check();
    }

    static class DeadlockChecker {
        private final static ThreadMXBean mbean = ManagementFactory
                .getThreadMXBean();

        public static void check() {

            Thread tt = new Thread(() -> {
                {
                    // TODO Auto-generated method stub
                    while (true) {
                        long[] deadlockedThreadIds = mbean.findDeadlockedThreads();
                        if (deadlockedThreadIds != null) {
                            ThreadInfo[] threadInfos = mbean.getThreadInfo(deadlockedThreadIds);
                            for (Thread t : Thread.getAllStackTraces().keySet()) {
                                for (int i = 0; i < threadInfos.length; i++) {
                                    if (t.getId() == threadInfos[i].getThreadId()) {
                                        System.out.println(t.getName());
                                        t.interrupt();
                                    }
                                }
                            }
                        }
                        try {
                            Thread.sleep(5000);
                        } catch (Exception e) {
                            // TODO: handle exception
                        }
                    }

                }
            });
            tt.setDaemon(true);
            tt.start();
        }

    }

}
