package top.dpdaidai.architect.multithreading;

import java.util.concurrent.Exchanger;

/**
 *
 * 线程间数据交换
 * 当一个线程调用exchange方法时 , 进入阻塞状态 , 等到另外个线程也调用该方法时再一起执行
 *
 * @Author chenpantao
 * @Date 2/25/21 4:27 PM
 * @Version 1.0
 */
public class T31_TestExchanger {

    static Exchanger<String> exchanger = new Exchanger<>();

    public static void main(String[] args) {
        new Thread(()->{
            String s = "T1";
            try {
                Thread.sleep(2000);
                s = exchanger.exchange(s);
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t1").start();


        new Thread(()->{
            String s = "T2";
            try {
                s = exchanger.exchange(s);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println(Thread.currentThread().getName() + " " + s);

        }, "t2").start();


    }

}
