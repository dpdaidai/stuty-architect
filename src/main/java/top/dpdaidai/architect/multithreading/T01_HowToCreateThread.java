package top.dpdaidai.architect.multithreading;

/**
 * @Author chenpantao
 * @Date 1/7/21 11:12 PM
 * @Version 1.0
 */
public class T01_HowToCreateThread {

    /**
     *  实现线程的两种方式
     *  1  新建类继承Thread实现run方法
     *  2  新建类实现Runnable的run接口 , 并在新建Thread时将该实现类传入
     *  3  线程池新建线程也是调用的上述方法
     */

    public static void main(String[] args) {

        new MyThread().start();
        new Thread(new MyRun()).start();
        new Thread(() -> {
            System.out.println("匿名内部类的方式实现run方法");
        }).start();
    }

    static class MyThread extends Thread {
        @Override
        public void run() {
            System.out.println("继承的方式实现线程类");
        }
    }

    static class MyRun implements Runnable {

        @Override
        public void run() {
            System.out.println("接口的方式实现线程类");
        }
    }


}
