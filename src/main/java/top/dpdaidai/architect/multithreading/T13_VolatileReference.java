package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * volatile 引用类型(包含数组)只能保证引用本身的可见性, 不能保证引用对象内部字段的可见性
 *
 * @Author chenpantao
 * @Date 1/15/21 8:53 PM
 * @Version 1.0
 */
public class T13_VolatileReference {

    boolean running = true;

    volatile static T13_VolatileReference t13_volatileReference = new T13_VolatileReference();

    void m() {
        System.out.println("m start");
        while (running) {

//			try {
//				TimeUnit.MILLISECONDS.sleep(10);
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}

        }
        System.out.println("m end!");
    }

    public static void main(String[] args) {
        new Thread(t13_volatileReference::m, "t1").start();

        //lambda表达式 new Thread(new Runnable( run() {m()}

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t13_volatileReference.running = false;


    }


}
