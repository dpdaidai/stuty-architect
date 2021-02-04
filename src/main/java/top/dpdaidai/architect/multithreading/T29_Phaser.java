package top.dpdaidai.architect.multithreading;

import java.util.concurrent.Phaser;
import java.util.concurrent.TimeUnit;

/**
 * phase(阶段)
 * 适用于一些需要分阶段的任务的处理。它的功能与 CyclicBarrier和CountDownLatch有些类似，类似于一个多阶段的栅栏，
 *  +   可以在初始时设定参与线程数
 *  +   当到达的参与者数量满足栅栏设定的数量后，会进行阶段升级
 *  +   而且它支持对任务的动态调整
 *  +   也可以中途注册/注销参与者
 *  +   并支持分层结构来达到更高的吞吐量
 *
 * 方法 :
 *      bulkRegister(int number) : 设置注册线程数量
 *      arriveAndAwaitAdvance() : 线程已到达, 并等待其余线程一起前进, 当前线程进入阻塞状态
 *      arriveAndDeregister() : 线程已到达, 并注销该线程的注册状态
 *      getRegisteredParities() : 获得注册的parties数量。
 *      register(): 每执行一次方法，就动态添加一个parties值。
 *      getArrivedParties(): 获得已经被使用的parties个数。
 *
 *
 * 使用场景 :
 *      1  通过Phaser控制多个线程的执行时机：有时候我们希望所有线程到达指定点后再同时开始执行
 *      2  通过Phaser实现开关。在以前讲CountDownLatch时，我们给出过以CountDownLatch实现开关的示例，也就是说，我们希望一些外部条件得到满足后，然后打开开关，线程才能继续执行
 *      3  通过Phaser控制任务的执行轮数
 *
 * https://www.pdai.tech/md/java/thread/java-thread-x-juc-tool-phaser.html
 * https://segmentfault.com/a/1190000015979879
 * https://blog.csdn.net/cold___play/article/details/106909344
 *
 * @Author chenpantao
 * @Date 2/4/21 11:57 PM
 * @Version 1.0
 */
public class T29_Phaser {

    static MarriagePhaser phaser = new MarriagePhaser();

    public static void main(String[] args) {

        phaser.bulkRegister(7);

        for (int i = 0; i < 5; i++) {

            new Thread(new Person("p" + i)).start();
        }

        new Thread(new Person("新郎")).start();
        new Thread(new Person("新娘")).start();

    }

    static void milliSleep(int milli) {
        try {
            TimeUnit.MILLISECONDS.sleep(milli);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static class MarriagePhaser extends Phaser {

        /**
         * 每当栅栏推倒时, 所有线程一同前进时,
         *    1  phase++
         *    2  执行对应phaser类中onAdvance方法
         * @param phase
         * @param registeredParties
         * @return  当返回值为true时, 终止Phaser
         */
        @Override
        protected boolean onAdvance(int phase, int registeredParties) {

            switch (phase) {
                case 0:
                    System.out.println("所有人到齐了！当前人数 : " + registeredParties);
                    System.out.println();
                    return false;
                case 1:
                    System.out.println("所有人吃完了！当前人数 : " + registeredParties);
                    System.out.println();
                    return false;
                case 2:
                    System.out.println("所有人离开了！当前人数 : " + registeredParties);
                    System.out.println();
                    return false;
                case 3:
                    System.out.println("婚礼结束！新郎新娘抱抱！当前人数 : " + registeredParties);
                    return true;
                default:
                    return true;
            }
        }

    }

    static class Person implements Runnable {
        String name;

        public Person(String name) {
            this.name = name;
        }

        public void arrive() {

            milliSleep(1000);
            System.out.printf("%s 到达现场！\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void eat() {
            milliSleep(1000);
            System.out.printf("%s 吃完!\n", name);
            phaser.arriveAndAwaitAdvance();
        }

        public void leave() {
            milliSleep(1000);
            System.out.printf("%s 离开！\n", name);


            phaser.arriveAndAwaitAdvance();
        }

        private void hug() {
            if (name.equals("新郎") || name.equals("新娘")) {
                milliSleep(1000);
                System.out.printf("%s 洞房！\n", name);
                phaser.arriveAndAwaitAdvance();
            } else {
                phaser.arriveAndDeregister();
                //phaser.register()
            }
        }

        @Override
        public void run() {
            arrive();


            eat();


            leave();


            hug();

        }
    }
}
