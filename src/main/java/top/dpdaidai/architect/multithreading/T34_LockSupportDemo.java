package top.dpdaidai.architect.multithreading;

import java.util.concurrent.locks.LockSupport;

/**
 *
 * 转载 https://www.jianshu.com/p/f1f2cd289205
 *
 *   park和unpark会对每个线程维持一个许可（boolean值）
 *
 *   unpark调用时，如果当前线程还未进入park，则许可为true
 *   park调用时，判断许可是否为true，如果是true，则继续往下执行；如果是false，则等待，直到许可为true
 *
 * @Author chenpantao
 * @Date 2/25/21 10:53 PM
 * @Version 1.0
 */
public class T34_LockSupportDemo {

    public static Object u = new Object();
    static ChangeObjectThread t1 = new ChangeObjectThread("t1");
    static ChangeObjectThread t2 = new ChangeObjectThread("t2");

    public static class ChangeObjectThread extends Thread {
        public ChangeObjectThread(String name) {
            super(name);
        }

        @Override
        public void run() {
            synchronized (u) {
                System.out.println(Thread.currentThread().getName() + " running");
                LockSupport.park();
                if (Thread.currentThread().isInterrupted()) {
                    System.out.println(Thread.currentThread().getName() + "被中断了");
                }
                System.out.println(Thread.currentThread().getName() + "继续执行");
            }
        }
    }

    public static void main(String[] args) throws InterruptedException {
//        这儿park和unpark其实实现了wait和notify的功能，不过还是有一些差别的。
//
//        park不需要获取某个对象的锁
//        因为中断的时候park不会抛出InterruptedException异常，所以需要在park之后自行判断中断状态，然后做额外的处理
        t1.start();
        Thread.sleep(1000L);
        t2.start();
        Thread.sleep(3000L);

        // t1处于park状态 , 被中断后 , 能继续执行 , 中断的时候park不会抛出InterruptedException异常，所以需要在park之后自行判断中断状态，然后做额外的处理
        t1.interrupt();

        // 解除t2的park状态
        LockSupport.unpark(t2);
        t1.join();
        t2.join();

    }


}
