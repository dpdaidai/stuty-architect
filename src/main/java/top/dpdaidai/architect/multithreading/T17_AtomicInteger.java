package top.dpdaidai.architect.multithreading;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * CAS 注意点 :
 * 1   CAS有什么用 :
 *          当某个线程在执行atomic的方法时，不会被其他线程打断，线程安全
 *          而别的线程就像自旋锁一样，一直等到该方法执行完成，才由JVM从等待队列中选择一个线程执行
 * 2   实现原理 :
 *          CAS指的是现代CPU广泛支持的一种对内存中的共享数据进行操作的一种特殊指令
 *          对于值的操作，是基于底层硬件处理器提供的原子指令，保证并发时线程的安全
 * 3   CAS 伪代码 : compare and set
 *          compareAndSet(V, OldExpected, NewValue)
 *          CAS有三个参数，当前内存值V、旧的预期值OldExpected、即将更新的值NewValue，当且仅当预期值OldExpected和内存值NewValue相同时，
 *          将内存值修改为NewValue并返回true，否则什么都不做，并返回false
 * 4    竞争十分频繁不适用 :
 *          系统在硬件层面保证了CAS操作的原子性，不会锁住当前线程，它的效率是很高的。
 *          但是在并发越高的条件下，失败的次数会越多，CAS如果长时间不成功，会极大的增加CPU的开销，因此CAS不适合竞争十分频繁的场景
 * 5    单个变量的操作 :
 *          CAS只能保证一个共享变量的原子操作，对多个共享变量操作时，无法保证操作的原子性，这时就可以用锁，或者把多个共享变量合并成一个共享变量来操作。
 *          JDK提供了AtomicReference类来保证引用对象的原子性，可以把多个变量放在一个对象里来进行CAS操作
 * 6    ABA问题与版本号 :
 *          如果一个值原来是A，变成B，又变成A，那么CAS进行检查时会认为这个值没有变化，但是实际上却变化了。ABA问题的解决方法是使用版本号。
 *          在变量前面追加上版本号，每次变量更新的时候把版本号加一，那么A－B－A就变成1A－2B－3A。
 *          举例 :
 *              使用 List 来存放 Item，如果将一个 Item 从 List 中移除并释放了其内存地址，然后重新创建一个新的 Item，并将其添加至 List 中，
 *              由于优化等因素，有可能新创建的 Item 的内存地址与前面删除的 Item 的内存地址是相同的，
 *              导致指向新的 Item 的指针因此也等同于指向旧的 Item 的指针，这将引发 ABA 问题。
 *
 * 类似的实现方式 :
 *      public int incrementAndGet(AtomicInteger var) {
 *          int prev, next;
 *          do {
 *              prev = var.get();
 *              next = prev + 1;
 *          } while ( ! var.compareAndSet(prev, next));
 *          return next;
 *      }
 *
 * 1  在var.compareAndSet(prev, next) 在这个操作中，如果AtomicInteger的当前值是prev，那么就更新为next，返回true。如果AtomicInteger的当前值不是prev，就什么也不干，返回false。
 * 2  通过CAS操作并配合do ... while循环，即使其他线程修改了AtomicInteger的值，那么就重新取值, 重新计算 , 直到正确完成方法
 * 3  本例子的重点是 : var.compareAndSet 方法, 虽然没有java的synchronized锁, 但实际上它是由cpu支持并保证线程安全的方法
 *
 * @Author chenpantao
 * @Date 1/20/21 9:57 PM
 * @Version 1.0
 */
public class T17_AtomicInteger {

    AtomicInteger count = new AtomicInteger(0);

    void method() {
        for (int i = 0; i < 10000; i++) {
            count.incrementAndGet(); // 原子性的 ++count
//            count.getAndIncrement(); // 原子性的 count++;
        }
    }

    public static void main(String[] args) {
        T17_AtomicInteger t17_atomicInteger = new T17_AtomicInteger();

        ArrayList<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            threads.add(new Thread(t17_atomicInteger::method, "thread " + i));
        }

        threads.forEach(thread -> thread.start());

        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

        System.out.println("count : " + t17_atomicInteger.count.get());

    }


}
