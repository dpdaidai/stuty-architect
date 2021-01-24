package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 * ReentrantLock 用于替代synchronized .
 * ReentrantLock 在功能上更加丰富, 它拥有如下几个特点
 *      1  可重入
 *      2  可中断 :
 *          1  当线程1想要持有 synchronized 的锁, 而锁被线程2占有时 , 线程1会持续等待, 直到持有锁. 对线程1调用iterrupted方法, 不会生效
 *          2  当线程1想要持有 reentrantLock.lock 的锁, 而锁被线程2占有时 , 效果同synchronized一致, 对线程1调用iterrupted方法, 不会生效
 *          3  当线程1想要持有 reentrantLock.lockInterruptibly 的锁, 而锁被线程2占有时 , 对线程1调用iterrupted方法, 会抛出InterruptedException, 业务对之进行相印得处理即可
 *          4  相关示例转载自 https://blog.csdn.net/qq379854836/article/details/72742205 , 也可在T22_InterruptibleTest查看
 *      3  可限时 : 设定多少时间无法等到锁, 则放弃
 *      4  公平锁 :
 *          + 一般意义上的锁是不公平的，不一定先来的线程能先得到锁，
 *            后来的线程就后得到锁。不公平的锁可能会产生饥饿现象。
 *          + 公平锁的意思就是，这个锁能保证线程是先来的先得到锁。
 *            虽然公平锁不会产生饥饿现象，但是公平锁的性能会比非公平锁差很多。
 *
 * @Author chenpantao
 * @Date 1/23/21 9:53 PM
 * @Version 1.0
 */
public class T21_ReentrantLock {

    ReentrantLock reentrantLock = new ReentrantLock();

    int i = 0;

    public static void main(String[] args) {
        // 1 可重入特点
        T21_ReentrantLock t21_reentrantLock = new T21_ReentrantLock();
        t21_reentrantLock.reentrant1();
//        1
//        2

        // 2 可限时特点
        TryLockTest thread1 = new TryLockTest("thread 1");
        TryLockTest thread2 = new TryLockTest("thread 2");
        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
//        thread 2 get lock failed
//        lock.isHeldByCurrentThread : thread 1

    }

    void reentrant1() {
        reentrantLock.lock();
        try {
            i++;
            System.out.println(i);
            reentrant2();
        } finally {
            reentrantLock.unlock();
        }

    }

    void reentrant2() {
        reentrantLock.lock();
        try {
            i++;
            System.out.println(i);
        } finally {
            reentrantLock.unlock();
        }
    }


}

class TryLockTest extends Thread {
    public static ReentrantLock reentrantLock = new ReentrantLock();


    public TryLockTest(String name) {
        super(name);
    }

    @Override
    public void run() {
        try {
            //线程1获得锁后, 睡眠6秒
            //线程2尝试获取该锁, 在尝试5秒后, 自动放弃, 并且在解锁时, 显示未能获得锁
            if (reentrantLock.tryLock(5, TimeUnit.SECONDS)) {
                Thread.sleep(6000);
            } else {
                System.out.println(Thread.currentThread().getName() + " get lock failed");
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            if (reentrantLock.isHeldByCurrentThread()) {
                System.out.println("lock.isHeldByCurrentThread : " + Thread.currentThread().getName());
                reentrantLock.unlock();
            }
        }


    }

}
