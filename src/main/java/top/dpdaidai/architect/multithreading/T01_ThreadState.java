package top.dpdaidai.architect.multithreading;

/**
 * @Author chenpantao
 * @Date 1/7/21 11:54 PM
 * @Version 1.0
 */
public class T01_ThreadState {

    /**
     * 1  首先是 新建状态 ,new 之后 , 通过start()进入 Runnable状态
     * 2  Runnable 有两种情况 :
     * ready 等待执行
     * running 当线程被选中执行时 , 进入running状态
     * cpu 切片时 , 或者线程调用了yield方法 , running状态又回切换回ready状态
     * 线程执行完后 , 进入 Terminated 状态
     * <p>
     * 3  Blocked : 线程等待进入同步代码块的锁 , 获得锁后进入Runnable状态
     * 4  Waiting : 线程调用 wait(), join(), LockSupport.park()方法进入waiting状态
     * 线程调用notify(), notifyAll(), LockSupport.unpark() 方法后进入Runnable状态
     * 5  TimeWaiting :  Thread.sleep(time), o.wait(), t.join(time),
     * LockSupport.parkNanos(), LockSupport.parkUntil()
     * 后进去TimeWaiting状态 , 时间结束后 , 进入Runnable状态
     * 6  JVM 中的线程和操作系统的线程不一定是一一对应的 , 这要看JVM 是如何实现的
     * HOT SPOT 内的线程和操作系统的线程是一一对应的.
     * <p>
     * 7  线程的stop() 方法很粗暴, 有可能会造成线程状态不一致 . 建议等待线程正常结束 , 不要使用该方法 .
     * 8  线程的interrupt方法 , 在工程中也很少使用 .
     */

    static class MyThread extends Thread {
        @Override
        public void run() {
            for (int i = 0; i < 10; i++) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println(Thread.currentThread().getName()
                        + " - " + "Thread running" + i);
            }
        }
    }

    public static void main(String[] args) {

        MyThread myThread = new MyThread();
        System.out.println(myThread.getState());  //NEW
        myThread.start();
        System.out.println(myThread.getState());  //RUNNABLE

        try {
            myThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println(myThread.getState());   //TERMINATED


    }

}
