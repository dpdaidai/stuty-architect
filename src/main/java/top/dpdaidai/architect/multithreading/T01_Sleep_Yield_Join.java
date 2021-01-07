package top.dpdaidai.architect.multithreading;

/**
 * @Author chenpantao
 * @Date 1/7/21 11:25 PM
 * @Version 1.0
 */
public class T01_Sleep_Yield_Join {

    public static void main(String[] args) {
//        testSleep();
//        testYield();
        testJoin();
    }


    static void testSleep() {
        /**
         * 当前线程sleep时 , cpu会去干别的线程的活 .
         * sleep 结束线程回到 '就绪' 状态
         */
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                try {
                    System.out.println(
                            Thread.currentThread().getName());
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        }).start();

    }

    static void testYield() {
        /**
         * 当前线程调用yield 方法后 , 该线程会进入等待队列 (就绪状态) .
         * 表示本线程并不会长久离开 , 只是稍微让出一下cpu .
         */
        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "-" + "A" + i);
                if (i % 10 == 0) Thread.yield();
            }
        }).start();

        new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "-" + "B" + i);
                if (i % 10 == 0) Thread.yield();
            }
        }).start();
    }


    static void testJoin() {
        /**
         * 当前线程T1调用 T2的 join 方法 , 则cpu会先去执行T2 ,
         * 等T2执行完后 , 再回来执行T1
         */

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "-" + "T1 running" + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });

        Thread t2 = new Thread(() -> {
            System.out.println(Thread.currentThread().getName()
                    + "-" + "T2 start");
            try {
                t1.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            for (int i = 0; i < 100; i++) {
                System.out.println(Thread.currentThread().getName()
                        + "-" + "T2 running" + i);
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println(Thread.currentThread().getName()
                    + "-" + "T2 end");
        });

        t1.start();
        t2.start();
    }

}
