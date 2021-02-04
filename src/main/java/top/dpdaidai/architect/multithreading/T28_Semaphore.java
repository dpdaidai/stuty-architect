package top.dpdaidai.architect.multithreading;

import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * Semaphore翻译成字面意思为 信号量，Semaphore可以控同时访问的线程个数，通过 acquire() 获取一个许可，如果没有就等待，而 release() 释放一个许可
 * 转载自 https://www.cnblogs.com/dolphin0520/p/3920397.html
 *
 * Semaphore其实和锁有点类似，它一般用于控制对某组资源的访问权限。
 * 锁限制只能有一个线程访问资源 , 而Semaphore可以控制访问线程的数量
 *
 * 构造器
 *      public Semaphore(int permits)
 *      public Semaphore(int permits, boolean fair) // fair表示是否公平
 * 方法 :
 *      public void acquire() throws InterruptedException {  }     //获取一个许可
 *      public void acquire(int permits) throws InterruptedException { }    //获取permits个许可
 *      public void release() { }          //释放一个许可
 *      public void release(int permits) { }    //释放permits个许可
 *
 *      public boolean tryAcquire() { };    //尝试获取一个许可，若获取成功，则立即返回true，若获取失败，则立即返回false
 *      public boolean tryAcquire(long timeout, TimeUnit unit) throws InterruptedException { };  //尝试获取一个许可，若在指定的时间内获取成功，则立即返回true，否则则立即返回false
 *      public boolean tryAcquire(int permits) { }; //尝试获取permits个许可，若获取成功，则立即返回true，若获取失败，则立即返回false
 *      public boolean tryAcquire(int permits, long timeout, TimeUnit unit) throws InterruptedException { }; //尝试获取permits个许可，若在指定的时间内获取成功，则立即返回true，否则则立即返回false
 *
 * @Author chenpantao
 * @Date 2/4/21 11:12 PM
 * @Version 1.0
 */
public class T28_Semaphore {

    public static void main(String[] args) {
        //　假若一个工厂有5台机器，但是有8个工人，一台机器同时只能被一个工人使用，只有使用完了，其他工人才能继续使用。那么我们就可以通过Semaphore来实现：
        int number = 8;
        Semaphore semaphore = new Semaphore(3);
        for (int i = 0; i < number; i++) {
            new Worker(i, semaphore).start();
        }

//        工人2占用一个机器
//        工人0占用一个机器
//        工人1占用一个机器
//        工人2释放机器
//        工人1释放机器
//        工人0释放机器
//        工人4占用一个机器
//        工人5占用一个机器
//        工人3占用一个机器
//        工人3释放机器
//        工人4释放机器
//        工人5释放机器
//        工人7占用一个机器
//        工人6占用一个机器
//        工人6释放机器
//        工人7释放机器

    }

    static class Worker extends Thread {
        private int number;
        private Semaphore semaphore;

        public Worker(int number, Semaphore semaphore) {
            this.number = number;
            this.semaphore = semaphore;
        }

        @Override
        public void run() {
            try {
                semaphore.acquire();
                TimeUnit.MILLISECONDS.sleep(50);
                System.out.println("工人" + this.number + "占用一个机器");
                TimeUnit.SECONDS.sleep(1);
                System.out.println("工人" + this.number + "释放机器");
                TimeUnit.MILLISECONDS.sleep(50);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                semaphore.release();
            }
        }

    }

}
