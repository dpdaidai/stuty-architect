package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 *
 * 锁的优化
 *
 * 1   减少需要加锁的范围
 * synchronized优化
 * 同步代码块中的语句越少越好
 * 比较m1和m2
 *
 * 2    当业务中有很多细粒度的锁导致锁的争用非常频繁 , 可以将锁变粗
 *
 * @Author chenpantao
 * @Date 1/20/21 9:25 PM
 * @Version 1.0
 */
public class T14_FineCoarseLock {

    int count = 0;

    synchronized void m1() {
        //do sth need not sync
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        //业务逻辑中只有下面这句需要sync，这时不应该给整个方法上锁
        count ++;

        //do sth need not sync
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    void m2() {
        //do sth need not sync
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //业务逻辑中只有下面这句需要sync，这时不应该给整个方法上锁
        //采用细粒度的锁，可以使线程争用时间变短，从而提高效率
        synchronized(this) {
            count ++;
        }
        //do sth need not sync
        try {
            TimeUnit.SECONDS.sleep(2);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



}
