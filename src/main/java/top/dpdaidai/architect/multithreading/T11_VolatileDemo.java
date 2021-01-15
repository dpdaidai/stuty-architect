package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * volatile 关键字示例
 * A B线程都用到一个变量, java默认是A线程中保留一份copy, 这样如果B线程修改了变量, 则A线程未必知道
 * 使用volatile关键字, 会让所有线程都读到变量的值
 * 详细来讲 :
 * 1  线程B修改volatile变量的值时, 不仅仅修改自身copy的值, 还会及时修改主内存的值
 * 2  修改主内存值后, 再使各个共享volatile变量的缓存失效
 * 3  线程A需要使用volatile变量时, 发现缓存失效, 再去主内存读取变量值
 *
 * 下面的示例中 :
 * 1  running是存在于堆内存的t11_volatileDemo对象中,
 * 2  当线程t1开始运行时, 会把running值从内存中读到t1线程的工作区, 运行过程中直接使用copy
 * 3  线程t1不会每次都去读取堆内存, 当主线程修改了running的值后, t1线程没有感知, 所以不会停止运行
 * 4  使用volatile修饰running变量, 将会强制所有线程都去堆内存读取running的值
 *
 * PS : 这里马士兵老师的说法是每次使用volatile变量都会去堆内存中读取 , 而T10示例的博主说的是volatile变量会使其余cpu核心的缓存失效 ,
 *      具体是哪一个 , 不清楚 .
 *
 * <p>
 * 可以阅读这篇文章进行更深入的理解
 * http://www.cnblogs.com/nexiyi/p/java_memory_model_and_thread.html
 * <p>
 * volatile并不能保证多个线程共同修改running变量时所带来的不一致问题，也就是说volatile不能替代synchronized
 *
 * @Author chenpantao
 * @Date 1/15/21 8:32 PM
 * @Version 1.0
 */
public class T11_VolatileDemo {

    volatile
    boolean running = true;

    void method() {
        System.out.println("method start");

        long start = System.currentTimeMillis();
        while (running) {
        }
        long end = System.currentTimeMillis();
        System.out.println("time consume : " + (end - start));
        System.out.println("method end!");
    }

    public static void main(String[] args) {

        T11_VolatileDemo t11_volatileDemo = new T11_VolatileDemo();
        new Thread(t11_volatileDemo::method, "t1").start();

        try {
            TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        t11_volatileDemo.running = false;


    }
}
