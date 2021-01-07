package top.dpdaidai.architect.multithreading;

import java.util.concurrent.TimeUnit;

/**
 * @Author chenpantao
 * @Date 1/7/21 11:05 PM
 * @Version 1.0
 */
public class T01_WhatIsThread {

    public static void main(String[] args) {

        new T1().start();

        for (int i = 0; i < 10; i++) {
            try {
                TimeUnit.MICROSECONDS.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.println("main");
        }


    }

    private static class T1 extends Thread {

        @Override
        public void run(){
            for (int i = 0; i < 10; i++) {
                try {
                    TimeUnit.MICROSECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("T1");
            }
        }

    }


}
