package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * 方法级别的锁锁的是对象本身 , 可以从hashCode查看是否是同一个对象
 *
 * @Author chenpantao
 * @Date 1/12/21 11:03 PM
 * @Version 1.0
 */
public class T07_SynchronizedObject {

    synchronized
    void fatherMethod(String name){
        System.out.println(name + " father method start" + this.hashCode());

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        System.out.println(name + " father method end");

    }

    public static void main(String[] args) {
        T07_Child child01 = new T07_Child();
        new Thread(()-> child01.childMethod("child01")).start();

        new Thread(()-> child01.childMethod("child01_1")).start();

        T07_Child child02 = new T07_Child();
        new Thread(()-> child02.childMethod("child02")).start();


    }


}

class T07_Child extends T07_SynchronizedObject{

//    synchronized
    void childMethod(String name){

        System.out.println(name + " child method start");

        super.fatherMethod(name);

        System.out.println(name + " child method end");

    }


}
