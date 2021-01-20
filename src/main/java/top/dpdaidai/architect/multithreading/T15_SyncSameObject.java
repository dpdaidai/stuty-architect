package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * 锁定对象o时, 如果o的属性发生改变, 不影响锁的使用
 * 但是o如果被赋值为别的对象, 则 被锁的对象发生改变, 原对象不再有锁.
 * 所以应该避免加锁的对象被赋值为别的引用, 建议添加final修饰
 *
 * @Author chenpantao
 * @Date 1/20/21 9:31 PM
 * @Version 1.0
 */
public class T15_SyncSameObject {
//    final
    Object o = new Object();

    void m() {
        synchronized (o) {
            while (true) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName());


            }
        }
    }

    public static void main(String[] args) {
        T15_SyncSameObject t = new T15_SyncSameObject();
        //启动第一个线程
        new Thread(t::m, "t1").start();

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //创建第二个线程
        Thread t2 = new Thread(t::m, "t2");

//        t.o = new Object(); //锁对象发生改变，所以t2线程得以执行，如果注释掉这句话，线程2将永远得不到执行机会

        t2.start();


    }

}
