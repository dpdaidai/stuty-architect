package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.LockSupport;

/**
 *
 * https://www.jianshu.com/p/f1f2cd289205
 * LockSupport是一个线程阻塞工具类，所有的方法都是静态方法，可以让线程在任意位置阻塞，当然阻塞之后肯定得有唤醒的方法。
 *
 *
 * @Author chenpantao
 * @Date 2/25/21 5:03 PM
 * @Version 1.0
 */
public class T32_TestLockSupport {

    public static void main(String[] args) {
        Thread t = new Thread(()->{
            for (int i = 0; i < 10; i++) {
                System.out.println(i);
                if(i == 5) {
                    LockSupport.park();
                }
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        t.start();

//        LockSupport.unpark(t);

        try {
            TimeUnit.SECONDS.sleep(8);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("after 8 senconds!");
        LockSupport.unpark(t);

    }


}
