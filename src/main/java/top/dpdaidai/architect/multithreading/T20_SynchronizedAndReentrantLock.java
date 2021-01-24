package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 本例子m1和m2证明synchronized是可重入锁
 * 在 方法1 里面调用方法2 , 2可以被成功调用 , 证明synchronized是可重入的
 *
 * m3和m4证明ReentrantLock同样拥有锁的功能
 * 同样是可重入锁, synchronized遇到异常时, jvm会自动释放锁,
 * 但是lock必须手动释放锁, 因此经常在finally中进行锁的释放
 *
 * @Author chenpantao
 * @Date 1/23/21 9:40 PM
 * @Version 1.0
 */
public class T20_SynchronizedAndReentrantLock {

    Lock lock = new ReentrantLock();

    synchronized void m1() {
        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(i);
            if (i == 5) {
                m2();  // 验证锁的可重入性
            }
        }
    }

    synchronized void m2() {
        System.out.println("m2 running");
    }

    void m3() {

        try {
            lock.lock();
            for (int i = 0; i < 10; i++) {
                TimeUnit.SECONDS.sleep(1);
                System.out.println(i);

                if (i == 5) {
                    m4();  // 验证锁的可重入性
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    void m4() {
        try {
            lock.lock();
            System.out.println("m4 running");
        } finally {
            lock.unlock();
        }
    }


    public static void main(String[] args) throws InterruptedException {
        T20_SynchronizedAndReentrantLock t20_synchronized = new T20_SynchronizedAndReentrantLock();
        Thread t1 = new Thread(t20_synchronized::m1);
        t1.start();
        TimeUnit.SECONDS.sleep(1);
        Thread t2 = new Thread(t20_synchronized::m2);
        t2.start();

        t1.join();
        t2.join();

        System.out.println("======synchronized test finished======");

        Thread t3 = new Thread(t20_synchronized::m3);
        t3.start();
        TimeUnit.SECONDS.sleep(1);
        Thread t4 = new Thread(t20_synchronized::m4);
        t4.start();

        t3.join();
        t4.join();

        System.out.println("======ReentrakLock test finished======");
    }

}
