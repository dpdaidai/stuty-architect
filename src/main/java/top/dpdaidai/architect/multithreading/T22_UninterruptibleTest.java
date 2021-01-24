package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 *   此示例证明线程在等待synchronized锁的时候 , 不会响应interrupted申请
 *
 * @Author chenpantao
 * @Date 1/24/21 12:13 PM
 * @Version 1.0
 */
public class T22_UninterruptibleTest {

    static class UninterruptibleThread implements Runnable {

        @Override
        public void run() {
            System.out.println(Thread.currentThread().getName() + " is trying lock");

            synchronized (this) {
                System.out.println(Thread.currentThread().getName() + " is over");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
        UninterruptibleThread uninterruptibleThread = new UninterruptibleThread();
        Thread t1 = new Thread(uninterruptibleThread, "t1");
        //线程main持有了锁
        synchronized (uninterruptibleThread) {
            //此时线程t1申请锁, 会处于等待状态
            t1.start();
            System.out.println(Thread.currentThread().getName() + " is running");
            TimeUnit.SECONDS.sleep(2);

            //睡眠两秒后, 线程t1申请中断
            //1  如果t1加锁方式如果时synchronized或者lock() , 则不会响应该请求, 直到t1获得了锁, 此时可构成死锁情况
            //2  如果t1加锁方式是reentrantLock.lockInterruptibly(), 则可以响应中断请求, 并抛出iterruptedException异常
            t1.interrupt();

            //等待两秒后, 结束线程main, 并查看是否仍然由main持有锁
            TimeUnit.SECONDS.sleep(2);
            System.out.println(Thread.currentThread().getName() + " is over");
        }

//        t1 is trying lock
//        main is running
//        main is over
//        t1 is over


    }
}
