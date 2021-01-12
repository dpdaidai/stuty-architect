package top.dpdaidai.architect.multithreading;

/**
 * @Author chenpantao
 * @Date 1/11/21 8:05 PM
 * @Version 1.0
 */
public class T03_Thread implements Runnable{

    private int count = 100;

    public
//    synchronized
    void run(){

        count--;
        System.out.println(Thread.currentThread().getName() + " count = " + count);

    }

    public static void main(String[] args){

        /**
         * 1  这个示例可以看到新建的线程都共用T02_Thread的count
         * 2  在run这个方法上不增加 synchronized 关键字有可能导致 count 变量的脏读情况 , 但是没能在println()中实际观察到
         *
         */
        T03_Thread t03_thread = new T03_Thread();
        for (int i = 0; i < 100; i++) {
            new Thread(t03_thread, "THREAD + " + i).start();
        }

    }

}
