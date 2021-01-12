package top.dpdaidai.architect.multithreading;

/**
 *
 * 一个同步方法可以调用另外一个同步方法 ,
 * 一个线程已经拥有某个对象的锁,
 * 再次申请时 仍然会得到该对象的锁 .
 *
 * 表明synchronized获得的锁时可重入的
 *
 * @Author chenpantao
 * @Date 1/12/21 10:50 PM
 * @Version 1.0
 */
public class T06_RepeatableEnter {
    public synchronized void m1(){
        System.out.println(Thread.currentThread().getName() +
                ": m1 start");
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        m2();
        System.out.println(Thread.currentThread().getName() +
                ": m1 end");

    }

    public synchronized void m2(){
        System.out.println(Thread.currentThread().getName() +
                ": m2 start");
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() +
                ": m2 end");

    }

    public static void main(String[] args) {
        new T06_RepeatableEnter().m1();
    }

}
