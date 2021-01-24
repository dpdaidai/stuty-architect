package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *   此示例证明三种加锁方式, 仅有 reentrantLock.lockInterruptibly 是响应中断的
 *          1  当线程1想要持有 synchronized 的锁, 而锁被线程2占有时 , 线程1会持续等待, 直到持有锁. 对线程1调用iterrupted方法, 不会生效
 *          2  当线程1想要持有 reentrantLock.lock 的锁, 而锁被线程2占有时 , 效果同synchronized一致, 对线程1调用iterrupted方法, 不会生效
 *          3  当线程1想要持有 reentrantLock.lockInterruptibly 的锁, 而锁被线程2占有时 , 对线程1调用iterrupted方法, 会抛出InterruptedException, 业务对之进行相印得处理即可
 *          4  相关示例转载自 https://blog.csdn.net/qq379854836/article/details/72742205 , 也可在T22_InterruptibleTest查看
 *
 * @Author chenpantao
 * @Date 1/24/21 12:13 PM
 * @Version 1.0
 */
public class T22_InterruptibleTest {
    static ReentrantLock lock = new ReentrantLock();

    static class interruptibleThread implements Runnable {
        ReentrantLock lock;

        public interruptibleThread(ReentrantLock lock) {
            this.lock = lock;
        }

        @Override
        public void run() {
            try {
                System.out.println(Thread.currentThread().getName() + " is trying lock");

                //可响应中断的锁
                lock.lockInterruptibly();

                //不可响应中断的锁
//                lock.lock();

                System.out.println(Thread.currentThread().getName() + " is over");
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + " is interrupted");
                e.printStackTrace();
            } finally {
                System.out.println(Thread.currentThread().getName() + " is holding the lock :  " + lock.isHeldByCurrentThread());
                if (lock.isHeldByCurrentThread()) lock.unlock();
            }
        }
    }

    public static void main(String[] args) {
        interruptibleThread interruptibleThread = new interruptibleThread(lock);
        Thread t1 = new Thread(interruptibleThread, "t1");
        try {
            //线程main持有了锁
            lock.lock();
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

        } catch (InterruptedException e) {
            System.out.println(Thread.currentThread().getName() + " is interrupted");
            e.printStackTrace();
        } finally {
            System.out.println(Thread.currentThread().getName() + " is holding the lock :  " + lock.isHeldByCurrentThread());
            lock.unlock();
        }

        // 使用 lock.lockInterruptibly() 输出结果 : t1被成功中断
//        main is running
//        t1 is trying lock
//        t1 is interrupted
//        java.lang.InterruptedException
//        t1 is holding the lock :  false
//        main is over
//        main is holding the lock :  true


        // 使用lock.lock()的输出结果 :  t1未响应中断申请, 直到main线程结束, 不再持有锁, t1才获得锁, 并执行下去
//        main is running
//        t1 is trying lock
//        main is over
//        main is holding the lock :  true
//        t1 is over
//        t1 is holding the lock :  true

    }
}
