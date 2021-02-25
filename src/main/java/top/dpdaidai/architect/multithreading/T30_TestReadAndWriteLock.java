package top.dpdaidai.architect.multithreading;

import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * 共享锁和排他锁
 *
 * @Author chenpantao
 * @Date 2/24/21 10:04 PM
 * @Version 1.0
 */
public class T30_TestReadAndWriteLock {

    static Lock reentrantLock = new ReentrantLock();
    private static int value;

    static ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
    static Lock readLock = readWriteLock.readLock();
    static Lock writeLock = readWriteLock.writeLock();

    public static void read(Lock lock) {
        try {
            lock.lock();
            TimeUnit.MILLISECONDS.sleep(50);
            System.out.println(Thread.currentThread().getName() + " begin read : value = " + value);
            //模拟读取操作
            TimeUnit.MILLISECONDS.sleep(1000);
            System.out.println(Thread.currentThread().getName() + " read over, value = " + value);
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }

    }

    public static void write(Lock lock, int v) {
        try {
            lock.lock();
            TimeUnit.MILLISECONDS.sleep(50);
            System.out.println(Thread.currentThread().getName() + " begin write : value = " + v);
            //模拟写入操作
            TimeUnit.MILLISECONDS.sleep(2000);
            value = v;
            System.out.println(Thread.currentThread().getName() + " write over, value = " + value);
            TimeUnit.MILLISECONDS.sleep(50);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        // 1 使用ReentrantLock进行锁定, read方法也会被锁定, 各线程只能依次读取或者数据
//        Runnable readThread = () -> read(reentrantLock);
//
//        Runnable writeThread = () -> write(reentrantLock, new Random().nextInt(100));
//        for (int i = 0; i < 20; i++) {
//            if (i % 5 == 0) {
//                new Thread(writeThread, "thread " + i).start();
//            } else {
//                new Thread(readThread, "thread " + i).start();
//            }
//        }
//        thread 1 begin read : value = 0
//        thread 1 read over, value = 0
//        thread 2 begin read : value = 0
//        thread 2 read over, value = 0
//        thread 3 begin read : value = 0
//        thread 3 read over, value = 0
//        thread 4 begin read : value = 0
//        thread 4 read over, value = 0
//        thread 5 begin write : value = 56
//        thread 5 write over, value = 56
//        thread 6 begin read : value = 56
//        thread 6 read over, value = 56
//        thread 7 begin read : value = 56
//        thread 7 read over, value = 56
//        thread 0 begin write : value = 95
//        thread 0 write over, value = 95
//        thread 8 begin read : value = 95
//        thread 8 read over, value = 95
//        thread 9 begin read : value = 95
//        thread 9 read over, value = 95
//        thread 10 begin write : value = 91
//        thread 10 write over, value = 91
//        thread 11 begin read : value = 91
//        thread 11 read over, value = 91
//        thread 12 begin read : value = 91
//        thread 12 read over, value = 91
//        thread 13 begin read : value = 91
//        thread 13 read over, value = 91
//        thread 14 begin read : value = 91
//        thread 14 read over, value = 91
//        thread 15 begin write : value = 80
//        thread 15 write over, value = 80
//        thread 16 begin read : value = 80
//        thread 16 read over, value = 80
//        thread 17 begin read : value = 80
//        thread 17 read over, value = 80
//        thread 18 begin read : value = 80
//        thread 18 read over, value = 80
//        thread 19 begin read : value = 80
//        thread 19 read over, value = 80

        // 2 使用readWriteLock, 只读时使用共享锁, 写入时使用排他锁.
        Runnable readThread = () -> read(readLock);

        Runnable writeThread = () -> write(writeLock, new Random().nextInt(100));
        for (int i = 0; i < 20; i++) {
            if (i % 5 == 0) {
                new Thread(writeThread, "thread " + i).start();
            } else {
                new Thread(readThread, "thread " + i).start();
            }
        }
//        thread 2 begin read : value = 0
//        thread 1 begin read : value = 0
//        thread 3 begin read : value = 0
//        thread 2 read over, value = 0
//        thread 1 read over, value = 0
//        thread 3 read over, value = 0
//        thread 0 begin write : value = 48
//        thread 0 write over, value = 48
//        thread 5 begin write : value = 75
//        thread 5 write over, value = 75
//        thread 4 begin read : value = 75
//        thread 7 begin read : value = 75
//        thread 8 begin read : value = 75
//        thread 9 begin read : value = 75
//        thread 6 begin read : value = 75
//        thread 7 read over, value = 75
//        thread 6 read over, value = 75
//        thread 9 read over, value = 75
//        thread 4 read over, value = 75
//        thread 8 read over, value = 75
//        thread 10 begin write : value = 49
//        thread 10 write over, value = 49
//        thread 12 begin read : value = 49
//        thread 13 begin read : value = 49
//        thread 14 begin read : value = 49
//        thread 11 begin read : value = 49
//        thread 12 read over, value = 49
//        thread 13 read over, value = 49
//        thread 14 read over, value = 49
//        thread 11 read over, value = 49
//        thread 15 begin write : value = 18
//        thread 15 write over, value = 18
//        thread 17 begin read : value = 18
//        thread 19 begin read : value = 18
//        thread 18 begin read : value = 18
//        thread 16 begin read : value = 18
//        thread 19 read over, value = 18
//        thread 18 read over, value = 18
//        thread 17 read over, value = 18
//        thread 16 read over, value = 18

    }


}
