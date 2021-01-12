package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * 在程序执行过程中, 如果出现异常, 默认情况锁会被释放.
 * 所以在并发处理的过程中, 有异常要多加小心 , 不然可能会发生不一致的情况.
 * 比如 , 在一个web app处理过程中, 多个servlet线程共同访问同一资源,
 * 这时如果异常处理不合适 , 在第一个线程抛出异常后, 其它线程就会进入同步打码区,
 * 有可能会访问到发生异常后未能恢复的数据
 *
 * 因此要小心处理同步业务逻辑中的异常
 *
 * @Author chenpantao
 * @Date 1/12/21 11:27 PM
 * @Version 1.0
 */
public class T08_CatchException {

    int count = 0;

    public synchronized void exceptionMethod() {

        System.out.println(Thread.currentThread().getName() + " start");
        while (true) {
            count++;
            System.out.println(Thread.currentThread().getName() + " count = " + count);

            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (count % 5 == 0) {
                try {
                    // 除0异常 , 如果没有catch, 那么锁会被释放 , 数据也就发生了异常
                    int i = 1 / 0;
                } catch (ArithmeticException e) {
                    e.printStackTrace();
                    count = 4;
                    System.out.println(Thread.currentThread().getName() +
                            " catch exception , count rollback , count = " + count);
                    break;
                }
            }
        }

    }

    public static void main(String[] args) {
        T08_CatchException t08_catchException = new T08_CatchException();

        new Thread(() -> t08_catchException.exceptionMethod()).start();
        new Thread(() -> t08_catchException.exceptionMethod()).start();
        new Thread(() -> t08_catchException.exceptionMethod()).start();

    }
}
