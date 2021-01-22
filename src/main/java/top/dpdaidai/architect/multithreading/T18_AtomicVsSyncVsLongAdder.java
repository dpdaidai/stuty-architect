package top.dpdaidai.architect.multithreading;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

/**
 *
 * LongAdder
 * 它是一个线程安全的、比Atomic*系工具性能更好的"计数器"。
 * LongAdder中会维护一个或多个变量，这些变量共同组成一个long型的“和”。当多个线程同时更新（特指“add”）值时，
 * 为了减少竞争，可能会动态地增加这组变量的数量。
 *
 * 在求和场景下比AtomicLong更高效的原因 (https://www.jianshu.com/p/22d38d5c8c2a)
 *      1  首先和AtomicLong一样，都会先采用cas方式更新值
 *      2  在初次cas方式失败的情况下(通常证明多个线程同时想更新这个值)，尝试将这个值分隔成多个cell（sum的时候求和就好），
 *         让这些竞争的线程只管更新自己所属的cell（因为在rehash之前，每个线程中存储的hashcode不会变，
 *         所以每次都应该会找到同一个cell），这样就将竞争压力分散了
 *      3  LongAdder适合的场景是统计求和计数的场景，而且LongAdder基本只提供了add方法，而AtomicLong还具有cas方法
 *
 * @Author chenpantao
 * @Date 1/22/21 9:19 PM
 * @Version 1.0
 */
public class T18_AtomicVsSyncVsLongAdder {

    static AtomicLong count1 = new AtomicLong(0L);
    static long count2 = 0L;
    static LongAdder count3 = new LongAdder();

    static int threadNumber = 1000;

    //测试多线程下AtomicLong , synchronized , LongAdder 做累加时的效率
//    AtomicLong : 100000000 , time : 2627
//    synchronized : 100000000 , time : 2854
//    LongAdder : 100000000 , time : 322
    //LongAdder 在多线程下 , 做统计求和比AtomicLong 更高效
    public static void main(String[] args) throws InterruptedException {
        //============= 测试AtomicLong ===============
        Thread[] threads = new Thread[threadNumber];
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    count1.incrementAndGet();
                }
            });
        }

        long start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        long end = System.currentTimeMillis();
        System.out.println("AtomicLong : " + count1 + " , time : " + (end - start));

        System.out.println("==============================");
        //============= 测试synchronized ===============

        Object lock = new Object();

        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    synchronized (lock) {
                        count2++;
                    }
                }
            });
        }

        start = System.currentTimeMillis();
        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        end = System.currentTimeMillis();
        System.out.println("synchronized : " + count2 + " , time : " + (end - start));

        System.out.println("==============================");

        //=============== 测试LongAdder ============
        for (int i = 0; i < threads.length; i++) {
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100000; j++) {
                    count3.increment();
                }
            });

        }

        start = System.currentTimeMillis();

        for (Thread thread : threads) {
            thread.start();
        }
        for (Thread thread : threads) {
            thread.join();
        }
        end = System.currentTimeMillis();
        System.out.println("LongAdder : " + count3 + " , time : " + (end - start));

    }


}
