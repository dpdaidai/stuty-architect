package top.dpdaidai.architect.multithreading;

/**
 *
 * @Author chenpantao
 * @Date 1/11/21 8:29 PM
 * @Version 1.0
 */
public class T04_SynchronizedMethod {

    public synchronized void synchronizedMethod(){
        System.out.println(Thread.currentThread().getName() +
                ": synchronized method start");
        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() +
                ": synchronized method end");

    }

    public void normalMethod(){
        System.out.println(Thread.currentThread().getName() +
                ": normal method start");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(Thread.currentThread().getName() +
                ": normal method end");

    }

    public static void main(String[] args) {

        /**
         * 验证在执行同步方法时 , 别的线程还能否执行一个对象的非同步方法
         */

        T04_SynchronizedMethod t04_synchronizedMethod = new T04_SynchronizedMethod();

        new Thread(t04_synchronizedMethod::synchronizedMethod,"t1").start();
        new Thread(t04_synchronizedMethod::normalMethod,"t2").start();

    }


}
