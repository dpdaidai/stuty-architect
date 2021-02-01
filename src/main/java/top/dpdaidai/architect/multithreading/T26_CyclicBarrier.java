package top.dpdaidai.architect.multithreading;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 *
 * 转载 : https://www.cnblogs.com/dolphin0520/p/3920397.html
 *
 * 　字面意思回环栅栏，通过它可以实现让一组线程等待至某个状态之后再全部同时执行。
 *   叫做回环是因为当所有等待线程都被释放以后，CyclicBarrier可以被重用。
 *   我们暂且把这个状态就叫做barrier，当调用await()方法之后，线程就处于barrier了。
 *
 *   特点 :
 *      1   让一组线程分别执行各自得任务, 任务完成后各自进入await状态
 *      2   当他们分别完成后, 结束await状态, 再分别执行后续任务.
 *      3   可以为 CyclicBarrier 提供Runnable参数, 当所有线程都到达barrier状态后，会选择线程去执行Runnable, 执行Runnable 会阻塞所有线程, 直到执行结束, await才算是解除
 *      4   可在await方法中, 指定等待时间, 到达时间后, 如果有线程未完成任务, 那么已完成任务得线程会抛出一个TimeoutException异常, 其余线程抛出 BrokenBarrierException, 然后所有线程继续执行
 *      5   CyclicBarrier是可以重复使用
 *
 * @Author chenpantao
 * @Date 2/1/21 10:37 PM
 * @Version 1.0
 */
public class T26_CyclicBarrier {

    public static void main(String[] args) throws InterruptedException {
        // 特点1, 2, 3
//        int number = 4;
//        CyclicBarrier cyclicBarrier = new CyclicBarrier(number, new Runnable() {
//            @Override
//            public void run() {
//                System.out.println("当前线程" + Thread.currentThread().getName());
//                try {
//                    TimeUnit.SECONDS.sleep(2);   // 这里会导致所有线程阻塞
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//        for (int i = 0; i < number; i++) {
//            TimeUnit.SECONDS.sleep(1);
//            new Writer(cyclicBarrier, "t" + i).start();
//        }

//        线程t0正在写入数据...
//        线程t0写入数据完毕，等待其他线程写入完毕
//        线程t1正在写入数据...
//        线程t1写入数据完毕，等待其他线程写入完毕
//        线程t2正在写入数据...
//        线程t2写入数据完毕，等待其他线程写入完毕
//        线程t3正在写入数据...
//        线程t3写入数据完毕，等待其他线程写入完毕
//        当前线程t3
//        t1 线程写入完毕，继续处理其他任务...
//        t0 线程写入完毕，继续处理其他任务...
//        t2 线程写入完毕，继续处理其他任务...
//        t3 线程写入完毕，继续处理其他任务...

        //特点4
        int number2 = 4;
        CyclicBarrier barrier = new CyclicBarrier(number2);

        for (int i = 0; i < number2; i++) {
            if (i < number2 - 1)
                new Writer(barrier, "t" + i).start();
            else {
                try {
                    TimeUnit.SECONDS.sleep(3);

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                new Writer(barrier, "t" + i).start();
            }
        }


//        线程t0正在写入数据...
//        线程t1正在写入数据...
//        线程t2正在写入数据...
//        线程t0写入数据完毕，等待其他线程写入完毕
//        线程t2写入数据完毕，等待其他线程写入完毕
//        线程t1写入数据完毕，等待其他线程写入完毕
//        t2java.util.concurrent.TimeoutException
//        t2 线程写入完毕，继续处理其他任务...
//        t0java.util.concurrent.BrokenBarrierException
//        t0 线程写入完毕，继续处理其他任务...
//        t1java.util.concurrent.BrokenBarrierException
//        t1 线程写入完毕，继续处理其他任务...
//        线程t3正在写入数据...
//        线程t3写入数据完毕，等待其他线程写入完毕
//        t3java.util.concurrent.BrokenBarrierException
//        t3 线程写入完毕，继续处理其他任务...

        //特点5 , 可重复使用
//        TimeUnit.SECONDS.sleep(5);
//        for (int i = 0; i < number2; i++) {
//            if (i < number2 - 1)
//                new Writer(barrier, "t" + i).start();
//            else {
//                try {
//                    TimeUnit.SECONDS.sleep(2);
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                new Writer(barrier, "t" + i).start();
//            }
//        }
    }

    static class Writer extends Thread {
        private CyclicBarrier cyclicBarrier;

        public Writer(CyclicBarrier cyclicBarrier, String name) {
            this.cyclicBarrier = cyclicBarrier;
            super.setName(name);
        }

        @Override
        public void run() {
            System.out.println("线程" + Thread.currentThread().getName() + "正在写入数据...");
            try {
                TimeUnit.SECONDS.sleep(1);    //以睡眠来模拟写入数据操作
                System.out.println("线程" + Thread.currentThread().getName() + "写入数据完毕，等待其他线程写入完毕");
                //特点1, 2, 3
//                cyclicBarrier.await();   // await结束后, 立刻安排线程去执行Runnable得方法, Runnable得方法执行完成后, 才算是接触await状态

                //特点4, 5
                cyclicBarrier.await(1, TimeUnit.SECONDS);

                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + e);
            } catch (BrokenBarrierException e) {
                System.out.println(Thread.currentThread().getName() + e);
            } catch (TimeoutException e) {
                System.out.println(Thread.currentThread().getName() + e);
            }
            System.out.println(Thread.currentThread().getName() + " 线程写入完毕，继续处理其他任务...");


        }


    }


}
