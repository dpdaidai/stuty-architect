package top.dpdaidai.architect.multithreading;

/**
 * 本例子表明volatile不等于加锁, volatile仅增加了变量的可见性
 * <p>
 * volatile 关键字 , 使一个变量在多个线程间可见
 * 1  保证线程可见性
 * 2  禁止指令重排序
 *
 * @Author chenpantao
 * @Date 1/15/21 5:28 PM
 * @Version 1.0
 */
public class T12_Volatile {
    public volatile int inc = 0;



    //synchronized可以保证数据正确
    public
//    synchronized
    void increase() {
        inc++;
    }

    public static void main(String[] args) {
        final T12_Volatile t12_volatile = new T12_Volatile();
        for (int i = 0; i < 10; i++) {
            new Thread() {
                public void run() {
                    for (int j = 0; j < 1000; j++)
                        t12_volatile.increase();
                }

                ;
            }.start();
        }

        while (Thread.activeCount() > 1) {    //保证前面的线程都执行完 , 需要在debug模式下才有效

            Thread.currentThread().getThreadGroup().list();
            Thread.yield();
        }
        System.out.println(t12_volatile.inc);
    }
}
