package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

/**
 *
 *   ReentrantLock 可配置为公平锁 , 将等待锁的线程放入队列, 先入先出
 *
 * @Author chenpantao
 * @Date 2/1/21 8:39 PM
 * @Version 1.0
 */
public class T24_FairLock implements Runnable {

    private ReentrantLock fairLock = new ReentrantLock();

    @Override
    public void run() {

        for (int i = 0; i < 100; i++) {
            fairLock.lock();
            try {
                System.out.println(Thread.currentThread().getName() + "获得锁");
                TimeUnit.MILLISECONDS.sleep(50);  //给打印控制台留出时间
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                fairLock.unlock();
            }
        }


    }

    public static void main(String[] args) {
        T24_FairLock fairLock = new T24_FairLock();
        Thread t1 = new Thread(fairLock, "t1");
        Thread t2 = new Thread(fairLock, "t2");
        Thread t3 = new Thread(fairLock, "t3");
        Thread t4 = new Thread(fairLock, "t4");
        t1.start();
        t2.start();
        t3.start();
        t4.start();

        //测试结果
//        当lock为公平锁时, 输出结果 :
//        t1, t2, t3, t4轮流获得锁

//        当lock为非公平锁时,
//        多次执行, 无法看出明显得线程执行顺序得规律
//        可以看到刚解锁得线程更容易获得锁, 但不绝对

    }


}
